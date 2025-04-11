package com.example.uts_a22202302996.product;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.uts_a22202302996.MainActivity;
import com.example.uts_a22202302996.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ProductDetailDialog extends BottomSheetDialogFragment {

    // List untuk menyimpan produk di keranjang
    private ArrayList<Product> listcart;

    // SharedPreferences untuk menyimpan data keranjang
    private SharedPreferences sharedPreferences;

    // Produk yang sedang ditampilkan
    private Product product;

    // Constructor untuk menerima data produk
    public ProductDetailDialog(Product product) {
        this.product = product;
    }

    // Format harga ke dalam format Rupiah
    private String formatRupiah(String harga) {
        try {
            double hargaDouble = Double.parseDouble(harga);
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            return formatRupiah.format(hargaDouble).replace(",00", "");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "Rp. 0";
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout dialog
        View view = inflater.inflate(R.layout.dialog_product_detail, container, false);

        // Inisialisasi SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("product", MODE_PRIVATE);

        // Load data keranjang dari SharedPreferences
        listcart = new ArrayList<>();
        if (sharedPreferences.contains("listproduct")) {
            Gson gson = new Gson();
            String jsonText = sharedPreferences.getString("listproduct", null);
            Product[] carts = gson.fromJson(jsonText, Product[].class);
            for (Product cart : carts) {
                listcart.add(cart);
            }
        }

        // Inisialisasi komponen UI
        ImageView imageView = view.findViewById(R.id.imageViewDetail);
        TextView textViewMerk = view.findViewById(R.id.textViewMerkDetail);
        TextView textViewHarga = view.findViewById(R.id.textViewHargaDetail);
        TextView textViewDeskripsi = view.findViewById(R.id.textViewDeskripsiDetail);
        TextView textViewStok = view.findViewById(R.id.textViewStokDetail);
        ImageButton imageButtonCart = view.findViewById(R.id.imageButtonCart);
        Button buttonClose = view.findViewById(R.id.buttonClose);

        // Set data produk ke komponen UI
        Glide.with(requireContext()).load(product.getFoto()).into(imageView);
        textViewMerk.setText(product.getMerk());
        textViewHarga.setText(formatRupiah(product.getHargaJual()));
        textViewDeskripsi.setText(product.getDeskripsi());
        textViewStok.setText("Stok: " + product.getStok());

        // Tambahkan produk ke keranjang saat tombol cart diklik
        imageButtonCart.setOnClickListener(v -> {
            Gson gson = new Gson();
            String jsonText = sharedPreferences.getString("listproduct", null);
            Type type = new TypeToken<ArrayList<Product>>() {}.getType();
            ArrayList<Product> listcart = gson.fromJson(jsonText, type);

            if (listcart == null) {
                listcart = new ArrayList<>();
            }

            // Periksa apakah produk sudah ada di keranjang
            boolean alreadyExists = false;
            for (Product p : listcart) {
                if (p.getKode().equals(product.getKode())) {
                    p.setQty(p.getQty() + 1); // Tambahkan jumlah produk
                    alreadyExists = true;
                    break;
                }
            }

            // Jika produk belum ada, tambahkan ke keranjang
            if (!alreadyExists) {
                product.setQty(1);
                listcart.add(product);
            }

            // Simpan data keranjang ke SharedPreferences
            String updatedJson = gson.toJson(listcart);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("listproduct", updatedJson);
            editor.apply();

            // Perbarui badge jumlah produk di MainActivity
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateCartBadge();
            }

            Toast.makeText(requireContext(), "Produk ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();
        });

        // Tutup dialog saat tombol close diklik
        buttonClose.setOnClickListener(v -> dismiss());

        return view;
    }
}