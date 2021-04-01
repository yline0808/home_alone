package com.example.myapplication;

import java.io.Serializable;

public class NoticeDTO implements Serializable {
    private String title;
    private String content;
    private String summary;
    private String time;
    private String uid;
    private int commentCnt;

    public NoticeDTO() {
    }

    public NoticeDTO(String title, String content, String time) {
        this.title = title;
        this.content = content;
        this.summary = settingSummary(content);
        this.time = time;
        this.commentCnt = 0;
    }
    public NoticeDTO(String title, String content, String time, String uid) {
        this.title = title;
        this.content = content;
        this.summary = settingSummary(content);
        this.time = time;
        this.uid = uid;
        this.commentCnt = 0;
    }
    public String settingSummary(String contents){
        String str = "";

        if(contents.length() > 23){
            for(int j = 0; j < 23; j++){
                str += contents.charAt(j);
            }
            str += "...";
        }else{
            str = contents;
        }
        return str;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCommentCnt() {
        return commentCnt;
    }

    public void setCommentCnt(int commentCnt) {
        this.commentCnt = commentCnt;
    }
}
