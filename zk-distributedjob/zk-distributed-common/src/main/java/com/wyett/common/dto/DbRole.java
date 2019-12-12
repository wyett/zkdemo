package com.wyett.common.dto;

/**
 * @author : wyettLei
 * @date : Created in 2019/12/11 11:35
 * @description: TODO
 */

public enum DbRole {
    MASTER("master"), SLAVE("slave");
    private String role;
    private DbRole(String role) {
        this.role = role;
    }
}
