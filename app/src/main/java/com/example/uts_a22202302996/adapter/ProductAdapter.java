package com.example.uts_a22202302996.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.product.Product;
import com.example.uts_a22202302996.product.ProductDetailDialog;

import java.text.NumberFormat;
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
        holder.textViewHargaJual.setText(formatRupiah(product.getHargaJual()));

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
        ImageView imageViewProduct;
        TextView textViewMerk;
        TextView textViewHargaJual;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewMerk = itemView.findViewById(R.id.textViewMerk);
            textViewHargaJual = itemView.findViewById(R.id.textViewHargaJual);
        }
    }

    // HomeViewModel
    public void setProductList(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }

}
