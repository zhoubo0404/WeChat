package com.zhoubo.controller;

import com.alibaba.druid.support.json.JSONParser;
import com.thoughtworks.xstream.XStream;
import com.zhoubo.model.*;
import com.zhoubo.service.WeChatService;
import com.zhoubo.util.FileUtil;
import com.zhoubo.util.HttpClientUtil;
import com.zhoubo.util.WeChatAPI;
import com.zhoubo.util.XStreamFactory;
import com.zhoubo.util.microsoft.CognitiveServicesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by zhoubo on 2016/12/14.
 */
//@Controller
@RequestMapping(value = "/weChat")
@Controller
public class WeChatController {
    private final static Logger log = LoggerFactory.getLogger(WeChatController.class);

    @Autowired
    private WeChatService weChatService;



    @RequestMapping(value = "/welcome")
    public String welcome(String json) {
        System.out.println("json  = " + json);
        return "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx5431e0019980e6e6&redirect_uri=http%3A%2F%2Fwechatdemo.tunnel.qydev.com%2FweChat%2FtestIndex%3Fphone%3D18680369308&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
//        return "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx5e1ec633ff1209c1&redirect_uri=http%3A%2F%2Fwechatdemo.tunnel.qydev.com%2FweChat%2FtestIndex&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
//        return "redirect:http://wechatdemo.tunnel.qydev.com/weChat/testIndex";
    }

    @RequestMapping(value = "/testIndex")
    public String test(String code, String state, String phone) {
        System.out.println("code = " + code + " state = " + state + " phone " + phone);
        return "index";
    }

    @ResponseBody
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
        return "success";
    }

    private void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/xml");
        InputStream is = request.getInputStream();
        String requestStr = convertInptStream2String(is);
        System.out.println(requestStr);
        XStream xs = new XStream();
        xs.alias("xml", WeChatMessage.class);
        log.info("wechat message:{}", requestStr);
        WeChatMessage wcm = (WeChatMessage) xs.fromXML(requestStr);
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
        System.out.println("reply image begion");
        WeChatMessage outMsg = new WeChatMessage();
        outMsg.setCreateTime(System.currentTimeMillis());
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
        System.out.println("reply image begion");
        ImageMessage outMsg = new ImageMessage();
        outMsg.setCreateTime(System.currentTimeMillis());
        outMsg.setToUserName(wcm.getFromUserName());
        outMsg.setFromUserName(wcm.getToUserName());
        outMsg.setMsgType(MsgType.IMAGE);

        //获得公共号访问token
        AccessToken accessToken = weChatService.getAccessToken();
        String jsonParams = "access_token=" + accessToken.getToken() + "&media_id=" + wcm.getMediaId();
        System.out.println("获得临时素材 = " + "https://api.weixin.qq.com/cgi-bin/media/get" + "?" + jsonParams);
        //下载素材
        byte[] bytes = HttpClientUtil.htppGetTobytes("https://api.weixin.qq.com/cgi-bin/media/get" + "?" + jsonParams);
        InputStream inputStream;
        //图片保存路径
        String imagePath = "G:\\git\\image\\4.jpg";
        FileUtil.save(bytes, imagePath);
        //使用微软认知服务获得缩略图
        inputStream = CognitiveServicesUtil.generateThumbnail(bytes);
        System.out.println(CognitiveServicesUtil.faceDetect(bytes));
        File file = new File("G:\\git\\image\\3.jpg");
        //保存文件
        FileUtil.save(inputStream, file);
        String uploadMediaFile = WeChatAPI.UPLOAD_MEDIA_FILE;
        //获得微信临时素材上传url
        uploadMediaFile = uploadMediaFile.replace("ACCESS_TOKEN", accessToken.getToken()).replace("TYPE", "image");
        log.info("uploadMediaFile", uploadMediaFile);
        System.out.println("uploadMediaFile = " + uploadMediaFile);
        //上传临时素材
        String uploadResponse = HttpClientUtil.uploadFile(uploadMediaFile, file);
        log.info("uploadResponse", uploadResponse);
        System.out.println("uploadResponse = " + uploadResponse);
//        JSONParser
//        JSONObject jsonObject = new JSONObject();
        JSONParser jsonParser = new JSONParser(uploadResponse);
        Map<String, Object> map = jsonParser.parseMap();
        Image image = new Image();
        image.setMediaId((String) map.get("media_id"));
//        Image image1 = new Image();
//        image1.setMediaId(wcm.getMediaId());
        List<Image> images = new ArrayList<Image>();
        images.add(image);
//        images.add(image1);
        outMsg.setImage(images);
        try {
            XStream xs = XStreamFactory.init(true);
            PrintWriter pw = response.getWriter();
            xs.alias("xml", ImageMessage.class);
            xs.alias("", Image.class);
            xs.toXML(outMsg, pw);
           /* pw.flush();
            pw.close();*/
            System.out.println("reply image");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void sendTextMsgToUser(HttpServletResponse response, WeChatMessage wcm, XStream xs) {
        WeChatMessage outMsg = new WeChatMessage();
        outMsg.setCreateTime(System.currentTimeMillis());
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

    @RequestMapping("/test")
    public ModelAndView test(String openId, HttpServletRequest request) {
        String oid = request.getParameter("openId");
        System.out.println("test");
        ModelAndView modelAndView = new ModelAndView("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxd1fb9aca45870db5&redirect_uri=http://wechatdemo.tunnel.qydev.com/weChat/test&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
        return modelAndView;
    }

}
