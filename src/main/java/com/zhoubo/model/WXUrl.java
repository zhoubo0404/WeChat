package com.zhoubo.model;

/**
 * Created by zhoubo on 2016/12/20.
 */
public class WXUrl {
    /**
     * 生成带参数的二维码
     */
    public static final String GENERATE_QR_CODE = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s";
    public static final String QR_URL = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=%s";
}
