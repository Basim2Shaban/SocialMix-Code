package com.basm.socialmix;

/**
 * Created by basm on 11/01/2018.
 */

public class GetReq {
    private int id;
    private String sender;
    private String sender_to;
    private String name;
    private String image;

    public GetReq(int id, String sender, String sender_to, String name, String image) {
        this.id = id;
        this.sender = sender;
        this.sender_to = sender_to;
        this.name = name;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender_to() {
        return sender_to;
    }

    public void setSender_to(String sender_to) {
        this.sender_to = sender_to;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}