package com.zhoubo.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhoubo.model.AccessToken;
import com.zhoubo.model.WXUrl;
import com.zhoubo.service.WeChatService;
import com.zhoubo.util.HttpClientUtil;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhoubo on 2016/12/14.
 */
@Component("weChatService")
public class WeChatServiceImpl implements WeChatService{
    public static final String ACCESS_TOKEN_FILE = "accessToken.txt";

    public static void main(String[] args) {
//       System.out.println(new WeChatServiceImpl().getAccessToken());
        long sceneId = 123;
        WeChatService weChatService = new WeChatServiceImpl();
        String tacket = weChatService.getTacket(sceneId);
        System.out.println("ticket: " + tacket);
        String QRUtl = weChatService.getQRbyTicket(tacket);
        System.out.println("QRUtl: " + QRUtl);

    }

    @Override
    public AccessToken getAccessToken() {
        AccessToken accessToken = null;
        File file = new File(ACCESS_TOKEN_FILE);
        if (!file.exists()) {
            accessToken = sendAccessTokenRequest();
            System.out.println("file not exists sendAccessTokenRequest: accessToken" + accessToken.getCreateTime());
            try {
                file.createNewFile();
                setObjectToFile(accessToken, file);
                System.out.println("create and writer success!");
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            try {
                accessToken = (AccessToken) getObjectFromFile(file);
                System.out.println("getObjectFromFile: accessToken" + accessToken.getCreateTime());
                if (!accessToken.isEfficient()) {
                    accessToken = sendAccessTokenRequest();
                    System.out.println(" file.exists sendAccessTokenRequest: accessToken" + accessToken.getCreateTime());
                    setObjectToFile(accessToken, file);
                }
                System.out.println(accessToken.toString());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e.getMessage(), e);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

        }
      /*  HttpPost httpPost = new HttpPost(accessTokenUrl);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("grant_type", "client_credential"));
        pairs.add(new BasicNameValuePair("appid", "wxd1fb9aca45870db5"));
        pairs.add(new BasicNameValuePair("secret", "7b1d5135b6376a1969c1cc3b9c1fee64"));

        try {
            UrlEncodedFormEntity urlEntity = new UrlEncodedFormEntity(pairs, "UTF-8");
            httpPost.setEntity(urlEntity);
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity entity = httpResponse.getEntity();
            System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return  accessToken;
    }

    public String getTacket(long sceneId) {
        String url = String.format(WXUrl.GENERATE_QR_CODE, this.getAccessToken().getToken());
        System.out.println("getTacket:url: " + url);
        String params = "{\"expire_seconds\": 604800,\"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\":" + sceneId + "}}}";
        String result = null;
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            result = HttpClientUtil.httpsRequest(url, "POST", params);
            ObjectMapper objectMapper = new ObjectMapper();
            resultMap = objectMapper.readValue(result, Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return resultMap.get("ticket").toString();
    }

    public String getQRbyTicket(String tacket) {
        return String.format(WXUrl.QR_URL, tacket);
    }

    private Object getObjectFromFile(File file) throws IOException, ClassNotFoundException {
        InputStream is = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(is);
        Object accessToken = ois.readObject();
        return accessToken;
    }

    private void setObjectToFile(Object accessToken, File file) throws IOException {
        OutputStream os = new FileOutputStream(file);
        ObjectOutputStream oos =  new ObjectOutputStream(os);
        oos.writeObject(accessToken);
    }

    private AccessToken sendAccessTokenRequest() {
        CloseableHttpClient httpClient = HttpClients.createDefault();                                                               //0bf4aa77eb74ae5794cb3c90497a1682
//        String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx5e1ec633ff1209c1&secret=0bf4aa77eb74ae5794cb3c90497a1682";
        String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxd1fb9aca45870db5&secret=7b1d5135b6376a1969c1cc3b9c1fee64"; //测试公众号
        String responeStr = HttpClientUtil.sendGetRequest(accessTokenUrl);
        System.out.println("send access token request!");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        AccessToken accessToken = null;
        try {
            long createTime = new Date().getTime();
            accessToken = objectMapper.readValue(responeStr, AccessToken.class);
            accessToken.setCreateTime(createTime);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        System.out.println("responeStr = " + responeStr);
        return accessToken;
    }
}
