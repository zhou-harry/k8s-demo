package com.harry.demo.controller;

import com.harry.demo.jco.CustomJcoService;
import com.harry.demo.jco.JcoProperties;
import com.harry.demo.jco.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Value("${harry.username}")
    private String username;
    @Value("${harry.password}")
    private String password;

    @Autowired
    private CustomJcoService customJcoService;

    @Autowired
    private JcoProperties properties;

    @GetMapping("index")
    public String index() {
        logger.info("后台服务 {} 启动成功，后台用户：{}，密码：{}", applicationName, username, password);
        return "后台服务 "+applicationName+" 启动成功，后台用户："+username+"，密码："+password;
    }

    @GetMapping("/ping")
    public R pingCalls() {

        R r = customJcoService.pingCalls( properties.getDestName() );

        return r;

    }

}
