package com.wyett.dubbo.zk.service;

import com.wyett.dubbo.zk.dto.User;

/**
 * @author : wyettLei
 * @date : Created in 2019/12/10 17:05
 * @description: TODO
 */

public interface UserService {
    User getUser(Integer id);
}
