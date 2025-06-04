package com.example.uts_a22202302996.adapter;

import static com.example.uts_a22202302996.api.ServerAPI.BASE_URL_IMAGE;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uts_a22202302996.MainActivity;
import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.api.RegisterAPI;
import com.example.uts_a22202302996.api.ServerAPI;
import com.example.uts_a22202302996.model.Product;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<Product> originalList;
    private List<Product> filteredList;

    private Context context;

    public void updateData(List<Product> newList) {
        originalList.clear();
        originalList.addAll(newList);
        filteredList.clear();
        filteredList.addAll(newList);
        notifyDataSetChanged();
    }

    public SearchAdapter(Context context, List<Product> itemList) {
        this.context = context;
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
                .load(BASE_URL_IMAGE+"product/"+product.getFoto())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.imageViewProduct);

        holder.itemView.setOnClickListener(v -> {
            // Increment the view count
            int currentViewCount = product.getView();
            currentViewCount++;

            // Update the view count in the database using the API
            RegisterAPI apiService = ServerAPI.getClient().create(RegisterAPI.class);
            Call<ResponseBody> call = apiService.postView(product.getKode(), currentViewCount);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "View count updated in database", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to update view count in database", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

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

