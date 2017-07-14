package com.zhoubo.controller;

import com.zhoubo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zhoubo on 2017/2/20.
 */
@RequestMapping(value = "/test")
@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @RequestMapping(value = "/test")
    public Object test(HttpServletRequest request, HttpServletResponse response) {
        return testService.getAll();
    }
}
