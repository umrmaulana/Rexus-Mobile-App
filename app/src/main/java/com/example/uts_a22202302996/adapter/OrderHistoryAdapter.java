package com.example.uts_a22202302996.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.model.OrderHistory;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private List<OrderHistory> orderList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(OrderHistory order);
    }

    public OrderHistoryAdapter(List<OrderHistory> orderList, OnItemClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_order_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderHistory order = orderList.get(position);

        holder.tvOrderNumber.setText("Order #" + order.getOrderNumber());
        holder.tvOrderDate.setText("Date: " + order.getOrderDate());
        holder.tvOrderStatus.setText(order.getOrderStatus());
        holder.tvPaymentStatus.setText("Payment: " + order.getPaymentStatus());
        holder.tvTotalAmount.setText("Total: " + formatRupiah(order.getTotalAmount()));

        // Set different colors based on order status
        switch (order.getOrderStatus().toLowerCase()) {
            case "pending":
                holder.tvOrderStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.navigation_bar));
                break;
            case "processing":
                holder.tvOrderStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.link));
                break;
            case "shipped":
                holder.tvOrderStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.warning_text));
                break;
            case "delivered":
                holder.tvOrderStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.success_text));
                break;
            case "cancelled":
                holder.tvOrderStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.error));
                break;
            case "completed":
                holder.tvOrderStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.success_text));
                break;
            case "failed":
                holder.tvOrderStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.error));
                break;
            default:
                holder.tvOrderStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black));
        }

        holder.btnViewDetails.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderNumber, tvOrderDate, tvOrderStatus, tvPaymentStatus, tvTotalAmount;
        Button btnViewDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }

    private String formatRupiah(double number) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return format.format(number).replace(",00", "");
    }
}