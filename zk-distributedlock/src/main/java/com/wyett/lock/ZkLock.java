package com.wyett.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : wyettLei
 * @date : Created in 2019/12/12 20:08
 * @description: TODO
 */

public class ZkLock {
    private static final Logger LOG = LoggerFactory.getLogger(ZkLock.class);

    private static final String zkServers = "192.168.100.1:2181";
    private static final String rootPath = "/lock";
    private static final int SESSION_TIMEOUT = 5000;
    private static final int CONN_TIMEOUT = 10000;

    private static ZkClient zkClient;
    private String curPath;
    private String myLockPath;

    public ZkLock(String myLockPath) {
        this.myLockPath = myLockPath;
        zkClient = new ZkClient(zkServers, SESSION_TIMEOUT, CONN_TIMEOUT);
        buildRoot();
        createParentLockNode();
    }

    /**
     * create root node
     */
    private void buildRoot() {
        if (!zkClient.exists(rootPath)) {
            zkClient.createPersistent(rootPath);
        }
    }

    /**
     * create lock directory
     */
    private void createParentLockNode() {
        String parentLockPath = rootPath + "/" + myLockPath;
        if (!zkClient.exists(parentLockPath)) {
            zkClient.createPersistent(parentLockPath);
        }
    }

    /**
     * create lockpath
     * @return
     */
    private Lock createLockNode(int lockId) {
        String lockPath = rootPath + "/" + myLockPath + "/" + lockId + "_";
        curPath = zkClient.createEphemeralSequential(lockPath, "w");
        return new Lock(lockId, curPath);
    }

    /**
     * get lock
     * @param lockId
     * @param timeout
     * @return
     */
    public Lock lock(int lockId, long timeout) {
        Lock lockNode = createLockNode(lockId);
        lockNode = tryActiveLock(lockNode);
        if (!lockNode.isActive()) {
            try {
                synchronized (lockNode) {
                    lockNode.wait(timeout);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
        }
        if (!lockNode.isActive()) {
            LOG.info("wait for locking time out");
        }
        return lockNode;
    }

    /**
     * release lock
     * @param lock
     */
    public void unlock(Lock lock) {
        if (lock.isActive()) {
            zkClient.delete(lock.getLockPath());
        }
    }

    /**
     * set lock to active
     * @param lockNode
     * @return
     */
    private Lock tryActiveLock(Lock lockNode) {
        // get all lock node
        List<String> ls = zkClient.getChildren(rootPath + "/" + myLockPath)
                .stream()
                .map(p -> rootPath + "/" + myLockPath + "/" + p)
                .sorted()
                .collect(Collectors.toList());

        // get first node
        String firstLockNode = ls.size() > 0 ? ls.get(0): null;
        if (firstLockNode == null) {
            return lockNode;
        }

        if(lockNode.getLockPath().equals(firstLockNode)) {
            // first lock node compares to current lock path
            lockNode.setActive(true);
        } else {
            //get the prev node
            String prevLockNode = ls.get(ls.indexOf(lockNode.getLockPath()) - 1);
            //add listener on the prev node
            zkClient.subscribeDataChanges(prevLockNode, new IZkDataListener() {
                @Override
                public void handleDataChange(String dataPath, Object data) throws Exception {

                }

                @Override
                public void handleDataDeleted(String dataPath) throws Exception {
                    System.out.println("deleting " + dataPath);
                    Lock lock1 = tryActiveLock(lockNode);
                    synchronized (lockNode) {
                        if (lockNode.isActive()) {
                            lockNode.notify();
                        }
                    }
                    zkClient.subscribeDataChanges(prevLockNode, this);
                }
            });
        }
        return lockNode;
    }
}
