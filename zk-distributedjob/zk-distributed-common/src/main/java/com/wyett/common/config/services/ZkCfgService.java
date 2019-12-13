package com.wyett.common.config.services;

import com.wyett.common.config.ReadConf;

/**
 * @author : wyettLei
 * @date : Created in 2019/12/12 18:24
 * @description: TODO
 */

public interface ZkCfgService {
    @ReadConf("${zk.servers}")
    String getZkServer();

    @ReadConf("${zk.root.path}")
    String getZkRootPath();

    @ReadConf("${zk.session.timeout}")
    String getZkSessionTimeout();

    @ReadConf("${zk.connection.timeout}")
    String getZkConnTimeout();
}
