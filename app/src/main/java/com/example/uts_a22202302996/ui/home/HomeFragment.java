package com.example.uts_a22202302996.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.SearchActivity;
import com.example.uts_a22202302996.adapter.ProductAdapter;
import com.example.uts_a22202302996.adapter.SearchAdapter;
import com.example.uts_a22202302996.adapter.ViewPagerAdapter;
import com.example.uts_a22202302996.databinding.FragmentHomeBinding;
import com.example.uts_a22202302996.product.AllProductsFragment;
import com.example.uts_a22202302996.product.HeadsetFragment;
import com.example.uts_a22202302996.product.KeyboardFragment;
import com.example.uts_a22202302996.product.MouseFragment;
import com.example.uts_a22202302996.product.Product;
import com.google.android.material.tabs.TabLayout;


import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ViewPagerAdapter viewPagerAdapter;
    private View searchViewLayout;
    private SearchView searchView;
    private RecyclerView recyclerViewSearch;
    private List<String> itemList;
    private SearchAdapter searchAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        Bundle arguments = getArguments();
//        if (arguments != null && arguments.containsKey("selected_product")) {
//            Product selectedProduct = (Product) arguments.getSerializable("selected_product");
//
//            // Hide the ViewPager and TabLayout
//            binding.tabLayout.setVisibility(View.GONE);
//            binding.viewPager.setVisibility(View.GONE);
//
//            // Display the selected product
//            displaySelectedProduct(selectedProduct);
//        } else {
//            setupViewPager();
//        }

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("selected_products")) {
            List<Product> selectedProducts = (List<Product>) arguments.getSerializable("selected_products");
            displaySelectedProducts(selectedProducts);
        } else {
            setupViewPager();
        }
        ImageButton btnSearch = binding.header.findViewById(R.id.searchIcon);
        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SearchActivity.class);
            startActivity(intent);
        });
        return root;
    }

    private void displaySelectedProducts(List<Product> products) {
        // Sembunyikan ViewPager dan TabLayout
        binding.tabLayout.setVisibility(View.GONE);
        binding.viewPager.setVisibility(View.GONE);

        // Pastikan toolbar tetap terlihat
        Toolbar toolbar = binding.header.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);

        // Tampilkan produk yang dipilih
        RecyclerView recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // Atur margin top untuk RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(10, 200, 10, 200); // marginLeft, marginTop, marginRight, marginBottom
        recyclerView.setLayoutParams(layoutParams);

        ProductAdapter adapter = new ProductAdapter(this, products);
        recyclerView.setAdapter(adapter);

        // Tambahkan RecyclerView ke layout
        binding.homeContainer.addView(recyclerView);
    }

    private void displaySelectedProduct(Product product) {
        // Sembunyikan ViewPager dan TabLayout
        binding.tabLayout.setVisibility(View.GONE);
        binding.viewPager.setVisibility(View.GONE);

        // Pastikan toolbar tetap terlihat
        Toolbar toolbar = binding.header.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);

        // Tampilkan produk yang dipilih
        RecyclerView recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // Atur margin top untuk RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(10, 200, 10, 10); // marginLeft, marginTop, marginRight, marginBottom
        recyclerView.setLayoutParams(layoutParams);

        List<Product> productList = new ArrayList<>();
        productList.add(product);

        ProductAdapter adapter = new ProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        // Tambahkan RecyclerView ke layout
        binding.homeContainer.addView(recyclerView);
    }

    private void setupViewPager() {
        TabLayout tabLayout = binding.tabLayout;
        ViewPager viewPager = binding.viewPager;

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new AllProductsFragment(), "All");
        viewPagerAdapter.addFragment(new KeyboardFragment(), "Keyboard");
        viewPagerAdapter.addFragment(new MouseFragment(), "Mouse");
        viewPagerAdapter.addFragment(new HeadsetFragment(), "Headset");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}