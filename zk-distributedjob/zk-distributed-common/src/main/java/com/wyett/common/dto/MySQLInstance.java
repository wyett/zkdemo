package com.wyett.common.dto;

import java.io.Serializable;

/**
 * @author : wyettLei
 * @date : Created in 2019/12/11 11:31
 * @description: TODO
 */

public class MySQLInstance implements Serializable {
    private int clusterId;
    private String version;
    private String hostip;
    private String writeVip;
    private String readVip;
    private int port;
    private String dbname;
    private DbRole role;

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHostip() {
        return hostip;
    }

    public void setHostip(String hostip) {
        this.hostip = hostip;
    }

    public String getWriteVip() {
        return writeVip;
    }

    public void setWriteVip(String writeVip) {
        this.writeVip = writeVip;
    }

    public String getReadVip() {
        return readVip;
    }

    public void setReadVip(String readVip) {
        this.readVip = readVip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public DbRole getRole() {
        return role;
    }

    public void setRole(DbRole role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "MySQLInstance{" +
                "clusterId=" + clusterId +
                ", version='" + version + '\'' +
                ", hostip='" + hostip + '\'' +
                ", writeVip='" + writeVip + '\'' +
                ", readVip='" + readVip + '\'' +
                ", port=" + port +
                ", dbname='" + dbname + '\'' +
                ", role=" + role +
                '}';
    }
}
