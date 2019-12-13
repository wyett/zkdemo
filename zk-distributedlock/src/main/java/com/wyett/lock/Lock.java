package com.wyett.lock;

/**
 * @author : wyettLei
 * @date : Created in 2019/12/12 20:06
 * @description: TODO
 */

public class Lock {
    private int lockId;
    private String lockPath;
    private boolean active;

    public int getLockId() {
        return lockId;
    }

    public void setLockId(int lockId) {
        this.lockId = lockId;
    }

    public String getLockPath() {
        return lockPath;
    }

    public void setLockPath(String lockPath) {
        this.lockPath = lockPath;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        active = active;
    }

    public Lock(int lockId, String lockPath) {
        this.lockId = lockId;
        this.lockPath = lockPath;
    }
}
