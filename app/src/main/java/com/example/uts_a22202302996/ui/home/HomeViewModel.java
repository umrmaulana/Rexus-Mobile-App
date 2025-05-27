package com.example.uts_a22202302996.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.uts_a22202302996.api.RegisterAPI;
import com.example.uts_a22202302996.api.ServerAPI;
import com.example.uts_a22202302996.product.Product;
import com.example.uts_a22202302996.api.ProductResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeViewModel extends ViewModel {
    private MutableLiveData<List<Product>> productLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Product>> discountLiveData = new MutableLiveData<>();
    private RegisterAPI apiService;

    public HomeViewModel() {
        apiService = ServerAPI.getClient().create(RegisterAPI.class);
    }

    public LiveData<List<Product>> getPopulerProducts() {
        return productLiveData;
    }
    public LiveData<List<Product>> getDiscountProducts() { return discountLiveData; }

    public void fetchPopularProducts() {
        Call<ProductResponse> call = apiService.getPopulerProducts();
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    productLiveData.setValue(response.body().getResult());
                } else {
                    productLiveData.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                productLiveData.setValue(new ArrayList<>());
            }
        });
    }

    public void fetchDiscountProducts() {
        Call<ProductResponse> call = apiService.getDiscountProducts();
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    discountLiveData.setValue(response.body().getResult());
                } else {
                    discountLiveData.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                discountLiveData.setValue(new ArrayList<>());
            }
        });
    }

}