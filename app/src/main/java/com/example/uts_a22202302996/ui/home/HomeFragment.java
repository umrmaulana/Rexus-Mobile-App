package com.example.uts_a22202302996.ui.home;

import static com.example.uts_a22202302996.auth.LoginActivity.URL;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.SearchActivity;
import com.example.uts_a22202302996.adapter.ProductAdapter;
import com.example.uts_a22202302996.databinding.FragmentHomeBinding;
import com.example.uts_a22202302996.model.SharedProductViewModel;
import com.example.uts_a22202302996.ui.product.ProductFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private HomeViewModel homeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up User Profile
        SharedPreferences sharedPreferences = requireActivity()
                .getSharedPreferences("login_session", Context.MODE_PRIVATE);
        String nama = sharedPreferences.getString("nama", "Guest");
        String foto = sharedPreferences.getString("foto", "");
        binding.userName.setText(nama);
        Glide.with(this)
                .load(URL + "images/" + foto)
                .circleCrop()
                .error(R.drawable.ic_launcher_foreground)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(binding.imgProfile);
        binding.imgProfile.setOnClickListener(v -> {
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.nav_view);
            bottomNav.setSelectedItemId(R.id.navigation_profile);
        });


        //Set up Search
        CardView btnSearch = binding.searchCard.findViewById(R.id.searchCard);
        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SearchActivity.class);
            startActivity(intent);
        });

        // Set up ImageSlider
        ImageSlider imageSlider = binding.imageSlider.findViewById(R.id.image_slider);
        List<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel("https://images.unsplash.com/photo-1677631231234-1234567890", ScaleTypes.FIT));
        imageList.add(new SlideModel("https://images.unsplash.com/photo-1677631231234-1234567890", ScaleTypes.FIT));
        imageList.add(new SlideModel("https://images.unsplash.com/photo-1677631231234-1234567890", ScaleTypes.FIT));
        imageList.add(new SlideModel("https://images.unsplash.com/photo-1677631231234-1234567890", ScaleTypes.FIT));
        imageSlider.setImageList(imageList, ScaleTypes.FIT);

        // Set up Product Categories
        SharedProductViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedProductViewModel.class);

        View allProductsButton = root.findViewById(R.id.allProducts);
        allProductsButton.setOnClickListener(v -> {
            viewModel.selectCategory("all");
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.nav_view);
            bottomNav.setSelectedItemId(R.id.navigation_product);
        });

        View headsetProductsButton = root.findViewById(R.id.headsetProducts);
        headsetProductsButton.setOnClickListener(v -> {
            viewModel.selectCategory("headset");
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.nav_view);
            bottomNav.setSelectedItemId(R.id.navigation_product);
        });

        View mouseProductsButton = root.findViewById(R.id.mouseProducts);
        mouseProductsButton.setOnClickListener(v -> {
            viewModel.selectCategory("mouse");
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.nav_view);
            bottomNav.setSelectedItemId(R.id.navigation_product);
        });

        View keyboardProductsButton = root.findViewById(R.id.keyboardProducts);
        keyboardProductsButton.setOnClickListener(v -> {
            viewModel.selectCategory("keyboard");
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.nav_view);
            bottomNav.setSelectedItemId(R.id.navigation_product);
        });

        // Set up Populer Products
        recyclerView = binding.popularProductsRecyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        productAdapter = new ProductAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(productAdapter);

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        // Observe LiveData
        homeViewModel.getPopulerProducts().observe(getViewLifecycleOwner(), products -> {
            productAdapter.setProductList(products);
        });

        homeViewModel.fetchPopularProducts();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

