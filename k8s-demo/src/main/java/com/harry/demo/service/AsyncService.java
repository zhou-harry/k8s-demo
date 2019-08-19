package com.harry.demo.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 需要 @EnableAsync 启动异步调用
 * @author zhouhong
 * @version 1.0
 * @title: AsyncService
 * @description: 异步调用服务
 * @date 2019/8/19 16:26
 */
@Service
public class AsyncService {

    @Async
    public Future<String> doTask1() throws InterruptedException {
        System.out.println("do task1 start");
        TimeUnit.SECONDS.sleep(2);
        System.out.println("do task1 end");
        return new AsyncResult<>("task1 return");
    }

    @Async
    public Future<String> doTask2() throws InterruptedException {
        System.out.println("do task2 start");
        TimeUnit.SECONDS.sleep(1);
        System.out.println("do task2 end");
        return new AsyncResult<>("task2 return");
    }
}
