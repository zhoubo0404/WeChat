package com.zhoubo.controller;

import com.thoughtworks.xstream.XStream;
import com.zhoubo.model.*;
import com.zhoubo.service.WeChatService;
import com.zhoubo.util.FileUtil;
import com.zhoubo.util.HttpClientUtil;
import com.zhoubo.util.WeChatAPI;
import com.zhoubo.util.XStreamFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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

    public static InputStream generateThumbnail(byte[] bytes) {
        HttpClient httpClient = new DefaultHttpClient();
        InputStream inputStream = null;

        try {
            // NOTE: You must use the same location in your REST call as you used to obtain your subscription keys.
            //   For example, if you obtained your subscription keys from westus, replace "westcentralus" in the
            //   URL below with "westus".
            URIBuilder uriBuilder = new URIBuilder("https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/generateThumbnail");

            uriBuilder.setParameter("width", "100");
            uriBuilder.setParameter("height", "150");
            uriBuilder.setParameter("smartCropping", "true");

            URI uri = uriBuilder.build();
            HttpPost request = new HttpPost(uri);

            // Request headers.
//            request.setHeader("Content-Type", "application/json");
//            request.setHeader("Content-Type", "application/json");

            // NOTE: Replace the "Ocp-Apim-Subscription-Key" value with a valid subscription key.
            request.setHeader("Ocp-Apim-Subscription-Key", "c8d980c17d494213958318ed78c1b8d4");

            // Request body. Replace the example URL with the URL for the JPEG image of a person.
//            StringEntity requestEntity = new StringEntity("{\"url\":" + url + "}");

//            StringEntity requestEntity = new StringEntity("{\"url\":\"http://a.hiphotos.baidu.com/zhidao/pic/item/e4dde71190ef76c647fd64809c16fdfaaf51676a.jpg\"}");
//            System.out.println("imgUrl = " + imgUrl);
/*            StringEntity requestEntity = new StringEntity("{\"url\":" + imgUrl + "}");
            request.setEntity(requestEntity);*/
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            multipartEntityBuilder.addBinaryBody("url", bytes);
//            multipartEntityBuilder.add
            HttpEntity httpEntity1 = multipartEntityBuilder.build();
//            MultipartEntity httpEntity1 = new MultipartEntity();
//            httpEntity1.addPart("url", new FileBody(new File("G:\\git\\image\\IMG_1141.JPG")));
            request.setEntity(httpEntity1);
//            InputStreamEntity inputStreamEntity = new InputStreamEntity(imageInputStream);
//            request.setEntity(inputStreamEntity);
            HttpResponse response = httpClient.execute(request);
            System.out.println(response);

            // Display the thumbnail.
            HttpEntity httpEntity = response.getEntity();
            inputStream = httpEntity.getContent();
//            displayImage(httpEntity.getContent());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return inputStream;
    }

    @RequestMapping(value = "/welcome")
    public String welcome(String json) {
        System.out.println("json  = " + json);
        return "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx5431e0019980e6e6&redirect_uri=http%3A%2F%2Fwechatdemo.tunnel.qydev.com%2FweChat%2FtestIndex&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
//        return "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx5e1ec633ff1209c1&redirect_uri=http%3A%2F%2Fwechatdemo.tunnel.qydev.com%2FweChat%2FtestIndex&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
//        return "redirect:http://wechatdemo.tunnel.qydev.com/weChat/testIndex";
    }

    @RequestMapping(value = "/testIndex")
    public String test(String code, String state) {
        System.out.println("code = " + code + " state = " + state);
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

        InputStream is =  request.getInputStream();
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
        System.out.println("reply image begion");
        ImageMessage outMsg = new ImageMessage();
        outMsg.setCreateTime(new Date().getTime());
        outMsg.setToUserName(wcm.getFromUserName());
        outMsg.setFromUserName(wcm.getToUserName());
        outMsg.setMsgType(MsgType.IMAGE);
        Image image = new Image();
        image.setMediaId(wcm.getMediaId());
//        image.setPicUrl(wcm.getPicUrl());
        //使用Microsoft Azure 生产缩略图
        AccessToken accessToken = weChatService.getAccessToken();
        String jsonParams = "access_token=" + accessToken.getToken() + "&media_id=" + wcm.getMediaId();
        System.out.println("获得临时素材 = " + "https://api.weixin.qq.com/cgi-bin/media/get" + "?" + jsonParams);
        String picUrl = null;
//        try {
//            picUrl = HttpClientUtil.sendGetRequest("https://api.weixin.qq.com/cgi-bin/media/get" + "?" + jsonParams);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        System.out.println("picUrl = " + "https://api.weixin.qq.com/cgi-bin/media/get" + "?" + jsonParams);
//        https://api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID
//        InputStream inputStream = generateThumbnail("https://api.weixin.qq.com/cgi-bin/media/get" + "?" + jsonParams);
        byte[] bytes = HttpClientUtil.htppGetTobytes("https://api.weixin.qq.com/cgi-bin/media/get" + "?" + jsonParams);
//        InputStream inputStream = new ByteArrayInputStream(bytes);
        InputStream inputStream;
//        String imgUrl = AliyunOSSUtil.putObject(inputStream);
//        String imgUrl = AliyunOSSUtil.getImage();
        FileUtil.save(bytes);
        inputStream = generateThumbnail(bytes);
/*        String uploadMediaFile =  WeChatAPI.UPLOAD_MEDIA_FILE;
        uploadMediaFile = uploadMediaFile.replace("ACCESS_TOKEN", accessToken.getToken()).replace("TYPE", "image");
        log.info("uploadMediaFile", uploadMediaFile);
        System.out.println("uploadMediaFile = " + uploadMediaFile);
        String uploadResponse = HttpClientUtil.uploadFile(uploadMediaFile, inputStream);
        log.info("uploadResponse", uploadResponse);
        System.out.println("uploadResponse = " +  uploadResponse);*/
        FileOutputStream fileOutputStream = null;
        File file = null;
        try {
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            file = new File("G:\\git\\image\\3.jpg");
            fileOutputStream = new FileOutputStream(file);
            byte[] byteBuffer = new byte[1024];
            int b;
            try {
                b = inputStream.read(byteBuffer);
                while (b != -1) {
                    fileOutputStream.write(byteBuffer);
                    b = inputStream.read(byteBuffer);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        String uploadMediaFile = WeChatAPI.UPLOAD_MEDIA_FILE;
        uploadMediaFile = uploadMediaFile.replace("ACCESS_TOKEN", accessToken.getToken()).replace("TYPE", "image");
        log.info("uploadMediaFile", uploadMediaFile);
        System.out.println("uploadMediaFile = " + uploadMediaFile);
        String uploadResponse = HttpClientUtil.uploadFile(uploadMediaFile, file);
        log.info("uploadResponse", uploadResponse);
        System.out.println("uploadResponse = " + uploadResponse);
//        JSONParser
//        JSONObject jsonObject = new JSONObject();


        List<Image> images = new ArrayList<Image>();
        images.add(image);
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

    @RequestMapping("/test")
    public ModelAndView test(String openId, HttpServletRequest request) {
        String oid = request.getParameter("openId");
        System.out.println("test");
        ModelAndView modelAndView = new ModelAndView("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxd1fb9aca45870db5&redirect_uri=http://wechatdemo.tunnel.qydev.com/weChat/test&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
        return modelAndView;
    }
}
