package com.example.uts_a22202302996.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.adapter.ProductAdapter;
import com.example.uts_a22202302996.adapter.ProductSkeletonAdapter;
import com.example.uts_a22202302996.model.Product;

import java.util.ArrayList;

public class CategoryResultFragment extends Fragment {

    private static final String ARG_PRODUCTS = "arg_products";
    private ProductAdapter productAdapter;
    private ArrayList<Product> productList;

    public CategoryResultFragment() {
        // Required empty public constructor
    }

    public static CategoryResultFragment newInstance(ArrayList<Product> categories) {
        CategoryResultFragment fragment = new CategoryResultFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCTS, categories);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productList = (ArrayList<Product>) getArguments().getSerializable(ARG_PRODUCTS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.product_fragment, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new ProductAdapter(CategoryResultFragment.this, new ArrayList<>());
        recyclerView.setAdapter(new ProductSkeletonAdapter(10));
        new android.os.Handler().postDelayed(() -> {
            recyclerView.setAdapter(productAdapter);
            if (productList != null) {
                productAdapter.setProductList(productList);
            }
        }, 1500);
//        recyclerView.setAdapter(productAdapter);

//        if (productList != null) {
//            productAdapter.setProductList(productList);
//        }

        return view;
    }
}