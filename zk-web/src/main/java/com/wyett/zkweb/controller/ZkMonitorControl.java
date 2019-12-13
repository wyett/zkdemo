package com.wyett.zkweb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wyett.myagent.OsBean;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : wyettLei
 * @date : Created in 2019/12/10 11:26
 * @description: TODO
 */

@RestController
public class ZkMonitorControl implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkMonitorControl.class);

    @Value("${192.168.100.1:2181}")
    private String zkServer;
    private ZkClient zkClient;
    private static final String rootPath = "/monitor";
    Map<Object, OsBean> mos = new HashMap<>();

    @RequestMapping("/list")
    public String list(Model model) {
        model.addAttribute("items", getCurrentOsBean());
        return "list";
    }

    private List<OsBean> getCurrentOsBean() {
        List<OsBean> items = zkClient.getChildren(rootPath).stream()
                .map(p -> rootPath + "/" + p)
                .map(p -> convert(zkClient.readData(p)))
                .collect(Collectors.toList());
        return items;
    }

    private OsBean convert(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, OsBean.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        zkClient = new ZkClient(zkServer, 5000, 10000);
        initSubscribeListener();
    }

    private void initSubscribeListener() {
        zkClient.unsubscribeAll();
        zkClient.getChildren(rootPath).stream()
                .map(p -> rootPath + "/" + p)
                .forEach(p -> {
                    zkClient.subscribeDataChanges(p, new DataChanges());
                });
        zkClient.subscribeChildChanges(rootPath, ((parentPath, currentChilds) -> initSubscribeListener()));
    }

    private class DataChanges implements IZkDataListener {
        @Override
        public void handleDataChange(String dataPath, Object data) throws Exception {
            OsBean osBean = convert((String) data);
            mos.put(dataPath, osBean);
            doFilter(osBean);
        }

        @Override
        public void handleDataDeleted(String dataPath) throws Exception {
            if (mos.containsKey(dataPath)) {
                OsBean osBean = mos.get(dataPath);
                System.out.println("service removed " + osBean);
                mos.remove(dataPath);
            }
        }
    }

    private void doFilter(OsBean osBean) {
        if (osBean.getCpu() > 10) {
            LOGGER.info("cpu used greater than 10%");
        }
    }
}
