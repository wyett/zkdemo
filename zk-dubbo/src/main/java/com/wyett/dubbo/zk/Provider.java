package com.wyett.dubbo.zk;

import com.wyett.dubbo.zk.dto.User;
import com.wyett.dubbo.zk.service.UserService;
import com.wyett.dubbo.zk.service.impl.UserServiceImpl;
import org.apache.dubbo.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.apache.dubbo.qos.server.DubboLogo.dubbo;

/**
 * @author : wyettLei
 * @date : Created in 2019/12/10 17:25
 * @description: TODO
 */

public class Provider {

    private static final Logger LOG = LoggerFactory.getLogger(Provider.class);

    //    private UserService userService;

    public UserService buildService(String url) {
        // application config
        ApplicationConfig application = new ApplicationConfig();
        application.setName("provider-client-simple");

        // registry config
        RegistryConfig registry = new RegistryConfig();
        registry.setAddress("zookeeper://192.168.100.1:2181");

        // Reference config
        ReferenceConfig<UserService> refConfig = new ReferenceConfig<>();
        refConfig.setRegistry(registry);
        refConfig.setApplication(application);
        refConfig.setInterface(UserService.class);
//        refConfig.setUrl(url);

        return refConfig.get();
    }

    public static void main(String[] args) throws IOException {
        Provider provider = new Provider();
        UserService userService = provider.buildService("");
        String cmd = null;
        while(!(cmd = read()).equals("exit")) {
            User user = userService.getUser(Integer.parseInt((cmd)));
            System.out.println(user);
        }
    }

    /**
     * read input
     * @return
     * @throws IOException
     */
    private static String read() throws IOException {
        byte[] buff = new byte[1024];
        int size = System.in.read(buff);
        return new String(buff, 0, size).trim();
    }

}












