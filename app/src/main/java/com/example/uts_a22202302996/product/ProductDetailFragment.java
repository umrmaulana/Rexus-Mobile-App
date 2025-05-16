package com.example.uts_a22202302996.product;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.uts_a22202302996.MainActivity;
import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.ui.product.ProductFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import androidx.activity.OnBackPressedCallback;
import androidx.navigation.fragment.NavHostFragment;

public class ProductDetailFragment extends Fragment {

    private static final String ARG_PRODUCT = "product";

    private ArrayList<Product> listcart;
    private SharedPreferences sharedPreferences, productView;
    private Product product;
    int currentViewCount = 0;

    public static ProductDetailFragment newInstance(Product product) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("product", product);
        fragment.setArguments(args);
        return fragment;
    }


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
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        sharedPreferences = requireContext().getSharedPreferences("product", MODE_PRIVATE);
        productView = requireContext().getSharedPreferences("product_views", MODE_PRIVATE);

        // Retrieve the product object from arguments
        if (getArguments() != null) {
            product = (Product) getArguments().getSerializable("product");
        }

        if (product == null) {
            Toast.makeText(requireContext(), "Product data is missing", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Retrieve the current view count for this product
        String productKey = "view_count_" + product.getKode();
        int savedViewCount = productView.getInt(productKey, 0);

        // Update the TextView with the saved view count
        TextView txView = view.findViewById(R.id.txView);
        txView.setText("view : " + savedViewCount);

        // Initialize cart list
        listcart = new ArrayList<>();

        // Check if cart data exists in SharedPreferences
        if (sharedPreferences.contains("listproduct")) {
            Gson gson = new Gson();
            String jsonText = sharedPreferences.getString("listproduct", null);
            if (jsonText != null) {
                Type type = new TypeToken<ArrayList<Product>>() {}.getType();
                listcart = gson.fromJson(jsonText, type);
            }
        }

        // Initialize views
        ImageView imageView = view.findViewById(R.id.imageViewDetail);
        ImageView imageViewStatus = view.findViewById(R.id.imageViewStatus);
        TextView textViewMerk = view.findViewById(R.id.textViewMerkDetail);
        TextView textViewKategori = view.findViewById(R.id.textViewKategori);
        TextView textViewHarga = view.findViewById(R.id.textViewHargaDetail);
        TextView textViewHargaDiskon = view.findViewById(R.id.textViewHargaDiskon);
        TextView textViewDeskripsi = view.findViewById(R.id.textViewDeskripsiDetail);
        TextView textViewStok = view.findViewById(R.id.textViewStokDetail);
        TextView textViewDiscountBadge = view.findViewById(R.id.textViewDiscountBadge);
        ImageButton imageButtonCart = view.findViewById(R.id.imageButtonCart);
        ImageButton imageButtonBack = view.findViewById(R.id.imageButtonBack);

        imageButtonBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);

            if (getActivity() != null) {
                navController.popBackStack(); // kembali ke HomeFragment
            }
            if (!navController.popBackStack()) {
                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish();
            }

        });

        // Set product data
        Glide.with(requireContext()).load(product.getFoto()).into(imageView);
        textViewMerk.setText(product.getMerk());
        textViewKategori.setText(product.getKategori());
        textViewDeskripsi.setText(product.getDeskripsi());
        textViewStok.setText("Stok: " + product.getStok());

        // Handle stock status
        if (product.getStok() <= 0) {
            imageViewStatus.setVisibility(View.VISIBLE);
            imageButtonCart.setEnabled(false); // Disable cart button when out of stock
            imageButtonCart.setAlpha(0.5f); // Make cart button semi-transparent
            imageView.setAlpha(0.5f); // Make image view semi-transparent
        } else {
            imageViewStatus.setVisibility(View.GONE);
            imageButtonCart.setEnabled(true);
            imageButtonCart.setAlpha(1f);
        }

        // Handle price display based on discount
        if (product.getDiskonJual() > 0) {
            // Show original price with strike-through
            textViewHarga.setPaintFlags(textViewHarga.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textViewHarga.setTextColor(getResources().getColor(R.color.error)); // Assuming you have red color defined
            textViewHarga.setTextSize(14f); // Smaller size for original price
            textViewHarga.setText(formatRupiah(String.valueOf(product.getHargapokok())));

            // Show discounted price
            textViewHargaDiskon.setVisibility(View.VISIBLE);
            textViewHargaDiskon.setText(formatRupiah(String.valueOf(product.getHargaJual())));
            textViewHargaDiskon.setTextColor(getResources().getColor(R.color.primary));
            textViewHargaDiskon.setTextSize(18f); // Larger size for discounted price
            textViewHargaDiskon.setTypeface(null, android.graphics.Typeface.BOLD);
            textViewDiscountBadge.setVisibility(View.VISIBLE);
            imageButtonBack.setVisibility(View.VISIBLE);

        } else {
            // No discount - show only original price
            textViewHarga.setPaintFlags(textViewHarga.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            textViewHarga.setTextColor(getResources().getColor(R.color.primary));
            textViewHarga.setTextSize(18f);
            textViewHarga.setTypeface(null, android.graphics.Typeface.BOLD);
            textViewHarga.setText(formatRupiah(String.valueOf(product.getHargaJual())));
            textViewHargaDiskon.setVisibility(View.GONE);
            textViewDiscountBadge.setVisibility(View.GONE);
            imageButtonBack.setVisibility(View.VISIBLE);
        }

        imageButtonCart.setOnClickListener(v -> {
            if (product.getStok() == 0) {
                Toast.makeText(requireContext(), "Produk habis, tidak dapat ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();
                return;
            }

            Gson gson = new Gson();
            String jsonText = sharedPreferences.getString("listproduct", null);
            Type type = new TypeToken<ArrayList<Product>>() {}.getType();
            ArrayList<Product> listcart = gson.fromJson(jsonText, type);

            if (listcart == null) {
                listcart = new ArrayList<>();
            }

            boolean alreadyExists = false;
            for (Product p : listcart) {
                if (p.getKode().equals(product.getKode())) {
                    if (p.getQty() >= product.getStok()) {
                        Toast.makeText(requireContext(), "Stok tidak mencukupi", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    p.setQty(p.getQty() + 1);
                    alreadyExists = true;
                    break;
                }
            }

            if (!alreadyExists) {
                product.setQty(1);
                listcart.add(product);
            }

            String updatedJson = gson.toJson(listcart);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("listproduct", updatedJson);
            editor.apply();

            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateCartBadge();
            }

            Toast.makeText(requireContext(), "Produk ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                navController.popBackStack(); // Kembali ke fragment sebelumnya
            }
        });
    }
}