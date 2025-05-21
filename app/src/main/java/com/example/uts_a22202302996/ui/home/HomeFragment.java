package com.example.uts_a22202302996.ui.home;

import static com.example.uts_a22202302996.api.ServerAPI.BASE_URL_IMAGE;
import static com.example.uts_a22202302996.auth.LoginActivity.URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.SearchActivity;
import com.example.uts_a22202302996.adapter.ProductAdapter;
import com.example.uts_a22202302996.api.RegisterAPI;
import com.example.uts_a22202302996.api.ServerAPI;
import com.example.uts_a22202302996.databinding.FragmentHomeBinding;
import com.example.uts_a22202302996.model.SharedProductViewModel;
import com.example.uts_a22202302996.ui.product.ProductFragment;
import com.example.uts_a22202302996.ui.profile.ProfileViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private HomeViewModel homeViewModel;
    private ProfileViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        loadProfileData();

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
        imageList.add(new SlideModel(R.drawable.slider1, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.slider2, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.slider3, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.slider4, ScaleTypes.FIT));
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                binding.userName.setText(profile.nama);
                Glide.with(this)
                        .load(ServerAPI.BASE_URL_IMAGE + "avatar/" + profile.foto)
                        .apply(new RequestOptions().circleCrop())
                        .into(binding.imgProfile);
            }
        });

        binding.imgProfile.setOnClickListener(v -> {
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.nav_view);
            bottomNav.setSelectedItemId(R.id.navigation_profile);
        });
    }

    private void loadProfileData() {
        // Ambil username dari SharedPreferences (hanya untuk inisialisasi)
        SharedPreferences sharedPreferences = requireActivity()
                .getSharedPreferences("login_session", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        // Tampilkan loading indicator
        binding.loadingProgressBar.setVisibility(View.VISIBLE);

        ServerAPI urlAPI = new ServerAPI();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urlAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        api.getProfile(username).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                binding.loadingProgressBar.setVisibility(View.GONE);
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        if (json.getInt("result") == 1) {
                            JSONObject data = json.getJSONObject("data");
                            updateUI(
                                    data.getString("nama"),
                                    data.getString("foto")
                            );
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Gagal memuat data profil");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                binding.loadingProgressBar.setVisibility(View.GONE);
                showError("Koneksi gagal: " + t.getMessage());
            }
        });
    }

    private void updateUI(String nama, String foto) {
        binding.userName.setText(nama);
        Glide.with(this)
                .load(BASE_URL_IMAGE + "avatar/" + foto)
                .apply(new RequestOptions()
                        .circleCrop()
                        .error(R.drawable.ic_user_white)
                        .placeholder(R.drawable.ic_user_white)
                        .diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(binding.imgProfile);
    }

    private void showError(String message) {
        if (isAdded()) { // Check if the fragment is attached to an activity
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        } else {
            Log.e("ProfileFragment", "Fragment not attached to context. Error: " + message);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfileData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

