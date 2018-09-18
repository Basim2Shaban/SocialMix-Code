package com.basm.socialmix;

/**
 * Created by basm on 15/01/2018.
 */

public class friends_user {
    private  String Image ;
    private  String Name ;

    public friends_user() {
    }

    public friends_user(String image, String name) {
        Image = image;
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
