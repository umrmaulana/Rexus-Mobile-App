package com.example.uts_a22202302996.ui.product;

import static android.view.View.GONE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.adapter.ViewPagerAdapter;
import com.example.uts_a22202302996.api.CategoryResponse;
import com.example.uts_a22202302996.api.RegisterAPI;
import com.example.uts_a22202302996.api.ServerAPI;
import com.example.uts_a22202302996.databinding.FragmentProductBinding;
import com.example.uts_a22202302996.model.SharedProductViewModel;
import com.example.uts_a22202302996.product.CategoryResultFragment;
import com.example.uts_a22202302996.product.Product;
import com.example.uts_a22202302996.product.ProductDetailFragment;
import com.example.uts_a22202302996.product.ProductListFragment;
import com.example.uts_a22202302996.api.ProductResponse;
import com.example.uts_a22202302996.product.SearchResultFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFragment extends Fragment {

    private FragmentProductBinding binding;
    private ViewPagerAdapter viewPagerAdapter;
    private boolean hasOpenedProductDetail = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProductBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        // Set up ViewPager and TabLayout
        setupViewPager();

        // Set up SharedProductViewModel
        SharedProductViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedProductViewModel.class);

        // Set up selected category
        viewModel.getSelectedCategory().observe(getViewLifecycleOwner(), category -> {
            if (category != null) {
                binding.toolbarTitle.setText(category.toUpperCase());
                displayCategoriesProducts(category);
                viewModel.selectCategory(null);
            }
        });


        // Set up Selected Product Item
        viewModel.getSelectedProduct().observe(getViewLifecycleOwner(), product -> {
            if (product != null && !hasOpenedProductDetail) {
                hasOpenedProductDetail = true;
                openProductDetailFragment(product);
                viewModel.selectProduct(null);
            }
        });

        // Set up Selected Product List Search
        viewModel.getSelectedProductList().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                binding.toolbarTitle.setText("Search Result");
                displaySelectedProducts(products);
                viewModel.setSelectedProductList(null);
            }
        });

        return root;
    }

    //  Fetch products by category
    private void displayCategoriesProducts(String category) {
        View productContent = binding.getRoot().findViewById(R.id.productContent);
        if (productContent != null) {
            productContent.setVisibility(View.GONE);
        }

        Fragment existingFragment = getChildFragmentManager().findFragmentById(R.id.productContainer);
        if (existingFragment != null && !(existingFragment instanceof SearchResultFragment)) {
            getChildFragmentManager().beginTransaction().remove(existingFragment).commit();
        }

        fetchProductsByCategory(category); // panggil API dulu
    }

    //  Fetch products by category from API
    private void fetchProductsByCategory(String category) {
        RegisterAPI apiService = ServerAPI.getClient().create(RegisterAPI.class);
        Call<ProductResponse> call = apiService.getCategoryProducts(category);

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<Product> categories = new ArrayList<>(response.body().getResult());
                    Log.d("ProductFragment", "Products: " + categories.toString());

                    CategoryResultFragment fragment = CategoryResultFragment.newInstance(categories);
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.productContainer, fragment)
                            .commit();
                } else {
                    Toast.makeText(getContext(), "Gagal mengambil produk", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    //  Open ProductDetailFragment
    public void openProductDetailFragment(Product product) {
        ProductDetailFragment detailFragment = ProductDetailFragment.newInstance(product);

        binding.toolbar.setVisibility(GONE);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.productContainer, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    //  Display selected products from search
    private void displaySelectedProducts(List<Product> products) {
        View productContent = binding.getRoot().findViewById(R.id.productContent);
        if (productContent != null) {
            productContent.setVisibility(View.GONE);
        }

        Fragment existingFragment = getChildFragmentManager().findFragmentById(R.id.productContainer);
        if (existingFragment != null && !(existingFragment instanceof SearchResultFragment)) {
            getChildFragmentManager().beginTransaction().remove(existingFragment).commit();
        }

        getChildFragmentManager().beginTransaction()
                .replace(R.id.productContainer, SearchResultFragment.newInstance(new ArrayList<>(products)))
                .commit();
    }


    private void setupViewPager() {
        Fragment existingFragment = getChildFragmentManager().findFragmentById(R.id.productContainer);
        if (existingFragment != null && !(existingFragment instanceof ProductDetailFragment)) {
            getChildFragmentManager().beginTransaction().remove(existingFragment).commit();
        }

        View productContent = binding.getRoot().findViewById(R.id.productContent);
        if (productContent != null) {
            productContent.setVisibility(View.VISIBLE);
        }

        TabLayout tabLayout = binding.tabLayout;
        ViewPager viewPager = binding.viewPager;
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        RegisterAPI apiService = ServerAPI.getClient().create(RegisterAPI.class);
        Call<CategoryResponse> call = apiService.getCategories();

        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> categories = response.body().getCategories();

                    // Tambahkan tab "All" di awal
                    viewPagerAdapter.addFragment(ProductListFragment.newInstance("all"), "All");

                    for (String category : categories) {
                        viewPagerAdapter.addFragment(ProductListFragment.newInstance(category), capitalize(category));
                    }

                    viewPager.setAdapter(viewPagerAdapter);
                    tabLayout.setupWithViewPager(viewPager);
                } else {
                    Toast.makeText(getContext(), "Gagal memuat kategori", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return "";
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        hasOpenedProductDetail = false;
    }
}
