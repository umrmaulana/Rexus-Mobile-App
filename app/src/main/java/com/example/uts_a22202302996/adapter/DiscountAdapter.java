package com.example.uts_a22202302996.adapter;

import static com.example.uts_a22202302996.api.ServerAPI.BASE_URL_IMAGE;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uts_a22202302996.MainActivity;
import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.api.RegisterAPI;
import com.example.uts_a22202302996.api.ServerAPI;
import com.example.uts_a22202302996.model.SharedProductViewModel;
import com.example.uts_a22202302996.product.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Fragment fragment;

    public DiscountAdapter(Fragment fragment, List<Product> productList) {
        this.fragment = fragment;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate your item layout here
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_product_discount, parent, false);
        return new DiscountAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.textViewMerk.setText(product.getMerk());
        holder.textViewDiscountBadge.setText(product.getDiskonJual()+"%");
        Glide.with(fragment.getContext())
                .load(BASE_URL_IMAGE+"product/"+product.getFoto())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.imageViewProduct);

        holder.itemView.setOnClickListener(v -> {
            int currentViewCount = product.getView();
            currentViewCount++;

            // Update the view count in the database using the API
            RegisterAPI apiService = ServerAPI.getClient().create(RegisterAPI.class);
            Call<ResponseBody> call = apiService.postView(product.getKode(), currentViewCount);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        if (fragment.getContext() != null) {
                            Toast.makeText(fragment.getContext(), "View count updated in database", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (fragment.getContext() != null) {
                            Toast.makeText(fragment.getContext(), "Failed to update view count in database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(fragment.getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            // Kirim data produk ke SharedProductViewModel
            SharedProductViewModel sharedViewModel = new ViewModelProvider(fragment.requireActivity()).get(SharedProductViewModel.class);
            sharedViewModel.selectProduct(product);

            // Navigasi ke ProductFragment
            Activity activity = fragment.getActivity();
            if (activity instanceof MainActivity) {
                BottomNavigationView bottomNavigationView = activity.findViewById(R.id.nav_view);
                bottomNavigationView.setSelectedItemId(R.id.navigation_product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProduct;
        TextView textViewMerk, textViewDiscountBadge;
        public ProductViewHolder(View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewMerk = itemView.findViewById(R.id.textViewMerk);
            textViewDiscountBadge = itemView.findViewById(R.id.textViewDiscountBadge);
        }
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }
}
