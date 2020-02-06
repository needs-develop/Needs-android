package com.example.project_basic;

public class ReplyList {
    String comment_reply;
    String writer_reply;
    String time_reply;

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

    public ReplyList(String comment_reply, String writer_reply,String time_reply) {
        this.comment_reply = comment_reply;
        this.writer_reply = writer_reply;
        this.time_reply = time_reply;
    }
}
