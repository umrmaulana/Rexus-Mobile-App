package com.example.uts_a22202302996.ui.cart;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_a22202302996.MainActivity;
import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.activity.CheckoutActivity;
import com.example.uts_a22202302996.adapter.CartAdapter;
import com.example.uts_a22202302996.databinding.FragmentCartBinding;
import com.example.uts_a22202302996.model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding; // Binding untuk mengakses komponen UI
    private SharedPreferences sharedPreferences; // SharedPreferences untuk menyimpan data keranjang

    /**
     * Format harga ke dalam format Rupiah.
     */
    private String formatRupiah(int harga) {
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatRupiah.format(harga).replace(",00", "");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inisialisasi ViewModel
        CartViewModel cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        // Inflate layout fragment
        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set  button checkout listener
        SharedPreferences userPreferences = requireActivity().getSharedPreferences("login_session", MODE_PRIVATE);
        String username = userPreferences.getString("username", "Guest");
        if ("Guest".equals(username)) {
            binding.btnCheckout.setOnClickListener(v -> {
                BottomNavigationView bottomNav = requireActivity().findViewById(R.id.nav_view);
                bottomNav.setSelectedItemId(R.id.navigation_profile);
            });
        } else {
            binding.btnCheckout.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), CheckoutActivity.class);
                // Pass data keranjang ke CheckoutActivity
                startActivity(intent);
            });
        }

        // List untuk menyimpan produk dari keranjang
        ArrayList<Product> listproduct = new ArrayList<>();

        // Load data keranjang dari SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("product", MODE_PRIVATE);
        boolean hasProduct = false;

        if (sharedPreferences.contains("listproduct")) {
            Gson gson = new Gson();
            String jsonText = sharedPreferences.getString("listproduct", null);
            Product[] productArray = gson.fromJson(jsonText, Product[].class);
            if (productArray != null && productArray.length > 0) {
                for (Product product : productArray) {
                    listproduct.add(product);
                }
                Log.i("Info pref", listproduct.toString());
                hasProduct = true;
            }
        }

        // Referensi ke tampilan
        RecyclerView recyclerView = binding.recyclerView;
        LinearLayout conNotFound = binding.conNotFound;

        // Tampilkan tampilan yang sesuai
        if (hasProduct) {
            recyclerView.setVisibility(View.VISIBLE);
            conNotFound.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            conNotFound.setVisibility(View.VISIBLE);
        }

        // Inisialisasi adapter jika ada produk
        CartAdapter adapter = new CartAdapter(listproduct);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.setAdapter(adapter);

        // Listener untuk memperbarui total harga
        adapter.setOnCartChangedListener(total -> {
            TextView tvTotalHarga = binding.tvTotalHarga;
            tvTotalHarga.setText(formatRupiah(total));
        });

        // Listener untuk memperbarui badge jumlah produk
        adapter.setOnCartQtyChangedListener(totalQty -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateCartBadge();
            }
        });

        // Perbarui total harga saat data di-load
        adapter.notifyCartTotalChanged();

        // Atur aksi untuk tombol checkout
        if (!hasProduct) {
            // Jika kosong, nonaktifkan tombol
            binding.btnCheckout.setEnabled(false);
            binding.btnCheckout.setAlpha(0.5f); // tampilan semi transparan
        } else {
            // Jika ada produk, tombol bisa diklik
            if ("Guest".equals(username)) {
                binding.btnCheckout.setOnClickListener(v -> {
                    BottomNavigationView bottomNav = requireActivity().findViewById(R.id.nav_view);
                    bottomNav.setSelectedItemId(R.id.navigation_profile);
                });
            } else {
                binding.btnCheckout.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), CheckoutActivity.class);
                    startActivity(intent);
                });
            }
        }

        // Navigasi dari btnAddItems ke fragment produk
        binding.btnAddItems.setOnClickListener(v -> {
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.nav_view);
            bottomNav.setSelectedItemId(R.id.navigation_product); // atau navigation_product jika ada
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}