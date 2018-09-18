package com.basm.socialmix;

/**
 * Created by basm on 06/01/2018.
 */

public class List_item {
    private  int Cid ;
    private  String pid ;
    private  String useremail ;
    private  String username ;
    private  String userimage ;
    private  String commenttext ;
    private  String commentimage ;
    private  String datatime ;

    public List_item(int cid, String pid, String useremail, String username, String userimage, String commenttext, String commentimage, String datatime) {
        Cid = cid;
        this.pid = pid;
        this.useremail = useremail;
        this.username = username;
        this.userimage = userimage;
        this.commenttext = commenttext;
        this.commentimage = commentimage;
        this.datatime = datatime;
    }

    public int getCid() {
        return Cid;
    }

    public void setCid(int cid) {
        Cid = cid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getCommenttext() {
        return commenttext;
    }

    public void setCommenttext(String commenttext) {
        this.commenttext = commenttext;
    }

    public String getCommentimage() {
        return commentimage;
    }

    public void setCommentimage(String commentimage) {
        this.commentimage = commentimage;
    }

    public String getDatatime() {
        return datatime;
    }

    public void setDatatime(String datatime) {
        this.datatime = datatime;
    }
}
