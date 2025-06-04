package com.example.uts_a22202302996.ui.product;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.uts_a22202302996.api.RegisterAPI;
import com.example.uts_a22202302996.api.ServerAPI;
import com.example.uts_a22202302996.model.Product;
import com.example.uts_a22202302996.api.ProductResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductViewModel extends ViewModel {
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    private final Map<String, MutableLiveData<List<Product>>> categoryDataMap = new HashMap<>();

    public LiveData<List<Product>> getProductsForCategory(String category) {
        if (!categoryDataMap.containsKey(category)) {
            categoryDataMap.put(category, new MutableLiveData<>());
            fetchProducts(category, "", categoryDataMap.get(category));
        }
        return categoryDataMap.get(category);
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
