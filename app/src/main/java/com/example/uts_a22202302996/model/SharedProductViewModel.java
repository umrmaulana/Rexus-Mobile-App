package com.example.uts_a22202302996.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.uts_a22202302996.product.Product;

import java.util.List;

public class SharedProductViewModel extends ViewModel {
    private final MutableLiveData<Product> selectedProduct = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> selectedProductList = new MutableLiveData<>();
    private final MutableLiveData<String> selectedCategory = new MutableLiveData<>();

    public void selectProduct(Product product) {
        selectedProduct.setValue(product);
    }

    public LiveData<Product> getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProductList(List<Product> products) {
        selectedProductList.setValue(products);
    }

    public LiveData<List<Product>> getSelectedProductList() {
        return selectedProductList;
    }

    public void selectCategory(String categories) {
        selectedCategory.setValue(categories);
    }

    public LiveData<String> getSelectedCategory() {
        return selectedCategory;
    }

}

