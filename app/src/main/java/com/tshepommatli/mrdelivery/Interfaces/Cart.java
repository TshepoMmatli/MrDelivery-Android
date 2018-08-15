package com.tshepommatli.mrdelivery.Interfaces;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Cart implements Serializable {

    @SerializedName("cart_id")
    public int id;

    @SerializedName("item_name")
    public String orderName;

    @SerializedName("item_desc")
    public String description;

    @SerializedName("quantity")
    public int quantity;

    @SerializedName("item_price")
    public double price;

    @SerializedName("menu_image")
    public String img_url;

    @SerializedName("user_id")
    public String customer;

}
