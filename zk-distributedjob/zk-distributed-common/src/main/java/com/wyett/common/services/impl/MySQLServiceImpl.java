package com.wyett.common.services.impl;


import com.wyett.common.dto.MySQLInstance;
import com.wyett.common.services.MySQLService;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author : wyettLei
 * @date : Created in 2019/12/11 11:57
 * @description: TODO
 */

public class MySQLServiceImpl implements MySQLService {

    private MySQLInstance mI;

    private String getLocalIp() {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return inetAddress.getHostAddress();
    }

    // simple implements

    @Override
    public MySQLInstance getMySQL() {
        MySQLInstance mi = new MySQLInstance();
        // fixed
        mi.setClusterId(100000);
        mi.setDbname("wyett");
        mi.setPort(3306);
        mi.setVersion("mariadb 10.0.16");
        mi.setWriteVip(null);
        mi.setReadVip(null);
        mi.setRole(null);

        // variable
        mi.setHostip(getLocalIp());

        return mi;
    }
}
