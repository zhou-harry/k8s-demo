package com.harry.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhouhong
 * @version 1.0
 * @title: DemoController
 * @description: TODO
 * @date 2019/7/23 11:10++
 */
@RestController
public class DemoController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.application.name}")
    private String applicationName;

    @GetMapping("index")
    public String index() {
        logger.info("后台服务 {} 启动成功！", applicationName);
        return "k8s deploy intergration was success！the application name is ："+applicationName;
    }

}
