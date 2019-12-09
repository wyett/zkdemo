package com.wyett.zkcli.v3;

/**
 * @author : wyettLei
 * @date : Created in 2019/12/9 16:02
 * @description: TODO
 */

public class OsBean {
    public String ip;
    public Double cpu;
    public long usedMemorySize;
    public long usableMemorySize;
    public String pid;
    public long lastUpdateTime;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Double getCpu() {
        return cpu;
    }

    public void setCpu(Double cpu) {
        this.cpu = cpu;
    }

    public long getUsedMemorySize() {
        return usedMemorySize;
    }

    public void setUsedMemorySize(long usedMemorySize) {
        this.usedMemorySize = usedMemorySize;
    }

    public long getUsableMemorySize() {
        return usableMemorySize;
    }

    public void setUsableMemorySize(long usableMemorySize) {
        this.usableMemorySize = usableMemorySize;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "OsBean{" +
                "ip='" + ip + '\'' +
                ", cpu=" + cpu +
                ", usedMemorySize=" + usedMemorySize +
                ", usableMemorySize=" + usableMemorySize +
                ", pid='" + pid + '\'' +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }
}
