package com.example.uts_a22202302996.model;

import com.google.gson.annotations.SerializedName;

public class ShipAddress {
    private int id;
    private int user_id;
    private int province_id;
    private int city_id;
    private int postal_code;
    private String province_name;
    private String city_name;
    private String recipt_name;
    private String no_tlp;
    private String address;
    @SerializedName("is_active")
    private String is_active;

    public int getId() { return id; }
    public int getUser_id() { return user_id; }
    public int getProvince_id() { return province_id; }
    public int getCity_id() { return city_id; }
    public int getPostal_code() { return postal_code; }
    public String getProvince_name() { return province_name; }
    public String getCity_name() { return city_name; }
    public String getRecipt_name() { return recipt_name; }
    public String getNo_tlp() { return no_tlp; }
    public String getAddress() { return address; }
    public String getIs_active() { return is_active; }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public boolean isActive() {
        return "1".equals(is_active);
    }
}
