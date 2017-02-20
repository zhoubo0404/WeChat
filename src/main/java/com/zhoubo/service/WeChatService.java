package com.zhoubo.service;

import com.zhoubo.model.AccessToken;

/**
 * Created by zhoubo on 2016/12/14.
 */
public interface WeChatService {

    /**
     * 获得访问微信借口所需AccessToken
     * @return
     */
    public AccessToken getAccessToken();

    String getTacket(long sceneId);

    String getQRbyTicket(String tacket);
}
