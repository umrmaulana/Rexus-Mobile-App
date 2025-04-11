package com.example.uts_a22202302996.adapter;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.product.Product;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    ArrayList<Product> listProduct;

    // Constructor adapter
    public CartAdapter(ArrayList<Product> listProduct) {
        this.listProduct = listProduct;
    }

    // Format angka ke bentuk rupiah
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item keranjang
        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_product_cart, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = listProduct.get(position);

        // Tampilkan gambar produk
        Glide.with(holder.itemView.getContext())
                .load(product.getFoto())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.imageViewProduct);

        holder.tvProduct.setText(product.getMerk());

        // Hitung harga total per produk
        int hargaJual = Integer.parseInt(product.getHargaJual());
        holder.tvPrice.setText(formatRupiah(String.valueOf(hargaJual * product.getQty())));
        holder.tvQty.setText(String.valueOf(product.getQty()));

        // Tombol tambah quantity
        holder.btnPlus.setOnClickListener(v -> {
            product.setQty(product.getQty() + 1);
            holder.tvQty.setText(String.valueOf(product.getQty()));
            holder.tvPrice.setText(formatRupiah(String.valueOf(hargaJual * product.getQty())));
            updateSharedPreferences(v, listProduct);
            notifyCartTotalChanged();
            notifyCartQtyChanged();
        });

        // Tombol kurangi quantity
        holder.btnMinus.setOnClickListener(v -> {
            if (product.getQty() > 1) {
                product.setQty(product.getQty() - 1);
                holder.tvQty.setText(String.valueOf(product.getQty()));
                holder.tvPrice.setText(formatRupiah(String.valueOf(hargaJual * product.getQty())));
                updateSharedPreferences(v, listProduct);
                notifyCartTotalChanged();
                notifyCartQtyChanged();
            } else {
                Toast.makeText(v.getContext(), "Minimal quantity 1", Toast.LENGTH_SHORT).show();
            }
        });

        // Tombol hapus produk dari keranjang
        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Product removedProduct = listProduct.get(pos);

                // Dialog konfirmasi
                new android.app.AlertDialog.Builder(v.getContext())
                        .setTitle("Konfirmasi Hapus")
                        .setMessage("Apakah kamu yakin ingin menghapus " + removedProduct.getMerk() + " dari keranjang?")
                        .setPositiveButton("Hapus", (dialog, which) -> {
                            listProduct.remove(pos);
                            notifyItemRemoved(pos);
                            notifyItemRangeChanged(pos, listProduct.size());

                            // Update SharedPreferences
                            updateSharedPreferences(v, listProduct);
                            notifyCartTotalChanged();
                            notifyCartQtyChanged();

                            Toast.makeText(v.getContext(), "Berhasil menghapus: " + removedProduct.getMerk(), Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            }
        });
    }

    // Simpan data keranjang ke SharedPreferences
    private void updateSharedPreferences(View v, ArrayList<Product> list) {
        SharedPreferences sharedPreferences = v.getContext()
                .getSharedPreferences("product", v.getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String jsonText = new Gson().toJson(list);
        editor.putString("listproduct", jsonText);
        editor.apply();
    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    // ViewHolder class untuk item keranjang
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProduct, tvPrice, tvQty;
        ImageButton btnDelete;
        Button btnPlus, btnMinus;
        ImageView imageViewProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProduct = itemView.findViewById(R.id.tvProduct);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQty = itemView.findViewById(R.id.tvQty);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
        }
    }

    // Listener untuk menghitung total harga semua item
    public interface OnCartChangedListener {
        void onCartTotalChanged(int total);
    }

    private OnCartChangedListener cartChangedListener;

    public void setOnCartChangedListener(OnCartChangedListener listener) {
        this.cartChangedListener = listener;
    }

    public void notifyCartTotalChanged() {
        if (cartChangedListener != null) {
            int total = 0;
            for (Product p : listProduct) {
                total += Integer.parseInt(p.getHargaJual()) * p.getQty();
            }
            cartChangedListener.onCartTotalChanged(total);
        }
    }

    // Listener untuk menghitung total jumlah produk
    public interface OnCartQtyChangedListener {
        void onCartQtyChanged(int totalQty);
    }

    private OnCartQtyChangedListener cartQtyChangedListener;

    public void setOnCartQtyChangedListener(OnCartQtyChangedListener listener) {
        this.cartQtyChangedListener = listener;
    }

    public void notifyCartQtyChanged() {
        if (cartQtyChangedListener != null) {
            int totalQty = 0;
            for (Product p : listProduct) {
                totalQty += p.getQty();
            }
            cartQtyChangedListener.onCartQtyChanged(totalQty);
        }
    }
}
