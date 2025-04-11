package com.example.uts_a22202302996.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.adapter.ProductAdapter;
import com.example.uts_a22202302996.ui.home.HomeViewModel;

import java.util.ArrayList;

public class HeadsetFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView textViewEmpty;
    private ProductAdapter productAdapter;
    private HomeViewModel homeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        productAdapter = new ProductAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(productAdapter);

        // Inisialisasi ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Observe LiveData dari ViewModel
        homeViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products == null || products.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                textViewEmpty.setVisibility(View.VISIBLE);
            } else {
                textViewEmpty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                productAdapter.setProductList(products);
            }
        });

        homeViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                textViewEmpty.setText("Gagal memuat data produk");
                textViewEmpty.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        // Panggil API untuk kategori keyboard
        homeViewModel.fetchProducts("headset");

        return view;
    }
}
