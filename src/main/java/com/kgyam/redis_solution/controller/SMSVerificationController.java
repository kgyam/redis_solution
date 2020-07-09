package com.kgyam.redis_solution.controller;

import com.kgyam.redis_solution.service.ISMSService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/sms/")
public class SMSVerificationController {

    @Resource
    private ISMSService service;

    @GetMapping("code/{user_id}")
    public String getCode(@PathVariable String userId) {
        if (StringUtils.isBlank(userId)) {
            return "fail";
        }
        service.getCode(userId);
        return "";
    }
}
