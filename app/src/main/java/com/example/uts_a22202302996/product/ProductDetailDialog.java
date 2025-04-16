package com.example.uts_a22202302996.product;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Paint;
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

    private ArrayList<Product> listcart;
    private SharedPreferences sharedPreferences;
    private Product product;

    public ProductDetailDialog(Product product) {
        this.product = product;
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
        View view = inflater.inflate(R.layout.dialog_product_detail, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("product", MODE_PRIVATE);
        listcart = new ArrayList<>();

        if (sharedPreferences.contains("listproduct")) {
            Gson gson = new Gson();
            String jsonText = sharedPreferences.getString("listproduct", null);
            Product[] carts = gson.fromJson(jsonText, Product[].class);
            for (Product cart : carts) {
                listcart.add(cart);
            }
        }

        // Initialize views
        ImageView imageView = view.findViewById(R.id.imageViewDetail);
        ImageView imageViewStatus = view.findViewById(R.id.imageViewStatus);
        TextView textViewMerk = view.findViewById(R.id.textViewMerkDetail);
        TextView textViewHarga = view.findViewById(R.id.textViewHargaDetail);
        TextView textViewHargaDiskon = view.findViewById(R.id.textViewHargaDiskon);
        TextView textViewDeskripsi = view.findViewById(R.id.textViewDeskripsiDetail);
        TextView textViewStok = view.findViewById(R.id.textViewStokDetail);
        ImageButton imageButtonCart = view.findViewById(R.id.imageButtonCart);
        Button buttonClose = view.findViewById(R.id.buttonClose);

        // Set product data
        Glide.with(requireContext()).load(product.getFoto()).into(imageView);
        textViewMerk.setText(product.getMerk());
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
            textViewHarga.setText(formatRupiah(String.valueOf(product.getHargaJual())));

            // Show discounted price
            textViewHargaDiskon.setVisibility(View.VISIBLE);
            textViewHargaDiskon.setText(formatRupiah(String.valueOf(product.getHargapokok())));
            textViewHargaDiskon.setTextColor(getResources().getColor(R.color.primary));
            textViewHargaDiskon.setTextSize(18f); // Larger size for discounted price
            textViewHargaDiskon.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            // No discount - show only original price
            textViewHarga.setPaintFlags(textViewHarga.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            textViewHarga.setTextColor(getResources().getColor(R.color.primary));
            textViewHarga.setTextSize(18f);
            textViewHarga.setTypeface(null, android.graphics.Typeface.BOLD);
            textViewHarga.setText(formatRupiah(String.valueOf(product.getHargaJual())));
            textViewHargaDiskon.setVisibility(View.GONE);
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

        buttonClose.setOnClickListener(v -> dismiss());

        return view;
    }
}