package com.kgyam.redis_solution.service;

import com.kgyam.redis_solution.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class SMSServiceImpl implements ISMSService {
    @Autowired
    public StringRedisTemplate redisTemplate;


    /**
     * 根据md5_标识查询redis是否存在限制器
     * 如果不存在就创建时长为10分钟的限制器
     * <p>
     * 如果存在判断是否超过限制
     *
     * @param userId
     * @return
     * @throws NoSuchAlgorithmException
     */
    @Override
    public String getCode(String userId) throws NoSuchAlgorithmException {
         /*
        记录手机获取code的次数
        设定10min内不能获取超过5次
         */
        User user = getUser(userId);
        if (user == null) {
            return "";
        }
        String md = user.getPhoneMD5();

        String str = redisTemplate.opsForValue().get(md + "_times");
        if (StringUtils.isBlank(str)) {
            //为空设置
            redisTemplate.opsForValue().set(md + "_times", "1", 10, TimeUnit.MINUTES);
            String code = getCode();
            sendSMS(code);
        } else {
            Long times = Long.valueOf(str);
            //不为空做判断
            //  Long times = redisTemplate.opsForValue().increment(md + "_times");
            if (times > 5) {
                return "";
            }

             /*
        获取code,有效时间为5min
         */
            String code = getCode();
            sendSMS(code);
            redisTemplate.opsForValue().increment(md + "_times");
            redisTemplate.opsForValue().set(md, code, 5, TimeUnit.MINUTES);

        }

        return "success";
    }


    private User getUser(String userId) {
        User user = new User();
        user.setPhone("13456789996");
        return user;
    }

    private String getCode() {
        return "123456";
    }


    private void sendSMS(String code) {
        //线程池发送
    }


    @Override
    public Boolean verfication(String userId, String code) throws NoSuchAlgorithmException {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(code)) {
            return Boolean.FALSE;
        }
        User user = getUser(userId);
        String mdStr = user.getPhoneMD5();
        String verifyCode = redisTemplate.opsForValue().get(mdStr);
        if (code.equals(verifyCode)) {
            redisTemplate.delete(mdStr);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
