package com.qqonline.domain;

import java.util.Date;

public class MpfPicture {
    private int ID;
    private int DbId;
    private String OpenId;
    private Date AddTime;
    private String PicUrl;
    private String SdCardAdd;
    private String MediaId;

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }

    public int getID() {
        return ID;
    }

    public int getDbId() {
        return DbId;
    }

    public void setDbId(int dbId) {
        DbId = dbId;
    }

    public String getOpenId() {
        return OpenId;
    }

    public void setOpenId(String openId) {
        OpenId = openId;
    }

    public Date getAddTime() {
        return AddTime;
    }

    public void setAddTime(Date addTime) {
        AddTime = addTime;
    }

    public String getPicUrl() {
        return PicUrl;
    }

    public void setPicUrl(String picUrl) {
        PicUrl = picUrl;
    }

    public String getSdCardAdd() {
        return SdCardAdd;
    }

    public void setSdCardAdd(String sdCardAdd) {
        SdCardAdd = sdCardAdd;
    }
}
