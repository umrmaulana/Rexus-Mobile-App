package com.example.uts_a22202302996.api;

import java.util.ArrayList;
import java.util.List;

public class CategoryResponse {
    private List<String> categories;

    public List<String> getCategories() {
        return categories != null ? categories : new ArrayList<>();
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}

