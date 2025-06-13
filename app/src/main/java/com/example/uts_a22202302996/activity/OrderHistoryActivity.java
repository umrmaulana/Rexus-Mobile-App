package com.example.uts_a22202302996.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.adapter.OrderHistoryAdapter;
import com.example.uts_a22202302996.api.RegisterAPI;
import com.example.uts_a22202302996.api.ServerAPI;
import com.example.uts_a22202302996.model.OrderHistory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrderHistory;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private OrderHistoryAdapter adapter;
    private List<OrderHistory> orderList = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        rvOrderHistory = findViewById(R.id.rvOrderHistory);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        // Setup toolbar
        findViewById(R.id.toolbar).setOnClickListener(v -> onBackPressed());

        // Setup RecyclerView
        adapter = new OrderHistoryAdapter(orderList, this::onOrderItemClick);
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        rvOrderHistory.setAdapter(adapter);

        // Setup swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(this::loadOrderHistory);

        // Load order history
        loadOrderHistory();
    }

    private void onOrderItemClick(OrderHistory order) {
        // Open order detail activity
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra("order_id", order.getId());
        intent.putExtra("order_number", order.getOrderNumber());
        startActivity(intent);
    }

    private void loadOrderHistory() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        // Get user ID from shared preferences
        int userId = getSharedPreferences("login_session", MODE_PRIVATE).getInt("user_id", -1);
        if (userId == -1) {
            Toasty.error(this, "User not logged in", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            finish();
            return;
        }

        RegisterAPI apiInterface = ServerAPI.getClient().create(RegisterAPI.class);
        Call<ResponseBody> call = apiInterface.getOrderHistory(userId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);

                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = response.body().string();
                        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

                        if (jsonObject.get("success").getAsBoolean()) {
                            JsonArray orders = jsonObject.getAsJsonArray("orders");
                            orderList.clear();

                            if (orders.size() > 0) {
                                for (int i = 0; i < orders.size(); i++) {
                                    JsonObject order = orders.get(i).getAsJsonObject();
                                    OrderHistory history = new OrderHistory(
                                            order.get("id").getAsInt(),
                                            order.get("order_number").getAsString(),
                                            formatDate(order.get("created_at").getAsString()),
                                            order.get("order_status").getAsString(),
                                            order.get("payment_status").getAsString(),
                                            order.get("courier").getAsString(),
                                            order.get("estimated_day").getAsString(),
                                            order.get("final_price").getAsDouble()
                                    );
                                    orderList.add(history);
                                }

                                adapter.notifyDataSetChanged();
                                tvEmpty.setVisibility(View.GONE);
                            } else {
                                tvEmpty.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toasty.error(OrderHistoryActivity.this,
                                    "Failed to load orders: " + jsonObject.get("message").getAsString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toasty.error(OrderHistoryActivity.this,
                                "Failed to load order history", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toasty.error(OrderHistoryActivity.this,
                            "Error processing response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                Toasty.error(OrderHistoryActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        loadOrderHistory();
    }
}