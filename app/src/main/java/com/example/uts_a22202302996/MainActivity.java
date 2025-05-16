package com.example.uts_a22202302996;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.uts_a22202302996.databinding.ActivityMainBinding;
import com.example.uts_a22202302996.model.SharedProductViewModel;
import com.example.uts_a22202302996.product.Product;
import com.example.uts_a22202302996.product.ProductDetailFragment;
import com.example.uts_a22202302996.ui.home.HomeFragment;
import com.example.uts_a22202302996.ui.product.ProductFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding; // Binding untuk mengakses komponen UI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate layout menggunakan ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup BottomNavigationView dan Navigation
        setupNavigation();

        // Inisialisasi dan perbarui badge keranjang
        updateCartBadge();

        // Mengatur product detail dari search
        handleSearchIntent(getIntent());

        // Mengatur product dari search
        handleProductIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleSearchIntent(intent);
    }

    /**
     * Mengatur BottomNavigationView dengan NavController.
     */
    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Konfigurasi destinasi utama
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_cart, R.id.navigation_profile, R.id.navigation_product)
                .build();

        // Setup NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    /**
     * Memperbarui badge keranjang dengan jumlah total produk di keranjang.
     */
    public void updateCartBadge() {
        // Ambil data keranjang dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("product", MODE_PRIVATE);
        String jsonText = sharedPreferences.getString("listproduct", null);

        // Dapatkan BottomNavigationView dan BadgeDrawable
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.navigation_cart);

        if (jsonText != null) {
            // Parsing data keranjang
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Product>>() {}.getType();
            ArrayList<Product> listProduct = gson.fromJson(jsonText, type);

            // Hitung jumlah total produk
            int totalQty = 0;
            for (Product product : listProduct) {
                totalQty += product.getQty();
            }

            // Perbarui visibilitas dan angka pada badge
            if (totalQty > 0) {
                badge.setVisible(true);
                badge.setNumber(totalQty);
            } else {
                badge.clearNumber();
                badge.setVisible(false);
            }
        } else {
            // Sembunyikan badge jika keranjang kosong atau null
            badge.clearNumber();
            badge.setVisible(false);
        }
    }

    /**
     * Mengatur produk dari intent pencarian.
     *
     * @param intent Intent yang berisi data produk.
     */
    private void handleSearchIntent(Intent intent) {
        if (intent != null && intent.hasExtra("product")) {
            Product product = (Product) intent.getSerializableExtra("product");

            if (product != null) {
                // Simpan ke Shared ViewModel
                SharedProductViewModel viewModel = new ViewModelProvider(this).get(SharedProductViewModel.class);
                viewModel.selectProduct(product);

                // Pindah ke tab Product
                BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
                bottomNavigationView.setSelectedItemId(R.id.navigation_product);
            }
        }
    }

    /**
     * Mengatur banyak produk dari intent pencarian.
     */
    private void handleProductIntent() {
        if (getIntent() != null && getIntent().hasExtra("selected_products")) {
            ArrayList<Product> selectedProducts = (ArrayList<Product>) getIntent().getSerializableExtra("selected_products");
            String navigateTo = getIntent().getStringExtra("navigate_to");

            if ("product".equals(navigateTo)) {
                // Kirim ke Shared ViewModel
                SharedProductViewModel viewModel = new ViewModelProvider(this).get(SharedProductViewModel.class);
                viewModel.setSelectedProductList(selectedProducts);

                // Navigasi ke tab product melalui BottomNavigationView
                BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
                bottomNavigationView.setSelectedItemId(R.id.navigation_product);
            }
        }
    }
}