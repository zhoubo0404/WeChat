package com.zhoubo.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties(value = {"isQueryDimensionType"})
public class SkinType implements Serializable {


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
    private Integer kind;

    /**
     * update_time (修改时间)
     */
    private Date updateTime;
    /**
     * 描述
     */
    private String desc;

    /**
     * 是否按维度类型查询skinType td_dimension表
     */
    private String isQueryDimensionType;

    public String getIsQueryDimensionType() {
        return isQueryDimensionType;
    }

    public void setIsQueryDimensionType(String isQueryDimensionType) {
        this.isQueryDimensionType = isQueryDimensionType;
    }

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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
