package com.example.administrator.ebook.database;

import org.litepal.crud.DataSupport;

/**
 * Created by Lxr on 2016/5/13.
 */
public class BookSetContent extends DataSupport {
    private int img ;
    private String name;
    private String msg;
    private int id;
    private String path;
    private String fileType;


    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public int getBookdata_id() {
        return bookdata_id;
    }

    public void setBookdata_id(int bookdata_id) {
        this.bookdata_id = bookdata_id;
    }

    private int bookdata_id;

    public BookData getBookData() {
        return bookData;
    }

    public void setBookData(BookData bookData) {
        this.bookData = bookData;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private BookData bookData;
}
