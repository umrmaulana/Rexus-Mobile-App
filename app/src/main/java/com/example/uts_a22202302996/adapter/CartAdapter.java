package com.example.uts_a22202302996.adapter;

import static com.example.uts_a22202302996.api.ServerAPI.BASE_URL_IMAGE;

import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

    // In CartFragment
    private String formatRupiah(double harga) {
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatRupiah.format(harga).replace(",00", "");
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

        // Get prices with null safety
        double hargaJual = product.getHargaJual();
        double hargaPokok = product.getHargapokok();
        double totalHarga = hargaJual * product.getQty();

        // Load product image
        Glide.with(holder.itemView.getContext())
                .load(BASE_URL_IMAGE+ "product/" +product.getFoto())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.imageViewProduct);

        // Set product name and quantity
        holder.tvProduct.setText(product.getMerk() != null ? product.getMerk() : "");
        holder.tvQty.setText(String.valueOf(product.getQty()));

        // Handle price display based on discount
        if (product.getDiskonJual() > 0) {
            // Show original price with strike-through
            holder.tvPrice.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvPrice.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.error));
            holder.tvPrice.setTextSize(14f);
            holder.tvPrice.setText(formatRupiah(hargaPokok * product.getQty()));

            // Show discounted price
            holder.tvHargaDiskon.setVisibility(View.VISIBLE);
            holder.tvHargaDiskon.setText(formatRupiah(totalHarga));
            holder.tvHargaDiskon.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary));
            holder.tvHargaDiskon.setTextSize(16f);
            holder.tvHargaDiskon.setTypeface(null, Typeface.BOLD);
        } else {
            // No discount - show only original price
            holder.tvPrice.setPaintFlags(holder.tvPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.tvPrice.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary));
            holder.tvPrice.setTextSize(16f);
            holder.tvPrice.setTypeface(null, Typeface.BOLD);
            holder.tvPrice.setText(formatRupiah(totalHarga));
            holder.tvHargaDiskon.setVisibility(View.GONE);
        }

        holder.btnPlus.setOnClickListener(v -> {
            product.setQty(product.getQty() + 1);
            notifyItemChanged(position); // Update the item display
            updateSharedPreferences(v, listProduct);
            notifyCartTotalChanged();
            notifyCartQtyChanged();
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (product.getQty() > 1) {
                product.setQty(product.getQty() - 1);
                notifyItemChanged(position); // Update the item display
                updateSharedPreferences(v, listProduct);
                notifyCartTotalChanged();
                notifyCartQtyChanged();
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
        TextView tvProduct, tvPrice, tvHargaDiskon, tvQty;
        ImageButton btnDelete;
        Button btnPlus, btnMinus;
        ImageView imageViewProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProduct = itemView.findViewById(R.id.tvProduct);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvHargaDiskon = itemView.findViewById(R.id.tvHargaDiskon); // Add this
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
            double total = 0;
            for (Product p : listProduct) {
                total += p.getHargaJual() * p.getQty();
            }
            cartChangedListener.onCartTotalChanged((int) total);
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
