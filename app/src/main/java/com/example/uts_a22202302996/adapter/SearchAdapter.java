package com.example.uts_a22202302996.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uts_a22202302996.MainActivity;
import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.product.Product;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<Product> originalList;
    private List<Product> filteredList;

    public void updateData(List<Product> newList) {
        originalList.clear();
        originalList.addAll(newList);
        filteredList.clear();
        filteredList.addAll(newList);
        notifyDataSetChanged();
    }

    public SearchAdapter(List<Product> itemList) {
        this.originalList = new ArrayList<>(itemList);
        this.filteredList = new ArrayList<>(itemList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_product_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = filteredList.get(position);
        holder.textViewMerk.setText(product.getMerk());
        Glide.with(holder.itemView.getContext())
                .load(product.getFoto())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.imageViewProduct);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
            intent.putExtra("product", product); // Kirim data produk
            intent.putExtra("navigate_to", "productDetail"); // Indikasi navigasi ke ProductDetailFragment
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            for (Product product : originalList) {
                if (product.getMerk().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(product);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMerk;
        ImageView imageViewProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMerk = itemView.findViewById(R.id.textViewMerk);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
        }
    }
}

