package com.kgyam.redis_solution.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RedisService implements IRedisService {

    @Autowired
    public StringRedisTemplate redisTemplate;

    @Autowired
    private DefaultRedisScript redisScript;

    /**
     * 击穿方案 & 穿透 & 分布式锁
     *
     * @param key
     * @return
     */
    @Override
    public String get(String key) {
        String val = "";
        String custid = "CUSTID_123";
        val = redisTemplate.opsForValue().get("key");
        if (StringUtils.isNotBlank(val)) {
            return val;
        }


        String lockVal = UUID.randomUUID().toString();
        try {
            if (lock("LOCK_APPLY_CREDIT_" + custid, lockVal, 30, TimeUnit.SECONDS)) {

                val = redisTemplate.opsForValue().get("key");
                if (StringUtils.isNotBlank(val)) {
                    return val;
                }

                val = getData(key);
                /*
                假如数据可从数据库读取,将其设置到缓存
                如果没有，视为穿透，同样将空值返回设置到缓存
                日后数据库存放了这个空值的key对应的真实数据,在插入数据的同时讲缓存到redis中
                 */
                if (val != null) {
                    redisTemplate.opsForValue().set(key, val);
                } else {
                    redisTemplate.opsForValue().set(key, "");
                }
            }
            return val;
        } catch (Exception e) {
            throw new RuntimeException(e.getLocalizedMessage());
        } finally {
            unlock(key, lockVal);
        }
    }


    /**
     * 获取锁，单体redis分布式锁实现。
     * 优点：实现简单
     * 缺点：会出现单点故障问题，和网络分区问题
     *
     * @param key
     * @param value
     * @return
     */
    public boolean lock(String key, String value, long timeout, TimeUnit unit) {

        long nanosTimeout = unit.toNanos(timeout);
        Boolean lock = false;
        if (nanosTimeout <= 0L) {
            return false;
        }
        final long deadline = System.nanoTime() + nanosTimeout;

        for (; ; ) {
            lock = redisTemplate.opsForValue().setIfAbsent(key, value,
                    30, TimeUnit.SECONDS);

            if (lock) {
                return lock;
            }
            nanosTimeout = deadline - System.nanoTime();
            if (nanosTimeout <= 0L) {
                return false;
            }
        }
    }


    /**
     * 使用这种方式释放锁可以避免删除别的客户端获取成功的锁
     * 使用lua脚本原因是 对于redis是一个原子操作，且执行效率高
     *
     * @param key
     * @param value
     * @return
     */
    public Boolean unlock(String key, String value) {
        String luaScript = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                "    return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "    return 0\n" +
                "end";
        redisScript.setScriptText(luaScript);
        Long result = (Long) redisTemplate.execute(redisScript, Arrays.asList(key), value);
        if (result == 0L) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }


    /**
     * 模拟读取db
     *
     * @param key
     * @return
     */
    private String getData(String key) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(500);
        return "data";
    }
}
