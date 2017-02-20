package com.tchip.autoplayer.model;

/**
 * Created by Administrator on 2016/10/31.
 */
public class Video {

    /**
     * 名称
     */
    String name;

    /**
     * 路径
     */
    String path;

    public Video(String name, String path){
        this.name = name;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
