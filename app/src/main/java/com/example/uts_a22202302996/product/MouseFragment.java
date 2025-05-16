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
import com.example.uts_a22202302996.model.SharedProductViewModel;
import com.example.uts_a22202302996.ui.product.ProductViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MouseFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView textViewEmpty;
    private ProductAdapter productAdapter;
    private ProductViewModel productViewModel;

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
        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        // Observe LiveData dari ViewModel
        productViewModel.getMouseProducts().observe(getViewLifecycleOwner(), products -> {
            if (products == null || products.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                textViewEmpty.setVisibility(View.VISIBLE);
            } else {
                textViewEmpty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                productAdapter.setProductList(products);
            }
        });

        productViewModel.fetchMouseProducts("");
        productViewModel.getSearchQuery().observe(getViewLifecycleOwner(), query -> {
            if (query != null) {
                productViewModel.fetchMouseProducts(query);
            }
        });

        productViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                textViewEmpty.setText("Gagal memuat data produk");
                textViewEmpty.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        // Fetch all products initially
        productViewModel.fetchMouseProducts("");

        // Observe search query
        productViewModel.getSearchQuery().observe(getViewLifecycleOwner(), query -> {
            if (query != null) {
                productViewModel.fetchMouseProducts(query);
            }
        });

        return view;
    }
}