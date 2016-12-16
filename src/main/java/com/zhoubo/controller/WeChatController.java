package com.zhoubo.controller;

import com.thoughtworks.xstream.XStream;
import com.zhoubo.model.MsgType;
import com.zhoubo.model.WeChatMessage;
import com.zhoubo.util.XStreamFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by zhoubo on 2016/12/14.
 */
//@Controller
@RequestMapping(value = "/weChat")
@RestController
public class WeChatController {
    private final static Logger log = LoggerFactory.getLogger(WeChatController.class);

    @RequestMapping(value = "/index")
    public String welcome() {
        return "index";
    }

    @RequestMapping(value = "/receiveWxMsg")
    public String receiveMsg(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getParameter("token");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        boolean isGet = request.getMethod().equals("GET");
        log.info("isGet = { }", isGet);
        System.out.println(isGet);
        if (isGet) {
            log.info("get method");
            return echostr;
        } else {
            doPost(request, response);
        }
        System.out.println("token = " + token);
        System.out.println("timestamp = " + timestamp);
        System.out.println("nonce = " + nonce);
        System.out.println("echostr = " + echostr);
       return  "success";
    }



    private void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/xml");

        InputStream is =  request.getInputStream();
        String requestStr = convertInptStream2String(is);
        System.out.println(requestStr);
        XStream xs = new XStream();
        xs.alias("xml", WeChatMessage.class);
        log.info("wechat message:{}", requestStr);
        WeChatMessage wcm = (WeChatMessage) xs.fromXML(requestStr);
        System.out.println(requestStr);
        switch (wcm.getMsgType()) {
            case MsgType.TEXT:
                System.out.println("………………text………………");
                sendTextMsgToUser(response, wcm, xs);
                break;
            case MsgType.IMAGE:
                System.out.println("………………image………………");
                sendImageMsgToUser(response, wcm);
                break;
            default:
                System.out.println("………………default………………");
                break;
        }


    }

    private void sendImageMsgToUser(HttpServletResponse response, WeChatMessage wcm, XStream xs) {
        WeChatMessage outMsg = new WeChatMessage();
        outMsg.setCreateTime(new Date().getTime());
        outMsg.setToUserName(wcm.getFromUserName());
        outMsg.setFromUserName(wcm.getToUserName());
        outMsg.setMsgType(MsgType.IMAGE);
        outMsg.setMediaId(wcm.getMediaId());
        outMsg.setPicUrl(wcm.getPicUrl());
        try {
            PrintWriter pw = response.getWriter();
            xs.alias("xml", WeChatMessage.class);
            xs.toXML(outMsg, pw);
           /* pw.flush();
            pw.close();*/
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void sendImageMsgToUser(HttpServletResponse response, WeChatMessage wcm) {
        WeChatMessage outMsg = new WeChatMessage();
        outMsg.setCreateTime(new Date().getTime());
        outMsg.setToUserName(wcm.getFromUserName());
        outMsg.setFromUserName(wcm.getToUserName());
        outMsg.setMsgType(MsgType.IMAGE);
        outMsg.setMediaId(wcm.getMediaId());
        outMsg.setPicUrl(wcm.getPicUrl());
        try {
            XStream xs = XStreamFactory.init(true);
            PrintWriter pw = response.getWriter();
            xs.alias("xml", WeChatMessage.class);
                xs.toXML(outMsg, pw);
           /* pw.flush();
            pw.close();*/
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void sendTextMsgToUser(HttpServletResponse response, WeChatMessage wcm, XStream xs) {
        WeChatMessage outMsg = new WeChatMessage();
        outMsg.setCreateTime(new Date().getTime());
        outMsg.setToUserName(wcm.getFromUserName());
        outMsg.setFromUserName(wcm.getToUserName());
        outMsg.setMsgType(MsgType.TEXT);
        outMsg.setContent(wcm.getContent());
        try {
            PrintWriter pw = response.getWriter();
            xs.alias("xml", WeChatMessage.class);
            xs.toXML(outMsg, pw);
           /* pw.flush();
            pw.close();*/
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String convertInptStream2String(InputStream is) throws IOException {

        byte[] b = new byte[4096];
        StringBuilder sb = new StringBuilder();
        int tmp;
        while ((tmp = is.read(b, 0, b.length)) != -1) {
            sb.append(new String(b, 0, tmp, "UTF-8"));
        }
        return sb.toString();
    }


}
