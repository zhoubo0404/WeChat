package com.zhoubo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhoubo.model.SkinType;
import com.zhoubo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by zhoubo on 2017/2/20.
 */
@RequestMapping(value = "/test")
@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @RequestMapping(value = "/test")
    public Object test(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        List<SkinType> all = testService.getAll();
        ObjectMapper objectMapper = new ObjectMapper();
        String str = objectMapper.writeValueAsString(all);
//        JSON
        return testService.getAll();
    }

    @RequestMapping(value = "/dbproxytest")
    public Object dbproxyTest(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        List<SkinType> all = testService.getAll();
        ObjectMapper objectMapper = new ObjectMapper();
        String str = objectMapper.writeValueAsString(all);
//        JSON
        return testService.getAll();
    }
}
