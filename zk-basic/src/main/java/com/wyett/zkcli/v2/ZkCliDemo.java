package com.wyett.zkcli.v2;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : wyettLei
 * @date : Created in 2019/12/9 10:57
 * @description: with ZkClient
 */

public class ZkCliDemo {

    private static final String conn = "192.168.100.1:2181/wyettroot";
    private static final int CONN_TIME_OUT = 4000;

    private ZkClient zkClient;


    /**
     * connect zkclient
     */
    @Before
    public void connZk() {
        zkClient = new ZkClient(conn, CONN_TIME_OUT);
    }

    @Test
    public void createNode() {
        zkClient.createPersistent("/tuling/z", "zkclient");
    }

    @Test
    public void createNode2() {
        List<ACL> aclList = new ArrayList<>();
        int perm = ZooDefs.Perms.READ | ZooDefs.Perms.ADMIN;
        ACL acl = new ACL(perm, new Id("world", "anyone"));
        aclList.add(acl);
        zkClient.createPersistentSequential("/tuling/z", "zkclient persistent sequential", aclList);
    }

    @Test
    public void createNode3() {
        List<ACL> aclList = new ArrayList<>();
        int perm = ZooDefs.Perms.READ | ZooDefs.Perms.ADMIN;
        ACL acl = new ACL(perm, new Id("world", "anyone"));
        aclList.add(acl);
        zkClient.createPersistent("/tuling/z01", "zkclient persistent sequential", aclList);
    }


    @Test
    public void readData() {
        System.out.println((String)zkClient.readData("/tuling/z01", true));
    }


}
