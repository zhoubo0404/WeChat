package com.zhoubo.service.impl;

import com.zhoubo.controller.WeChatController;
import com.zhoubo.service.WeChatService;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoubo on 2016/12/14.
 */
public class WeChatServiceImpl implements WeChatService{
    @Override
    public String getAccessToken() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token";
        HttpPost httpPost = new HttpPost(accessTokenUrl);
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
        }
        return  null;
    }

    public static void main(String[] args) {
        new WeChatServiceImpl().getAccessToken();
    }
}
