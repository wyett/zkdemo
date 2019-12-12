package com.wyett.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wyett.common.dto.DbRole;
import com.wyett.common.dto.MySQLInstance;
import com.wyett.common.services.impl.MySQLServiceImpl;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author : wyettLei
 * @date : Created in 2019/12/11 18:58
 * @description: TODO
 */

public class ZkServerUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ZkServerUtil.class);

    // static
    private static ZkClient zkClient;
    private static final String zkServer = "192.168.100.1";
    private static final String rootPath = "/distributedjob";
    private static final int CONN_TIMEOUT = 10000;
    private static final int SESSION_TIMEOUT = 5000;
    private static ZkServerUtil zkServerUtil;

    private static ZkServerUtil getInstance() {
        if (zkServerUtil == null) {
            zkServerUtil = new ZkServerUtil();
        }
        return zkServerUtil;
    }

    public ZkServerUtil() {
        zkClient = new ZkClient(zkServer, SESSION_TIMEOUT, CONN_TIMEOUT);
        buildRoot();

        //
        for (String s : getClusterPathAsList()) {
            initMaster(s);
            initListener(s);
        }
    }

    // child nodes in the same cluster
    private Map<String, MySQLInstance> msm = new HashMap<>();
//    private ExecutorService executors = Executors.newFixedThreadPool(2);

    /**
     * create root node
     */
    private void buildRoot() {
        if (!zkClient.exists(rootPath)) {
            zkClient.createPersistent(rootPath);
        }
    }

    /**
     * return all child path under root path
     * @return
     */
    private List<String> getClusterPathAsList() {
        List<String> ls = zkClient.getChildren(rootPath).stream()
                .map(p -> rootPath + "/" + p)
                .collect(Collectors.toList());
        return ls;
    }

    private void initMaster(String clusterNode) {
        // get node info under clusterNode
        msm = zkClient.getChildren(clusterNode).stream()
                .map(p -> clusterNode + "/" + p)
                .collect(Collectors.toMap(p -> p, p -> convert(zkClient.readData(p))));

        // check if exists master
        Boolean existsMaster = msm.values().stream()
                .map(p -> p.getRole() == DbRole.MASTER)
                .anyMatch(p -> p.equals(true));
        if (!existsMaster) {
            doElection(msm);
            LOG.info("do election on cluster " + clusterNode);
        }
    }

    private void initListener(String clusterNode) {
        zkClient.unsubscribeAll();
        zkClient.getChildren(clusterNode).stream()
                .map(p -> clusterNode + "/" + p)
                .forEach(p -> zkClient.subscribeDataChanges(p, new DataChange()));
//        zkClient.subscribeChildChanges(clusterNode, ((parentPath, currentChilds) -> doElection()));
//        zkClient.subscribeChildChanges(clusterNode, new DataChange());
//        zkClient.subscribeChildChanges(rootPath, ((parentPath, currentChilds) -> doElection()));
    }

    /**
     * do election. get the smallest path name, and update dbrole
     * @param m
     */
    private void doElection(Map<String, MySQLInstance> m) {
        if (m.values().stream().anyMatch(p -> DbRole.MASTER.equals(p.getRole()))) {
            return;
        }

        m.keySet().stream().sorted().findFirst()
                .ifPresent(p -> {
                    MySQLInstance mi = convert(zkClient.readData(p));
                    mi.setRole(DbRole.MASTER);
                    System.out.println(mi.toString());

                    try {
                        zkClient.writeData(p, new ObjectMapper().writeValueAsString(mi));
                    } catch (JsonProcessingException e) {
//                        throw new RuntimeException(e.getMessage());
                        LOG.info(e.getMessage());
                    }
                    LOG.info("node " + p + " was elected to be master");
                });
    }


    /**
     * convert string into MySQLIntance object
     * @param json
     * @return
     */
    public MySQLInstance convert(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, MySQLInstance.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    private class DataChange implements IZkDataListener {

        @Override
        public void handleDataChange(String dataPath, Object data) throws Exception {
            MySQLInstance mi = convert((String)data);
            msm.put(dataPath, mi);
            if (msm.size() > 0) {
                doElection(msm);
            }
        }

        @Override
        public void handleDataDeleted(String dataPath) throws Exception {
            MySQLInstance cm = msm.get(dataPath);
            if (cm.getRole() == DbRole.MASTER) {
                msm.remove(dataPath);
                if (msm.size() > 0) {
                    doElection(msm);
                }
            }
        }

        /*@Override
        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
            Map<String, MySQLInstance> msm = currentChilds.stream()
                    .map(p -> parentPath + "/" + p)
                    .collect(Collectors.toMap(p -> p, p -> convert(zkClient.readData(p))));

            if (currentChilds.size() > 0) {
                doElection(msm);
            }
        }*/
    }
}


