package com.harry.demo;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhouhong
 * @version 1.0
 * @title: SynchronousQueueTest
 * @description: TODO
 * @date 2019/8/29 10:01
 */
public class SynchronousQueueTest {

    public static void main(String[] args) throws InterruptedException {

        test1();

    }

    public static void test1() {
        // SynchronousQueue 互斥使用场景
        // SynchronousQueue put() 完成之后，必须被其他线程 take()
        // capacity == 0 , 又允许插入(put) 一个元素
        // offer 方法无效，add 方法抛出异常
        BlockingQueue<Integer> sQueue = new SynchronousQueue<>();
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.execute(() -> { // 写线程
            try {
                // 必须要有 put，不能用 offer
                // BlockingQueue 尽可能用 put，避免使用 offer，最好不要用 add
                // sQueue.offer(1); // 如果 SynchronousQueue 被其他线程调用 take() 方法的话，会发生死锁
                sQueue.put(1);
                System.out.println("put finish...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executorService.execute(() -> { // 读线程
            try {
                System.out.println("take finish..."+sQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

//        executorService.awaitTermination(10, TimeUnit.MICROSECONDS);

        executorService.shutdown();
    }

}
