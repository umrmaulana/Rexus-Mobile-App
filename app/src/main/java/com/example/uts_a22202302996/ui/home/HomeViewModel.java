package com.example.uts_a22202302996.ui.home;

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
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Menampilkan Produk berdasarkan kategori
    public void fetchProducts(String category) {
        RegisterAPI apiService = ServerAPI.getClient().create(RegisterAPI.class);
        Call<ProductResponse> call = category.isEmpty()
                ? apiService.getProducts("") // Fetch all products
                : apiService.getProducts(category);

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    products.setValue(response.body().getResult());
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