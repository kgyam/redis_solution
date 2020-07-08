package com.kgyam.redis_solution.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.sql.Time;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class RedisService implements IRedisService {

    @Autowired
    public StringRedisTemplate redisTemplate;

    /**
     * 击穿方案 & 穿透 & 分布式锁
     *
     * @param key
     * @return
     */
    @Override
    public String get(String key) {

        String custid = "CUSTID_123";
        Object val = redisTemplate.opsForValue().get("key");
        if (val != null) {
            return val.toString();
        }


        String lockVal = UUID.randomUUID().toString();
        try {
            if (lock("LOCK_APPLY_CREDIT_" + custid, lockVal, 30, TimeUnit.SECONDS)) {
                Object val2 = redisTemplate.opsForValue().get("key");
                if (val2 != null) {
                    return val.toString();
                }

                String data = getData(key);
                if (data != null) {
                    redisTemplate.opsForValue().set(key, data);
                } else {
                    redisTemplate.opsForValue().set(key, "");
                }
            }
        } finally {

        }


        return null;
    }


    /**
     * 获取锁
     *
     * @param key
     * @param value
     * @return
     */
    public boolean lock(String key, String value, long timeout, TimeUnit unit) {

        long nanosTimeout = unit.toNanos(timeout);
        Boolean getLock = false;
        if (nanosTimeout <= 0L) {
            return false;
        }
        final long deadline = System.nanoTime() + nanosTimeout;

        for (; ; ) {
            getLock = redisTemplate.opsForValue().setIfAbsent(key, value,
                    30, TimeUnit.SECONDS);

            if (getLock) {
                break;
            }
            nanosTimeout = deadline - System.nanoTime();
            if (nanosTimeout <= 0L) {
                return false;
            }
        }
        return getLock;
    }


    public boolean unlock(String key, String value) {

        return true;
    }


    private String getData(String key) {
        return null;
    }
}
