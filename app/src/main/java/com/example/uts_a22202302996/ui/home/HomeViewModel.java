package com.example.uts_a22202302996.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.uts_a22202302996.product.Product;
import com.example.uts_a22202302996.product.ProductResponse;
import com.example.uts_a22202302996.api.RegisterAPI;
import com.example.uts_a22202302996.api.ServerAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<Product>> products = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> keyboardProducts = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> mouseProducts = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> headsetProducts = new MutableLiveData<>();

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<Product>> getKeyboardProducts() {
        return keyboardProducts;
    }

    public LiveData<List<Product>> getMouseProducts() {
        return mouseProducts;
    }

    public LiveData<List<Product>> getHeadsetProducts() {
        return headsetProducts;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void fetchAllProducts(String search) {
        fetchProducts("all", search, products);
        if (products.getValue() != null && search.equals(searchQuery.getValue())) {
            return;
        }
    }

    public void fetchKeyboardProducts(String search) {
        fetchProducts("keyboard", search, keyboardProducts);
        if (keyboardProducts.getValue() != null && search.equals(searchQuery.getValue())) {
            return;
        }
    }

    public void fetchMouseProducts(String search) {
        fetchProducts("mouse", search, mouseProducts);
        if (mouseProducts.getValue() != null && search.equals(searchQuery.getValue())) {
            return;
        }
    }

    public void fetchHeadsetProducts(String search) {
        fetchProducts("headset", search, headsetProducts);
        if (headsetProducts.getValue() != null && search.equals(searchQuery.getValue())) {
            return;
        }
    }

    // Menampilkan Produk berdasarkan kategori
    public void fetchProducts(String category, String search, MutableLiveData<List<Product>> targetLiveData) {

        RegisterAPI apiService = ServerAPI.getClient().create(RegisterAPI.class);
        Call<ProductResponse> call = apiService.getProducts(category, search);

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    targetLiveData.setValue(response.body().getResult());
                } else {
                    errorMessage.setValue("Failed to load products");
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }

}