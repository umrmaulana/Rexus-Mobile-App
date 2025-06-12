package com.example.uts_a22202302996.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.model.OrderProduct;
import com.example.uts_a22202302996.util.RupiahFormatter;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private Context context;
    private List<OrderProduct> orderProducts;
    private boolean isLimitedView = false;
    private int maxItems = 3;

    public OrderItemAdapter(Context context, List<OrderProduct> orderProducts) {
        this.context = context;
        this.orderProducts = orderProducts;
    }

    public OrderItemAdapter(Context context, List<OrderProduct> orderProducts, boolean isLimitedView) {
        this.context = context;
        this.orderProducts = orderProducts;
        this.isLimitedView = isLimitedView;
    }

    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_order_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderProduct item = orderProducts.get(position);

        holder.tvProductName.setText(item.getProductName());
        holder.tvQuantity.setText("Qty: " + item.getQty());
        holder.tvPrice.setText(RupiahFormatter.formatRupiah(item.getPrice()));
        holder.tvSubTotal.setText("Subtotal: " + RupiahFormatter.formatRupiah(item.getSubTotal()));

        // Load image with Glide
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_launcher_foreground);
        }
    }

    @Override
    public int getItemCount() {
        if (isLimitedView && orderProducts.size() > maxItems) {
            return maxItems;
        }
        return orderProducts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvQuantity, tvPrice, tvSubTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSubTotal = itemView.findViewById(R.id.tvSubTotal);
        }
    }
}