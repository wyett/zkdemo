package com.wyett.zkcli.v1;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author : wyettLei
 * @date : Created in 2019/12/8 17:17
 * @description: TODO
 */

public class ZkConn {

    private ZooKeeper zooKeeper;
    private static final String conn = "10.18.56.30:2181, 10.18.57.168:2181, 10.18.56.149:2181/wyettroot";
    private static final int connTimeOut = 4000;

    @Before
    public void init() throws IOException {
        zooKeeper = new ZooKeeper(conn, connTimeOut, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                System.out.println(watchedEvent.getPath());
                System.out.println(watchedEvent);
            }
        });

    }

    @Test
    public void getData() throws KeeperException, InterruptedException {
        byte[] data = zooKeeper.getData("/tuling", false, null);
        System.out.println(new String(data));
    }

    /**
     * 只监听一次
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Test
    public void getData2() throws KeeperException, InterruptedException {
        byte[] data = zooKeeper.getData("/tuling", true, null);
        System.out.println(new String(data));
        Thread.sleep(Long.MAX_VALUE);
    }

    /**
     * 多次监听
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Test
    public void getData3() throws KeeperException, InterruptedException {
        Stat stat = new Stat();
        zooKeeper.getData("/tuling", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                try {
                    zooKeeper.getData(watchedEvent.getPath(), this, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(watchedEvent.getPath());
            }
        }, stat);
        System.out.println(stat);
        Thread.sleep(Long.MAX_VALUE);
    }

    @Test
    public void getData4() throws KeeperException, InterruptedException {
        zooKeeper.getData("/tuling", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
                System.out.println(bytes);
            }
        }, "");
        Thread.sleep(Long.MAX_VALUE);
    }

    @Test
    public void getChild() throws KeeperException, InterruptedException {
        List<String> children = zooKeeper.getChildren("/tuling", false);
        children.stream().forEach(System.out::println);

    }

    @Test
    public void getChild2() throws KeeperException, InterruptedException {
        List<String> children = zooKeeper.getChildren("/tuling", event -> {
            System.out.println(event.getPath());
            try {
                zooKeeper.getChildren(event.getPath(), false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        children.stream().forEach(System.out::println);
        Thread.sleep(Long.MAX_VALUE);

    }


}
