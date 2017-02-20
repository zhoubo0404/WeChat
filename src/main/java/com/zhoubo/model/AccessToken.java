package com.zhoubo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by 80011083 on 2016/11/7.
 */
public class AccessToken implements Serializable{

    private static final long serialVersionUID = 2084622880176015255L;

    public AccessToken(){

    }
    @JsonProperty(value = "access_token")
    private String token;
    @JsonProperty(value = "expires_in")
    private long expireIn;

    private long createTime;

    public AccessToken(String token, long expireIn) {
        this.token = token;
        this.expireIn = expireIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpireIn() {
        return expireIn;
    }

    public void setExpireIn(long expireIn) {
        this.expireIn = expireIn;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isEfficient() {
        System.out.println(new Date().getTime() + " date = ");
        System.out.println((this.getCreateTime()) +"this.getCreateTime()");
        if (new Date().getTime() > (this.getCreateTime()  + this.getExpireIn() * 1000)){
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return "AccessToken{" +
                "token='" + token + '\'' +
                ", expireIn=" + expireIn +
                ", createTime=" + createTime +
                '}';
    }
}
