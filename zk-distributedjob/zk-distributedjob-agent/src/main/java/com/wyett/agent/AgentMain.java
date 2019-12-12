package com.wyett.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author : wyettLei
 * @date : Created in 2019/12/11 18:55
 * @description: TODO
 */

public class AgentMain {
    private static final Logger LOG = LoggerFactory.getLogger(AgentMain.class);

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 1; i <= 3; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    ZkUtil.premain(null, null);
                }
            });
        }

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            LOG.error(e.toString());
        }
    }
}
