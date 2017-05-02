package com.example.administrator.instagramdemo.Item;

/**
 * 模型对象类，将成功获取的Json数据存入
 * Created by Administrator on 2017/3/6.
 */

public class GalleryItem {
    private String mCaption;
    private String mId;
    private String mUrl;

    public String getmOwnername() {
        return mOwnername;
    }

    public void setmOwnername(String mOwnername) {
        this.mOwnername = mOwnername;
    }

    private String mOwnername;


    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    private String mTitle;

    @Override
    public String toString() {
        return mCaption;
    }

    public String getmCaption() {
        return mCaption;
    }

    public void setmCaption(String mCaption) {
        this.mCaption = mCaption;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}
