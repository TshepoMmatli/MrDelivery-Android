package com.tshepommatli.mrdelivery.Interfaces;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Products implements Serializable{

    @SerializedName("menu_id")
    public int id;

    @SerializedName("item_name")
    public String name;

    @SerializedName("item_desc")
    public String description;

    @SerializedName("item_price")
    public double price;

    @SerializedName("menu_image")
    public String img_url;

}
