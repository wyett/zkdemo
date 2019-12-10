package com.wyett.dubbo.zk;

import com.wyett.dubbo.zk.service.UserService;
import com.wyett.dubbo.zk.service.impl.UserServiceImpl;
import org.apache.dubbo.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * @author : wyettLei
 * @date : Created in 2019/12/10 17:24
 * @description: TODO
 */

public class Consumer {
    private static final Logger LOG = LoggerFactory.getLogger(Consumer.class);

    private void openSession(Integer port) {
        // application config
        ApplicationConfig application = new ApplicationConfig();
        application.setName("Consumer-server-simple");

        // protocal config
        ProtocolConfig protocol = new ProtocolConfig("dubbo", port);
        protocol.setThreads(20);

        // register conf
        RegistryConfig registry = new RegistryConfig();
//        registry.setProtocol(dubbo);
        registry.setAddress("zookeeper://10.18.56.30:2181, 10.18.57.168:2181, 10.18.56.149:2181");


        // Service config
        ServiceConfig<UserService> scus = new ServiceConfig<>();
        scus.setApplication(application);
        scus.setProtocol(protocol);
        scus.setRegistry(registry);
        scus.setInterface(UserService.class);

        UserServiceImpl ref = new UserServiceImpl();
        scus.setRef(ref);

        scus.export();
        LOG.info("provider启动..端口:" + scus.getExportedUrls().get(0).getPort());
        ref.setPort(scus.getExportedUrls().get(0).getPort());

    }

    public static void main(String[] args) throws IOException {
        new Consumer().openSession(-1);
        System.in.read();
    }

}
