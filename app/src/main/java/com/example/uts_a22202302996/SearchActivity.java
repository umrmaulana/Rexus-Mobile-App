package com.example.uts_a22202302996;

import static com.example.uts_a22202302996.api.ServerAPI.BASE_URL;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_a22202302996.adapter.SearchAdapter;
import com.example.uts_a22202302996.api.RegisterAPI;
import com.example.uts_a22202302996.product.Product;
import com.example.uts_a22202302996.product.ProductResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {

    private final Handler handler = new Handler();
    private Runnable searchRunnable;
    private SearchAdapter adapter;
    private List<Product> productList;
    RecyclerView recyclerView;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerViewSearch);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        productList = new ArrayList<>();
        adapter = new SearchAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        searchView.setIconified(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search...");
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchProducts(query, true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable); // Cancel the previous search
                }

                // Schedule a new search after a delay
                searchRunnable = () -> fetchProducts(newText, false);
                handler.postDelayed(searchRunnable, 100); // 500ms delay
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            finish();
            return true;
        });

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        fetchProducts("", false);
    }

    private void fetchProducts(String searchQuery, boolean navigateToHome) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI apiService = retrofit.create(RegisterAPI.class);
        Call<ProductResponse> call = apiService.getProducts("all", searchQuery);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> results = response.body().getResult();
                    if (navigateToHome) {
                        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                        intent.putExtra("selected_products", new ArrayList<>(results));
                        intent.putExtra("navigate_to", "product");
                        startActivity(intent);
                    } else {
                        adapter.updateData(results);
                    }

                    // Show or hide the "No Results" message
                    TextView textViewNoResults = findViewById(R.id.textViewNoResults);
                    if (results.isEmpty()) {
                        textViewNoResults.setVisibility(View.VISIBLE);
                    } else {
                        textViewNoResults.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}