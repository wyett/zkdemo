package com.wyett.zkcli.v3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : wyettLei
 * @date : Created in 2019/12/9 15:08
 * @description: TODO
 */

public class Agent {
    private static final Logger LOGGER = LoggerFactory.getLogger(Agent.class);

    private static String zkConn = "10.18.56.30:2181, 10.18.57.168:2181, 10.18.56.149:2181";
    private static final String ROOT_PATH = "/monitor";
    private static final int CONN_TIMEOUT = 10000;
    private static final int SESSION_TIMEOUT = 5000;

    // presistent and sequence
    private static final String SERVICE_PATH = ROOT_PATH + "/service";
    // "/monitor/service0000000001"
    private String nodePath;

    private ZkClient zkClient;
    private Thread stateThread;

    // agent singleton
    private Agent() {}
    private static Agent agent = new Agent();
    public static Agent getInstance() {
        return agent;
    }

    /**
     * 获取数据监控
     * @param args
     * @param instrumentation
     */
    public void premain(String args, Instrumentation instrumentation) {

    }

    public void init() {
        zkClient = new ZkClient(zkConn, SESSION_TIMEOUT, CONN_TIMEOUT);

        //create root node
        createRootNode();

        // create temp node
        createServiceNode();

        // thread
        stateThread = new Thread(() -> {
            while(true) {
                updateServiceNode();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Thread_osInfo");
        stateThread.setDaemon(true);
        stateThread.start();
    }

    /**
     * create root node
     */
    public void createRootNode() {
        if (!zkClient.exists(ROOT_PATH)) {
            List<ACL> list = new ArrayList<>();
            int perm = ZooDefs.Perms.CREATE | ZooDefs.Perms.DELETE;
            ACL acl = new ACL(perm, new Id("world", "anyone"));
            zkClient.createPersistent(ROOT_PATH, "monitor root node", list);
        }
    }

    /**
     * create service node
     */
    public void createServiceNode() {
        List<ACL> aclList = new ArrayList<>();
        int perm = ZooDefs.Perms.READ | ZooDefs.Perms.WRITE;
        ACL acl = new ACL(perm, new Id("world", "anyone"));
        nodePath = zkClient.createEphemeralSequential(SERVICE_PATH, getOsInfo(), aclList);
    }

    /**
     * update service node
     */
    public void updateServiceNode() {
        zkClient.writeData(nodePath, getOsInfo());
    }

    /**
     * get local host ip
     * @return
     */
    private String getLocalIp() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return addr.getHostAddress();
    }

    /**
     * get os info
     * @return
     */
    public String getOsInfo() {
        OsBean osBean = new OsBean();
        osBean.lastUpdateTime = System.currentTimeMillis();
        osBean.ip = getLocalIp();
        osBean.cpu = CPUMonitorCalc.getInstance().getProcessCpu();
        MemoryUsage memoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        osBean.usedMemorySize = memoryUsage.getUsed() / 1024 / 1024;
        osBean.usableMemorySize = memoryUsage.getMax() / 1024 / 1024;
        osBean.pid = ManagementFactory.getRuntimeMXBean().getName();

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(osBean);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
