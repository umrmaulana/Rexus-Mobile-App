package com.example.uts_a22202302996.adapter;

import static com.example.uts_a22202302996.api.ServerAPI.BASE_URL_IMAGE;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.model.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> {

    private ArrayList<Product> listProduct;
    private OnTotalPriceChangedListener totalPriceChangedListener;

    public interface OnTotalPriceChangedListener {
        void onTotalPriceChanged(double totalPrice);
    }

    public CheckoutAdapter(ArrayList<Product> listProduct) {
        this.listProduct = listProduct != null ? listProduct : new ArrayList<>();
    }

    public void setOnTotalPriceChangedListener(OnTotalPriceChangedListener listener) {
        this.totalPriceChangedListener = listener;
        if (listener != null) {
            updateTotalPrice(); // Otomatis hitung saat pertama kali set listener
        }
    }

    public void updateTotalPrice() {
        if (totalPriceChangedListener != null) {
            double total = calculateTotalPrice();
            totalPriceChangedListener.onTotalPriceChanged(total);
        }
    }

    private double calculateTotalPrice() {
        double total = 0;
        for (Product p : listProduct) {
            if (p != null) {
                total += p.getHargaJual() * p.getQty();
            }
        }
        return total;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_product_checkout, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutAdapter.ViewHolder holder, int position) {
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
        holder.tvQty.setText("X " + String.valueOf(product.getQty()));

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

        updateTotalPrice();

    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public void notifyTotalPriceChanged() {
        if (totalPriceChangedListener != null) {
            // Biarkan activity yang menghitung total dari semua produk
            totalPriceChangedListener.onTotalPriceChanged(0);
            // Nilai 0 akan di-override oleh activity
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProduct, tvPrice, tvHargaDiskon, tvQty;
        ImageView imageViewProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProduct = itemView.findViewById(R.id.tvProduct);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvHargaDiskon = itemView.findViewById(R.id.tvHargaDiskon); // Add this
            tvQty = itemView.findViewById(R.id.tvQty);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
        }
    }

    // In CartFragment
    private String formatRupiah(double harga) {
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatRupiah.format(harga).replace(",00", "");
    }

}
