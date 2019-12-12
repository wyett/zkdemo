package com.wyett.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wyett.common.dto.DbRole;
import com.wyett.common.dto.MySQLInstance;
import com.wyett.common.services.impl.MySQLServiceImpl;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author : wyettLei
 * @date : Created in 2019/12/11 10:47
 */

public class ZkUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ZkUtil.class);

    // static
    private static ZkClient zkClient;
    private static final String zkServer = "10.18.56.30:2181, 10.18.57.168:2181, 10.18.56.149:2181";
    private static final String rootPath = "/distributedjob";
    private static final int CONN_TIMEOUT = 10000;
    private static final int SESSION_TIMEOUT = 5000;
    private static ZkUtil zkUtil = new ZkUtil();
    private static ZkUtil getInstance() {
        return zkUtil;
    }

    // non-static
    private MySQLInstance mysqlInfo = new MySQLServiceImpl().getMySQL();
    private String childPath = rootPath + "/mysql_" + mysqlInfo.getClusterId();
    private String grandPath = childPath + "/" + mysqlInfo.getClusterId() + "_";
    private String curPath;

    private ZkUtil() {}

    public static void premain(String args, Instrumentation instrumentation) {
        LOG.info("begin to create agent...");
        ZkUtil.getInstance().init();
    }

    public void init() {
        zkClient = new ZkClient(zkServer, SESSION_TIMEOUT, CONN_TIMEOUT);

        //create root
        buildRoot();
        //create cluster node
        createClusterNode();
        //create server node
        createServerNode();
        //update per second
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    updateNode();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();

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
     * create cluster directory
     */
    private void createClusterNode() {
        if (!zkClient.exists(childPath)) {
            zkClient.createPersistent(childPath);
        }
    }

    /**
     * create temp sequese node,  at the beginning, nodes in the same cluster are all slaves;
     */
    private void createServerNode() {
        curPath = zkClient.createEphemeralSequential(grandPath, buildNodeInfo());
    }


    /**
     * build node info
     * @return
     */
    private String buildNodeInfo() {
        MySQLInstance mi = new MySQLServiceImpl().getMySQL();
        if (curPath != null && getCurRole(curPath) != null) {
            mi.setRole(getCurRole(curPath));
        }

        // serializable cast mi into string
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(mi);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * read path from data
     * @param curPath
     * @return
     */
    private DbRole getCurRole(String curPath) {
        return convert(zkClient.readData(curPath)).getRole();
    }

    /**
     * convert string into MySQLIntance
     * @param json
     * @return
     */
    private MySQLInstance convert(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, MySQLInstance.class);
        } catch (JsonProcessingException e) {
            LOG.error("conver json to MySQLInstance failed " + e.toString());
            throw new RuntimeException(e);
        }
    }

    /**
     * update node
     */
    private void updateNode() {
        zkClient.writeData(curPath, buildNodeInfo());
    }

}
