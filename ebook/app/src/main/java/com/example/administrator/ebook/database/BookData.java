package com.example.administrator.ebook.database;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lxr on 2016/5/13.
 */
public class BookData extends DataSupport {
    private int img;
    private String name;
    private String msg;
    private int type;
    private int contentCount;
    private List<BookSetContent> contentList = new ArrayList<BookSetContent>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getContentCount() {
        return contentCount;
    }

    public void setContentCount(int contentCount) {
        this.contentCount = contentCount;
    }

    public List<BookSetContent> getContentList() {
        return contentList;
    }

    public void setContentList(List<BookSetContent> contentList) {
        this.contentList = contentList;
    }





}