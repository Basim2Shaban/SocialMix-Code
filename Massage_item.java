package com.basm.socialmix;

/**
 * Created by basm on 20/01/2018.
 */

public class Massage_item {
    private String image;
    private String massage ;
    private String name ;


    public Massage_item() {
    }

    public Massage_item(String image, String massage, String name) {
        this.image = image;
        this.massage = massage;
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
