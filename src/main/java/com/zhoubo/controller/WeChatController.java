package com.zhoubo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhoubo on 2016/12/14.
 */
//@Controller
@RequestMapping(value = "/weChat")
@RestController
public class WeChatController {

    @RequestMapping(value = "/index")
    public String welcome() {
        return "index";
    }

    @RequestMapping(value = "/receiveWxMsg")
    public String receiveMsg(String echostr) {
        System.out.println("echostr = " +echostr);
        return "success";
    }

}
