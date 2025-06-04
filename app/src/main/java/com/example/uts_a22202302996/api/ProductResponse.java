package com.example.uts_a22202302996.api;

import com.example.uts_a22202302996.model.Product;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductResponse {
    @SerializedName("result")
    private List<Product> result;

    public List<Product> getResult() {
        return result;
    }
}
