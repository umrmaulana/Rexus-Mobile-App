package com.example.uts_a22202302996.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_a22202302996.R;
import com.facebook.shimmer.ShimmerFrameLayout;

public class ProductSkeletonAdapter extends RecyclerView.Adapter<ProductSkeletonAdapter.SkeletonViewHolder> {

    private int itemCount;

    public ProductSkeletonAdapter(int itemCount) {
        this.itemCount = itemCount;
    }

    @NonNull
    @Override
    public SkeletonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_skeleton_product, parent, false);
        return new SkeletonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkeletonViewHolder holder, int position) {
        // No binding needed for skeleton
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull SkeletonViewHolder holder) {
        holder.shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull SkeletonViewHolder holder) {
        holder.shimmerFrameLayout.stopShimmer();
    }

    static class SkeletonViewHolder extends RecyclerView.ViewHolder {
        ShimmerFrameLayout shimmerFrameLayout;

        SkeletonViewHolder(@NonNull View itemView) {
            super(itemView);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmerLayout);
        }
    }
}