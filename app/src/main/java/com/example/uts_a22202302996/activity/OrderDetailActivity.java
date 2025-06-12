package com.example.uts_a22202302996.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.adapter.OrderItemAdapter;
import com.example.uts_a22202302996.api.RegisterAPI;
import com.example.uts_a22202302996.api.ServerAPI;
import com.example.uts_a22202302996.api.ShipAddressResponse;
import com.example.uts_a22202302996.model.Order;
import com.example.uts_a22202302996.model.OrderProduct;
import com.example.uts_a22202302996.model.ShipAddress;
import com.example.uts_a22202302996.util.RupiahFormatter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    // Views
    private Toolbar toolbar;
    private ImageView ivBack, ivClose;
    private TextView tvOrderNumber, tvOrderDate, tvOrderStatus, tvPaymentStatus, tvPaymentMethod;
    private TextView tvRecipientInfo, tvAddress, tvShippingMethod;
    private RecyclerView rvOrderItem, rvOrderItems;
    private TextView tvMoreItems;
    private TextView tvSubtotal, tvShippingCost, tvTotalPrice, tvPaymentDetails;
    private View conProductList, conOderDetail;

    // Data
    private int orderId;
    private Order currentOrder;
    private ShipAddress shippingAddress;
    private List<OrderProduct> orderProducts = new ArrayList<>();
    private OrderItemAdapter orderItemAdapter;
    private ProgressDialog progressDialog;
    private RegisterAPI apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Get order ID from intent
        orderId = getIntent().getIntExtra("order_id", -1);
        if (orderId == -1) {
            Toasty.error(this, "Order ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        initViews();

        // Setup adapters
        setupAdapters();

        // Setup listeners
        setupListeners();

        // Initialize API
        apiInterface = ServerAPI.getClient().create(RegisterAPI.class);

        // Load order details
        loadOrderDetails();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        ivBack = findViewById(R.id.ivBack);
        conOderDetail = findViewById(R.id.conOrderDetail);

        // Order info
        tvOrderNumber = findViewById(R.id.tvOrderNumber);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);

        // Shipping info
        tvRecipientInfo = findViewById(R.id.tvRecipientInfo);
        tvAddress = findViewById(R.id.tvAddress);
        tvShippingMethod = findViewById(R.id.tvShippingMethod);

        // Order items
        rvOrderItem = findViewById(R.id.rvOrderItem);
        tvMoreItems = findViewById(R.id.tvMoreItems);

        // Payment summary
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShippingCost = findViewById(R.id.tvShippingCost);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvPaymentDetails = findViewById(R.id.tvPaymentDetails);

        // Product list modal
        conProductList = findViewById(R.id.conProductList);
        ivClose = findViewById(R.id.ivClose);
        rvOrderItems = findViewById(R.id.rvOrderItems);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading order details...");
        progressDialog.setCancelable(false);
    }

    private void setupAdapters() {
        // Initialize order products list
        orderItemAdapter = new OrderItemAdapter(OrderDetailActivity.this, orderProducts, true);
        rvOrderItem.setLayoutManager(new LinearLayoutManager(this));
        rvOrderItem.setAdapter(orderItemAdapter);

        // Untuk modal/full list
        OrderItemAdapter fullAdapter = new OrderItemAdapter(OrderDetailActivity.this, orderProducts);
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        rvOrderItems.setAdapter(fullAdapter);
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(OrderDetailActivity.this, OrderHistoryActivity.class);
            startActivity(intent);
            finish();
        });

        // Show more items
        tvMoreItems.setOnClickListener(v -> {
            conProductList.setVisibility(VISIBLE);
            conOderDetail.setVisibility(GONE);
        });

        // Close modal
        ivClose.setOnClickListener(v -> {
            conProductList.setVisibility(GONE);
            conOderDetail.setVisibility(VISIBLE);
        });

        // View payment details
        tvPaymentDetails.setOnClickListener(v -> {
            if (currentOrder != null) {
                openPaymentDetailActivity(currentOrder);
            } else {
                Toasty.error(OrderDetailActivity.this, "Data pesanan tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void openPaymentDetailActivity(Order order) {
        if (order.getPaymentMethod().equalsIgnoreCase("cod")) {
            Toasty.warning(this, "Metode pembayaran COD tidak memiliki detail pembayaran", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Create payment info
            JSONObject paymentInfo = new JSONObject();
            paymentInfo.put("bank", getBankName(order.getPaymentMethod()));
            paymentInfo.put("account_number", getAccountNumber(order.getPaymentMethod()));
            paymentInfo.put("account_name", "Kla Computer");

            // Convert order to JSON
            Gson gson = new Gson();
            String orderJson = gson.toJson(order);

            Intent intent = new Intent(OrderDetailActivity.this, PaymentDetailActivity.class);
            intent.putExtra("order_data", orderJson);
            intent.putExtra("payment_info", paymentInfo.toString());

            // ✅ Status pembayaran
            intent.putExtra("is_paid", order.getPaymentStatus().equalsIgnoreCase("paid"));

            // ✅ Bukti transfer jika ada
            intent.putExtra("proof_image_url", order.getProofTransfer());
            Log.d("OrderDetailActivity", "Proof Transfer URL: " + order.getProofTransfer());

            startActivity(intent);

        } catch (Exception e) {
            Toasty.error(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @NonNull
    private String getBankName(String paymentMethod) {
        switch (paymentMethod.toLowerCase()) {
            case "bri": return "BRI";
            case "bca": return "BCA";
            case "mandiri": return "Mandiri";
            default: return "Bank";
        }
    }

    @NonNull
    private String getAccountNumber(String paymentMethod) {
        switch (paymentMethod.toLowerCase()) {
            case "bri": return "0912256378221";
            case "bca": return "123456789012";
            case "mandiri": return "987654321098";
            default: return "1234567890";
        }
    }

    private void loadOrderDetails() {
        progressDialog.show();

        Call<ResponseBody> call = apiInterface.getOrderDetails(orderId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = response.body().string();
                        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

                        if (jsonObject.get("success").getAsBoolean()) {
                            // Parse order data
                            JsonObject orderData = jsonObject.getAsJsonObject("order");
                            currentOrder = parseOrder(orderData);

                            // Parse order items
                            JsonArray products = jsonObject.getAsJsonArray("products");
                            parseOrderItems(products);

                            // Load shipping address
                            loadShippingAddress(currentOrder.getShipAddressId());

                            // Update UI
                            updateUI();
                        } else {
                            Toasty.error(OrderDetailActivity.this,
                                    "Failed to load order: " + jsonObject.get("message").getAsString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toasty.error(OrderDetailActivity.this,
                                "Failed to load order details", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toasty.error(OrderDetailActivity.this,
                            "Error processing response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toasty.error(OrderDetailActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Order parseOrder(JsonObject orderData) {
        Order order = new Order();
        order.setOrderId(orderData.get("id").getAsInt());
        order.setOrderNumber(orderData.get("order_number").getAsString());
        order.setUserId(orderData.get("user_id").getAsInt());
        order.setShipAddressId(orderData.get("ship_address_id").getAsInt());
        order.setSubTotal(orderData.get("sub_total").getAsDouble());
        order.setShippingCost(orderData.get("shipping_cost").getAsDouble());
        order.setFinalPrice(orderData.get("final_price").getAsDouble());
        order.setCourier(orderData.get("courier").getAsString());
        order.setCourierService(orderData.get("courier_service").getAsString());
        order.setEstimatedDay(orderData.get("estimated_day").getAsString());
        order.setTotalWeight(orderData.get("total_weight").getAsDouble());
        order.setPaymentMethod(orderData.get("payment_method").getAsString());
        order.setPaymentStatus(orderData.get("payment_status").getAsString());
        order.setOrderStatus(orderData.get("order_status").getAsString());
        order.setCreatedAt(orderData.get("created_at").getAsString());
        order.setProofTransfer(orderData.get("proof_transfer").getAsString());

        return order;
    }

    private void parseOrderItems(JsonArray products) {
        orderProducts.clear();
        for (int i = 0; i < products.size(); i++) {
            JsonObject product = products.get(i).getAsJsonObject();
            OrderProduct item = new OrderProduct();
            item.setProductId(product.get("product_id").getAsString());
            item.setProductName(product.get("product_name").getAsString());
            item.setQty(product.get("qty").getAsInt());
            item.setPrice(product.get("price").getAsDouble());
            item.setSubTotal(product.get("sub_total").getAsDouble());
            if (product.has("image_url")) {
                item.setImageUrl(product.get("image_url").getAsString());
            } else {
                // Default image or placeholder
                item.setImageUrl(String.valueOf(R.drawable.ic_launcher_foreground));
            }

            orderProducts.add(item);
        }
        orderItemAdapter.notifyDataSetChanged();
        rvOrderItems.getAdapter().notifyDataSetChanged();
    }

    private void loadShippingAddress(int addressId) {
        // Show loading indicator if needed
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading shipping address...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<ShipAddressResponse> call = apiInterface.getAddressById(addressId);
        call.enqueue(new Callback<ShipAddressResponse>() {
            @Override
            public void onResponse(Call<ShipAddressResponse> call, Response<ShipAddressResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    shippingAddress = response.body().getAddress();
                    updateShippingInfo();
                } else {
                    Toasty.warning(OrderDetailActivity.this,
                            "Failed to load shipping address", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ShipAddressResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toasty.error(OrderDetailActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateShippingInfo() {
        if (shippingAddress == null) return;

        String recipientInfo = shippingAddress.getRecipt_name() + " | " + shippingAddress.getNo_tlp();
        tvRecipientInfo.setText(recipientInfo);

        String address = shippingAddress.getAddress() + ", " +
                shippingAddress.getCity_name() + ", " +
                shippingAddress.getProvince_name() + " " +
                shippingAddress.getPostal_code();
        tvAddress.setText(address);
    }

    private void updateUI() {
        // Order info
        tvOrderNumber.setText("#" + currentOrder.getOrderNumber());
        tvOrderDate.setText(formatDate(currentOrder.getCreatedAt()));
        tvOrderStatus.setText(currentOrder.getOrderStatus());
        tvPaymentStatus.setText(currentOrder.getPaymentStatus());
        tvPaymentMethod.setText(currentOrder.getPaymentMethod());

        // Payment summary
        tvSubtotal.setText(RupiahFormatter.formatRupiah(currentOrder.getSubTotal()));
        tvShippingCost.setText(RupiahFormatter.formatRupiah(currentOrder.getShippingCost()));
        tvTotalPrice.setText(RupiahFormatter.formatRupiah(currentOrder.getFinalPrice()));

        // Shipping method
        tvShippingMethod.setText(currentOrder.getCourier() + " " + currentOrder.getCourierService());

        // Update shipping info if available
        if (shippingAddress != null) {
            updateShippingInfo();
        }
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

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(OrderDetailActivity.this, OrderHistoryActivity.class);
        startActivity(intent);
        finish();
    }
}