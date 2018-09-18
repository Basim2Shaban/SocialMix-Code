package com.basm.socialmix;

/**
 * Created by basm on 18/01/2018.
 */

public class SendOpject {
   private String name ;
   private String massage ;
   private String image ;

    public SendOpject() {
    }

    public SendOpject(String name, String massage, String image) {
        this.name = name;
        this.massage = massage;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
