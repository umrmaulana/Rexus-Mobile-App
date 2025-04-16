package com.example.uts_a22202302996.adapter;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.product.Product;
import com.example.uts_a22202302996.product.ProductDetailDialog;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Fragment fragment;

    // Constructor
    public ProductAdapter(Fragment fragment, List<Product> productList) {
        this.fragment = fragment;
        this.productList = productList;
    }

    // Fungsi format harga ke dalam bentuk Rupiah (Indonesia)
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

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout setiap item produk
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set data ke tampilan
        holder.textViewMerk.setText(product.getMerk());

        // Format harga
        String hargaJual = formatRupiah(String.valueOf(product.getHargaJual()));
        String hargaPokok = formatRupiah(String.valueOf(product.getHargapokok()));

        // Cek diskon
        if (product.getDiskonJual() > 0) {
            // Tampilkan harga asli dengan coretan dan harga diskon
            holder.textViewHargaJual.setPaintFlags(holder.textViewHargaJual.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textViewHargaJual.setTextSize(16);
            holder.textViewHargaJual.setTypeface(null, Typeface.NORMAL);
            holder.textViewHargaJual.setText(hargaPokok);
            holder.textViewHargaJualDiskon.setVisibility(View.VISIBLE);
            holder.textViewHargaJualDiskon.setText(hargaJual);
        } else {
            // Tanpa diskon
            holder.textViewHargaJual.setPaintFlags(holder.textViewHargaJual.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.textViewHargaJual.setText(hargaJual);
            holder.textViewHargaJualDiskon.setVisibility(View.GONE);
        }

        // Cek stok (Sold Out)
        if (product.getStok() <= 0) {
            holder.imageViewStatus.setVisibility(View.VISIBLE);
            holder.imageViewProduct.setAlpha(0.5f); // Reduce opacity
        } else {
            holder.imageViewStatus.setVisibility(View.GONE);
            holder.imageViewProduct.setAlpha(1.0f);
        }

        // Load gambar menggunakan Glide
        Glide.with(fragment.getContext())
                .load(product.getFoto())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.imageViewProduct);

        // Klik item untuk membuka detail produk
        holder.itemView.setOnClickListener(v -> {
            ProductDetailDialog dialog = new ProductDetailDialog(product);
            dialog.show(fragment.getChildFragmentManager(), "ProductDetailDialog");
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ViewHolder untuk menyimpan referensi elemen UI
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProduct, imageViewStatus;
        TextView textViewMerk, textViewHargaJual, textViewHargaJualDiskon;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            imageViewStatus = itemView.findViewById(R.id.imageViewStatus);
            textViewMerk = itemView.findViewById(R.id.textViewMerk);
            textViewHargaJual = itemView.findViewById(R.id.textViewHargaJual);
            textViewHargaJualDiskon = itemView.findViewById(R.id.textViewHargaJualDiskon);
        }
    }

    // HomeViewModel
    public void setProductList(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }

}
