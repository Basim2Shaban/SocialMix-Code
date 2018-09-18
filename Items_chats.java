package com.basm.socialmix;

/**
 * Created by Basim on 26/01/2018.
 */

class Items_chats {
    private String image ;
    private String massage ;
    private String name ;

    public Items_chats(String image, String massage, String name) {
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
