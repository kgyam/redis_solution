package com.kgyam.redis_solution.service;

import java.security.NoSuchAlgorithmException;

public interface ISMSService {



    String getCode(String userId) throws NoSuchAlgorithmException;

    Boolean verfication(String userId, String code) throws NoSuchAlgorithmException;
}
