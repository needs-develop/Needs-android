package com.example.project_basic;

public class ReplyList {
    String comment_reply;
    String writer_reply;
    String time_reply;
    String doc_reply;
    String r_doc_reply;

    public String getDoc_reply() {
        return doc_reply;
    }

    public void setDoc_reply(String doc_reply) {
        this.doc_reply = doc_reply;
    }

    public String getR_doc_reply() {
        return r_doc_reply;
    }

    public void setR_doc_reply(String r_doc_reply) {
        this.r_doc_reply = r_doc_reply;
    }

    public String getTime_reply() {
        return time_reply;
    }

    public void setTime_reply(String time_reply) {
        this.time_reply = time_reply;
    }



    public String getComment_reply() {
        return comment_reply;
    }

    public void setComment_reply(String comment_reply) {
        this.comment_reply = comment_reply;
    }

    public String getWriter_reply() {
        return writer_reply;
    }

    public void setWriter_reply(String writer_reply) {
        this.writer_reply = writer_reply;
    }

    public ReplyList(String comment_reply, String writer_reply,String time_reply,String doc_reply,String r_doc_reply) {
        this.comment_reply = comment_reply;
        this.writer_reply = writer_reply;
        this.time_reply = time_reply;
        this.doc_reply = doc_reply;
        this.r_doc_reply = r_doc_reply;
    }
}
