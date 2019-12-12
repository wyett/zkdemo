package com.wyett.agent;

import org.junit.Test;

/**
 * @author : wyettLei
 * @date : Created in 2019/12/11 17:25
 * @description: TODO
 */

public class ZkUtilTest {
    @Test
    public void init() {
        ZkUtil.premain(null, null);
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
