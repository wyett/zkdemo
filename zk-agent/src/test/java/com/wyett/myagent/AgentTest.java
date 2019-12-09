package com.wyett.myagent;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author : wyettLei
 * @date : Created in 2019/12/9 17:59
 * @description: TODO
 */

public class AgentTest {
    @Test
    @Ignore
    public void initTest() {
        Agent.premain(null, null);
        runCPU(2);
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void runCPU(int count) {
        for (int i = 0; i < count; i++) {
            new Thread(() -> {
                while (true) {
                    long bac = 1000000;
                    bac = bac >> 1;
                }
            }).start();
        }
    }
}
