package com.example.uts_a22202302996.adapter;

import static com.example.uts_a22202302996.api.ServerAPI.BASE_URL_IMAGE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uts_a22202302996.MainActivity;
import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.api.RegisterAPI;
import com.example.uts_a22202302996.api.ServerAPI;
import com.example.uts_a22202302996.model.SharedProductViewModel;
import com.example.uts_a22202302996.model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        // Format harga
        String hargaJual = formatRupiah(String.valueOf(product.getHargaJual()));
        String hargaPokok = formatRupiah(String.valueOf(product.getHargapokok()));

        // Cek diskon
        if (product.getDiskonJual() > 0) {
            // Tampilkan harga asli dengan coretan dan harga diskon
            holder.textViewHargaJual.setPaintFlags(holder.textViewHargaJual.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textViewHargaJual.setTextSize(14);
            holder.textViewHargaJual.setTypeface(null, Typeface.ITALIC);
            holder.textViewHargaJual.setTextColor(ContextCompat.getColor(fragment.getContext(), R.color.error));
            holder.textViewHargaJual.setText(hargaPokok);
            holder.textViewHargaJualDiskon.setVisibility(View.VISIBLE);
            holder.textViewHargaJualDiskon.setText(hargaJual);
            holder.textViewDiscountBadge.setVisibility(View.VISIBLE);
            holder.textViewDiscountBadge.setText(product.getDiskonJual() + "%");
        } else {
            // Tanpa diskon
            holder.textViewHargaJual.setPaintFlags(holder.textViewHargaJual.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.textViewHargaJual.setTextSize(16);
            holder.textViewHargaJual.setTypeface(null, Typeface.BOLD);
            holder.textViewHargaJual.setTextColor(ContextCompat.getColor(fragment.getContext(), R.color.primary));
            holder.textViewHargaJual.setText(hargaPokok);
            holder.textViewHargaJualDiskon.setVisibility(View.GONE);
            holder.textViewDiscountBadge.setVisibility(View.GONE);
        }

        // Cek stok (Sold Out)
        if (product.getStok() <= 0) {
            holder.imageViewStatus.setVisibility(View.VISIBLE);
            holder.addToCart.setEnabled(false); // Disable cart button when out of stock
            holder.addToCart.setAlpha(0.5f); // Make cart button semi-transparent
            holder.imageViewProduct.setAlpha(0.5f); // Make image view semi-transparent
        } else {
            holder.imageViewStatus.setVisibility(View.GONE);
            holder.addToCart.setEnabled(true); // Disable cart button when out of stock
            holder.addToCart.setAlpha(1f);
            holder.imageViewProduct.setAlpha(1.0f);
        }

        // Load view count
        holder.txView.setText(product.getView()+"x");

        // Load gambar menggunakan Glide
        Glide.with(fragment.getContext())
                .load(BASE_URL_IMAGE+"product/"+product.getFoto())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.imageViewProduct);

        // Klik item untuk membuka detail produk
        holder.itemView.setOnClickListener(v -> {
            int currentViewCount = product.getView();
            currentViewCount++;

            // Update the TextView
            holder.txView.setText(currentViewCount+"x");

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

        // Handle "Add to Cart" button click
        holder.addToCart.setOnClickListener(v -> {
            SharedPreferences cartPreferences = fragment.requireContext().getSharedPreferences("product", Context.MODE_PRIVATE);
            Gson gson = new Gson();

            // Retrieve existing cart data
            String jsonText = cartPreferences.getString("listproduct", null);
            Type type = new TypeToken<ArrayList<Product>>() {}.getType();
            ArrayList<Product> cartList = gson.fromJson(jsonText, type);

            if (cartList == null) {
                cartList = new ArrayList<>();
            }

            // Check if the product already exists in the cart
            boolean alreadyExists = false;
            for (Product p : cartList) {
                if (p.getKode().equals(product.getKode())) {
                    if (p.getQty() >= product.getStok()) {
                        Toast.makeText(fragment.getContext(), "Stok tidak mencukupi", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    p.setQty(p.getQty() + 1);
                    alreadyExists = true;
                    break;
                }
            }

            // If the product is not in the cart, add it
            if (!alreadyExists) {
                product.setQty(1);
                cartList.add(product);
            }

            // Save the updated cart back to SharedPreferences
            String updatedJson = gson.toJson(cartList);
            SharedPreferences.Editor editor = cartPreferences.edit();
            editor.putString("listproduct", updatedJson);
            editor.apply();

            // Show confirmation
            Toast.makeText(fragment.getContext(), "Produk ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();

            // Optionally, update the cart badge in MainActivity
            if (fragment.getActivity() instanceof MainActivity) {
                ((MainActivity) fragment.getActivity()).updateCartBadge();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ViewHolder untuk menyimpan referensi elemen UI
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProduct, imageViewStatus;
        TextView textViewMerk, textViewHargaJual, textViewHargaJualDiskon, textViewDiscountBadge;
        TextView txView;
        ImageButton addToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            imageViewStatus = itemView.findViewById(R.id.imageViewStatus);
            textViewMerk = itemView.findViewById(R.id.textViewMerk);
            textViewHargaJual = itemView.findViewById(R.id.textViewHargaJual);
            textViewHargaJualDiskon = itemView.findViewById(R.id.textViewHargaJualDiskon);
            textViewDiscountBadge = itemView.findViewById(R.id.textViewDiscountBadge);
            addToCart = itemView.findViewById(R.id.addToCart);
            txView = itemView.findViewById(R.id.txView);
        }
    }

    // HomeViewModel
    public void setProductList(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }

}
