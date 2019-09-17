package com.harry.demo.controller;

import com.harry.demo.service.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;

/**
 * @author zhouhong
 * @version 1.0
 * @title: AsyncController
 * @description: 异步调用
 * @date 2019/8/19 16:26
 */
@RestController
public class AsyncController {

    @Autowired
    private AsyncService asyncService;

    @GetMapping("async")
    public void asyncCall() throws InterruptedException, ExecutionException {
        Future<String> task1 = asyncService.doTask1();
        Future<String> task2 = asyncService.doTask2();

        //Future类的get方法是阻塞方法，没有结果返回会一直阻塞
        System.out.println(task1.get());
        System.out.println(task2.get());
    }

}
