package com.example.uts_a22202302996.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.adapter.ProductAdapter;
import com.example.uts_a22202302996.ui.product.ProductViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProductListFragment extends Fragment {
    private static final String ARG_CATEGORY = "category";

    private String category;
    private RecyclerView recyclerView;
    private TextView textViewEmpty;
    private ProductAdapter productAdapter;
    private ProductViewModel productViewModel;

    public static ProductListFragment newInstance(String category) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY, "all");
        } else {
            category = "all";
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        productAdapter = new ProductAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(productAdapter);

        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        // Observe produk sesuai kategori
        productViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                textViewEmpty.setText(error);
                recyclerView.setVisibility(View.GONE);
                textViewEmpty.setVisibility(View.VISIBLE);
            }
        });

        // Panggil fetch sesuai kategori
        fetchProductsByCategory();

        return view;
    }

    private void fetchProductsByCategory() {
        productViewModel.getProductsForCategory(category)
                .observe(getViewLifecycleOwner(), products -> {
                    if (products == null || products.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        textViewEmpty.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        textViewEmpty.setVisibility(View.GONE);
                        productAdapter.setProductList(products);
                    }
                });
    }
}
