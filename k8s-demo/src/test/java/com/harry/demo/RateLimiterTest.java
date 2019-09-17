package com.harry.demo;

import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.io.FileUtils;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhouhong
 * @version 1.0
 * @title: RateLimiterTest
 * @description:
 * @date 2019/9/4 10:52
 */
public class RateLimiterTest {

    public static void main(String[] args) throws IOException, URISyntaxException {
        accquireWithLua();
    }

    /**
     * 基于令牌桶算法的单机版限流
     */
    public static void accquireLocal(){
        //线程池
        ExecutorService exec = Executors.newCachedThreadPool();
        //速率是每秒只有3个许可
        final RateLimiter rateLimiter = RateLimiter.create(3);

        for (int i = 0; i < 100; i++) {
            final int no = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        //获取许可
                        rateLimiter.acquire();
                        System.out.println("time:"
                                + new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date()) + "，Accessing: " + no);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
            //执行线程
            exec.execute(runnable);
        }
        //退出线程池
        exec.shutdown();
    }

    /**
     * Redis+Lua的分布式限流
     * @return
     */
    public static void accquireWithLua() throws IOException, URISyntaxException {
        final CountDownLatch latch = new CountDownLatch(1);
        for (int i = 0; i < 7; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        latch.await();
                        System.out.println("请求是否被执行："+RedisLimitRateWithLUA.accquire());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }

        latch.countDown();
    }

    /**
     * Redis+Lua的分布式限流
     *
     * 分布式限流最关键的是要将限流服务做成原子化，而解决方案可以使使用redis+lua或者nginx+lua技术进行实现，通过这两种技术可以实现的高并发和高性能。
     * 首先我们来使用redis+lua实现时间窗内某个接口的请求数限流，实现了该功能后可以改造为限流总并发/请求数和限制总资源数。Lua本身就是一种编程语言，也可以使用它实现复杂的令牌桶或漏桶算法。
     * 如下操作因是在一个lua脚本中（相当于原子操作），又因Redis是单线程模型，因此是线程安全的。
     *
     * 相比Redis事务来说，Lua脚本有以下优点
     * 减少网络开销: 不使用 Lua 的代码需要向 Redis 发送多次请求, 而脚本只需一次即可, 减少网络传输;
     * 原子操作: Redis 将整个脚本作为一个原子执行, 无需担心并发, 也就无需事务;
     * 复用: 脚本会永久保存 Redis 中, 其他客户端可继续使用.
     *
     * Lua脚本
     *
     * local key = KEYS[1] --限流KEY（一秒一个）
     * local limit = tonumber(ARGV[1]) --限流大小
     * local current = tonumber(redis.call('get', key) or "0")
     * if current + 1 > limit then --如果超出限流大小
     *     return 0
     * else --请求数+1，并设置2秒过期
     *     redis.call("INCRBY", key,"1")
     *     redis.call("expire", key,"2")
     * end
     * return 1
     *
     * IP限流Lua脚本
     *
     * local key = "rate.limit:" .. KEYS[1]
     * local limit = tonumber(ARGV[1])
     * local expire_time = ARGV[2]
     *
     * local is_exists = redis.call("EXISTS", key)
     * if is_exists == 1 then
     *     if redis.call("INCR", key) > limit then
     *         return 0
     *     else
     *         return 1
     *     end
     * else
     *     redis.call("SET", key, 1)
     *     redis.call("EXPIRE", key, expire_time)
     *     return 1
     * end
     *
     * @return
     */
    public static class RedisLimitRateWithLUA {

        /**
         * Java代码传入key和最大的限制limit参数进lua脚本
         * 执行lua脚本（lua脚本判断当前key是否超过了最大限制limit）
         *
         * 如果超过，则返回0（限流）
         * 如果没超过，返回1（程序继续执行）
         * @return
         * @throws IOException
         * @throws URISyntaxException
         */
        public static boolean accquire() throws IOException, URISyntaxException {
            Jedis jedis = new Jedis("127.0.0.1");
            File luaFile = new File(RedisLimitRateWithLUA.class.getResource("/").toURI().getPath() + "limit.lua");
            String luaScript = FileUtils.readFileToString(luaFile);

            String key = "ip:" + System.currentTimeMillis()/1000; // 当前秒
            String limit = "5"; // 最大限制
            List<String> keys = new ArrayList<String>();
            keys.add(key);
            List<String> args = new ArrayList<String>();
            args.add(limit);
            Long result = (Long)(jedis.eval(luaScript, keys, args)); // 执行lua脚本，传入参数
            return result == 1;
        }
    }
}
