package com.example.project_basic;

public class FavoritesList {

    private String btn_num;
    private String btn_title;
    private String content;
    private String btn_writer;
    private String btn_date;
    private String btn_visit;
    private String content_good;
    private String document_name;
    private  String sub_title;

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getDocument_name() {
        return document_name;
    }

    public void setDocument_name(String document_name) {
        this.document_name = document_name;
    }

    public String getBtn_num() {
        return btn_num;
    }

    public void setBtn_num(String btn_num) {
        this.btn_num = btn_num;
    }

    public String getBtn_title() {
        return btn_title;
    }

    public void setBtn_title(String btn_title) {
        this.btn_title = btn_title;
    }

    public String getBtn_writer() {
        return btn_writer;
    }

    public void setBtn_writer(String btn_writer) {
        this.btn_writer = btn_writer;
    }

    public String getBtn_date() {
        return btn_date;
    }

    public void setBtn_date(String btn_date) {
        this.btn_date = btn_date;
    }

    public String getBtn_visitnum() {
        return btn_visit;
    }

    public void setBtn_visitnum(String btn_visit) {
        this.btn_visit = btn_visit;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent_good() {
        return content_good;
    }

    public void setContent_good(String content_good) {
        this.content_good = content_good;
    }

    public FavoritesList(String btn_num, String btn_title, String content, String btn_writer, String btn_date,
                         String btn_visit, String content_good, String document_name,String sub_title) {
        this.btn_num = btn_num;
        this.btn_title = btn_title;
        this.btn_writer = btn_writer;
        this.btn_date = btn_date;
        this.btn_visit = btn_visit;
        this.content = content;
        this.content_good = content_good;
        this.document_name = document_name;
        this.sub_title = sub_title;
    }
}
