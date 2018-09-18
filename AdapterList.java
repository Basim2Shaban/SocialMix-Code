package com.basm.socialmix;

/**
 * Created by basm on 20/12/2017.
 */

public class AdapterList implements IItemWithAds {
    private int id;
    private String useremail ;
    private String username ;
    private String userimage ;
    private String posttext ;
    private String postimage ;
    private String datatime ;

    public AdapterList(int id, String useremail, String username, String userimage, String posttext, String postimage, String datatime) {
        this.id = id;
        this.useremail = useremail;
        this.username = username;
        this.userimage = userimage;
        this.posttext = posttext;
        this.postimage = postimage;
        this.datatime = datatime;
    }

    public int getUid() {
        return id;
    }

    public void setUid(int id) {
        this.id = id;
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

    public String getPosttext() {
        return posttext;
    }

    public void setPosttext(String posttext) {
        this.posttext = posttext;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getDatatime() {
        return datatime;
    }

    public void setDatatime(String datatime) {
        this.datatime = datatime;
    }

    @Override
    public int getItemType() {
        return ITEM_TYPE_NORMAL;
    }
}
