package com.wyett.dubbo.zk.dto;

import java.util.Date;

/**
 * @author : wyettLei
 * @date : Created in 2019/12/10 17:03
 * @description: TODO
 */

public class User {
    private Integer id;
    private String name;
    private Date birthday;
    private int port;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                ", port=" + port +
                '}';
    }
}
