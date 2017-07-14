package com.zhoubo.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class SkinTypeWithPic implements Serializable {


    private static final long serialVersionUID = -2950452297672565914L;
    /**
     * skin_type_id (肤质类型主键)
     */
    private Integer skinTypeId;

    /**
     * type_id (类型key)
     */
    private Integer typeId;

    /**
     * type_name (类型名称)
     */
    private String typeName;


    /**
     * kind (类型分类（1-单点肤质分类，2-单点肤质细分类，3-肤质5点取值分类，4-肤质年龄分类）)
     */
    @JsonIgnore
    private Integer kind;

    /**
     * skin_type_img.android_pic (安卓图片地址)
     */
    private String androidPic;

    /**
     * skin_type_img.ios_pic (iOS图片地址)
     */
    private String iosPic;

    /**
     * 描述
     */
    private String desc;


    public Integer getSkinTypeId() {
        return skinTypeId;
    }

    public void setSkinTypeId(Integer skinTypeId) {
        this.skinTypeId = skinTypeId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getKind() {
        return kind;
    }

    public void setKind(Integer kind) {
        this.kind = kind;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAndroidPic() {
        return androidPic;
    }

    public void setAndroidPic(String androidPic) {
        this.androidPic = androidPic;
    }

    public String getIosPic() {
        return iosPic;
    }

    public void setIosPic(String iosPic) {
        this.iosPic = iosPic;
    }
}
