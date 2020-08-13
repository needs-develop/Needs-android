package com.tester.Needs.Common;

public class RecordList {
    private String profile;
    private String data;
    private  String address;
    private String document_name;
    private String writer;
    private String day;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDocument_name() {
        return document_name;
    }

    public void setDocument_name(String document_name) {
        this.document_name = document_name;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
    public String getWriter()
    {
        return this.writer;
    }
    public void setWriter(String context)
    {
        this.writer = context;
    }
    public RecordList(String profile, String data, String address, String document_name, String writer, String day) {
        this.profile = profile;
        this.data = data;
        this.address = address;
        this.document_name = document_name;
        this.writer = writer;
        this.day = day;
    }



}
