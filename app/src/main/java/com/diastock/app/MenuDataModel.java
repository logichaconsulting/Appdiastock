package com.diastock.app;

public class MenuDataModel {

    String name;
    String attributes;
    String number;
    Integer image;

    public String getAttributes() {
        return attributes;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public MenuDataModel(String name, String number, String attributes, Integer image) {
        this.name = name;
        this.number = number;
        this.image = image;
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public Integer getImage() {
        return image;
    }

}