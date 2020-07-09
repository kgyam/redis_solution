package com.kgyam.redis_solution.domain;

import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class User {

    private String id;
    private String name;
    private String phone;

    public User() {
        id = UUID.randomUUID().toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getPhoneMD5() throws NoSuchAlgorithmException {
        if (StringUtils.isBlank(phone)) {
            return "";
        }
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(this.phone.getBytes());
        return new BigInteger(1, md.digest()).toString(16);
    }
}
