package com.wyett.dubbo.zk.service.impl;

import com.wyett.dubbo.zk.dto.User;
import com.wyett.dubbo.zk.service.UserService;

import java.lang.management.ManagementFactory;
import java.util.Date;

/**
 * @author : wyettLei
 * @date : Created in 2019/12/10 17:05
 * @description: TODO
 */

public class UserServiceImpl implements UserService {
    private  int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public User getUser(Integer id) {
        User user = new User();

        user.setId(id);
        user.setName(String.format(ManagementFactory.getRuntimeMXBean().getName()));
        user.setBirthday(new Date());
        user.setPort(port);

        if (port == 20880) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return user;
    }
}
