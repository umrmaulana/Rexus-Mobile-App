package com.example.uts_a22202302996.activity;

import static android.app.ProgressDialog.show;

import static com.example.uts_a22202302996.util.RupiahFormatter.formatRupiah;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.adapter.CheckoutAdapter;
import com.example.uts_a22202302996.adapter.ShippingServiceAdapter;
import com.example.uts_a22202302996.api.RegisterAPI;
import com.example.uts_a22202302996.api.ServerAPI;
import com.example.uts_a22202302996.databinding.ActivityCheckoutBinding;
import com.example.uts_a22202302996.model.Order;
import com.example.uts_a22202302996.model.Product;
import com.example.uts_a22202302996.model.ShipAddress;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {
    private ActivityCheckoutBinding binding;

    private double shippingCost = 0;
    private int userId;
    private int addressId = -1;

    private int destinationId = -1;

    private CheckoutAdapter checkoutAdapter;
    private ArrayList<Product> productList;

    private RegisterAPI apiInterface;

    private String selectedCourier;
    private String selectedService;
    private String selectedPaymentMethod = null;
    private String selectedPaymentNumber = null;
    private String selectedDeliveryTime = "";
    private String selectedDeliveryService = "";

    private Double subtotal;
    private Double finalPrice;

    // For shipping services
    private List<Map<String, String>> shippingServices = new ArrayList<>();
    private ShippingServiceAdapter shippingServiceAdapter;

    // Add this new variable to track available couriers
    private List<String> availableCouriers = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            loadAddressData();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up toolbar and back button
        binding.ivBack.setOnClickListener(v -> onBackPressed());

        binding.editAddress.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, ShippingAddressActivity.class);
            startActivityForResult(intent, 100);
        });

        // Set up close button to hide product list and show checkout items
        binding.ivClose.setOnClickListener(v -> {
            binding.conItemCheckout.setVisibility(View.VISIBLE);
            binding.conProductList.setVisibility(View.GONE);
        });

        // Setup shipping services RecyclerView
        shippingServiceAdapter = new ShippingServiceAdapter(this::selectShippingService);
        binding.recyclerViewServices.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewServices.setAdapter(shippingServiceAdapter);

        // Set click listener for checkout button
        binding.btnPayment.setOnClickListener(v -> {
            // Validasi sebelum checkout
            if (productList.isEmpty()) {
                Toasty.warning(CheckoutActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            processCheckout();
        });

        // Load address data
        loadAddressData();

        // Load product data
        loadProductData();

        // Setup RecyclerView dan adapter
        setupAdapters();

        // Setup courier selection
        setupCourierSelection();

        // Setup payment method selection
        setupPaymentMethodSelection();
    }

    // Add new method to check which couriers are available for the selected city
    private void checkAvailableCouriers() {
        // Reset available couriers
        availableCouriers.clear();

        // Hide all courier options initially
        binding.rbJne.setVisibility(View.GONE);
        binding.rbTiki.setVisibility(View.GONE);
        binding.rbPos.setVisibility(View.GONE);

        // Show loading indicator
        binding.courierLoadingProgress.setVisibility(View.VISIBLE);

        // If cityId is invalid, don't proceed
        if (destinationId <= 0) {
            binding.courierLoadingProgress.setVisibility(View.GONE);
            return;
        }

        // Minimum weight for checking
        int minWeight = 1000; // 1kg

        // List of couriers to check
        List<String> couriersToCheck = Arrays.asList("jne", "tiki", "pos", "pick");
        AtomicInteger pendingRequests = new AtomicInteger(couriersToCheck.size());

        RegisterAPI apiService = ServerAPI.getClient().create(RegisterAPI.class);

        // Check each courier
        for (String courier : couriersToCheck) {
            apiService.getShippingCost(399, destinationId, minWeight, courier)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    String jsonResponse = response.body().string();
                                    JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

                                    if (jsonObject != null && jsonObject.has("rajaongkir")) {
                                        JsonObject rajaongkir = jsonObject.getAsJsonObject("rajaongkir");
                                        JsonObject status = rajaongkir.getAsJsonObject("status");

                                        if (status.get("code").getAsInt() == 200) {
                                            JsonArray results = rajaongkir.getAsJsonArray("results");

                                            if (results != null && results.size() > 0) {
                                                JsonObject courierObject = results.get(0).getAsJsonObject();
                                                JsonArray costs = courierObject.getAsJsonArray("costs");

                                                // If courier has any costs/services, consider it available
                                                if (costs != null && costs.size() > 0) {
                                                    availableCouriers.add(courier);
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("CheckCouriers", "Error parsing response for " + courier, e);
                            }

                            // When all requests are done, update UI
                            if (pendingRequests.decrementAndGet() == 0) {
                                updateCourierUI();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("CheckCouriers", "Network error for " + courier, t);

                            // When all requests are done, update UI
                            if (pendingRequests.decrementAndGet() == 0) {
                                updateCourierUI();
                            }
                        }
                    });
        }
    }

    // Add method to update courier UI based on available couriers
    private void updateCourierUI() {
        runOnUiThread(() -> {
            // Hide loading indicator
            binding.courierLoadingProgress.setVisibility(View.GONE);
            binding.radioGroupCourier.setVisibility(View.VISIBLE);

            // Show courier options based on availability
            if (availableCouriers.contains("jne")) {
                binding.rbJne.setVisibility(View.VISIBLE);
            }

            if (availableCouriers.contains("tiki")) {
                binding.rbTiki.setVisibility(View.VISIBLE);
            }

            if (availableCouriers.contains("pos")) {
                binding.rbPos.setVisibility(View.VISIBLE);
            }

            // Show message if no couriers are available
            if (availableCouriers.isEmpty()) {
                Toasty.warning(CheckoutActivity.this,
                        "No delivery services available for this location",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private String setupCourierSelection() {
        selectedCourier = null;
        selectedService = null;

        // Original courier selection code remains unchanged
        binding.radioGroupCourier.setOnCheckedChangeListener((group, checkedId) -> {
            // Clear previous shipping services
            shippingServices.clear();
            if (shippingServiceAdapter != null) {
                shippingServiceAdapter.submitList(null);
            }
            binding.servicesCardView.setVisibility(View.GONE);
            shippingCost = 0;
            updateOrderSummary();

            // Check if we have a valid address
            if (destinationId == -1) {
                Toasty.warning(this, "Please select a shipping address first", Toast.LENGTH_SHORT, true).show();
                binding.radioGroupCourier.clearCheck();
                return;
            }

            if (checkedId == R.id.rbJne) {
                selectedCourier = "jne";
                calculateShippingCost(selectedCourier, calculateTotalWeight());
            } else if (checkedId == R.id.rbTiki) {
                selectedCourier = "tiki";
                calculateShippingCost(selectedCourier, calculateTotalWeight());
            } else if (checkedId == R.id.rbPos) {
                selectedCourier = "pos";
                calculateShippingCost(selectedCourier, calculateTotalWeight());
            } else if (checkedId == R.id.rbPick) {
                selectedCourier = "pickup"; // gunakan string pickup atau "pick" sesuai sistem Anda
                selectedService = "pickup";
                shippingCost = 0;

                // Sembunyikan cardView karena tidak ada pilihan layanan
                binding.servicesCardView.setVisibility(View.GONE);

                // Perbarui ringkasan pesanan
                updateOrderSummary();
            }
        });
        return selectedCourier;
    }

    private int calculateTotalWeight() {
        int totalWeight = 0;
        for (Product product : productList) {
            totalWeight += product.getQty() * product.getWeight();

        }
        return totalWeight; // Convert to grams
    }

    private void calculateShippingCost(String courier, int weight) {
        // Tampilkan loading spinner di dalam CardView shipping services
        binding.servicesCardView.setVisibility(View.VISIBLE);
        binding.servicesLoadingProgress.setVisibility(View.VISIBLE);
        binding.recyclerViewServices.setVisibility(View.GONE);

        RegisterAPI apiService = ServerAPI.getClient().create(RegisterAPI.class);
        apiService.getShippingCost(399, destinationId, weight, courier).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Sembunyikan loading spinner di dalam CardView shipping services
                binding.servicesLoadingProgress.setVisibility(View.GONE);
                binding.recyclerViewServices.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonResponse = response.body().string();
                        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

                        // Check if rajaongkir response exists and has valid status
                        if (jsonObject != null && jsonObject.has("rajaongkir")) {
                            JsonObject rajaongkir = jsonObject.getAsJsonObject("rajaongkir");
                            JsonObject status = rajaongkir.getAsJsonObject("status");

                            if (status.get("code").getAsInt() == 200) {
                                shippingServices.clear();
                                JsonArray results = rajaongkir.getAsJsonArray("results");

                                if (results != null && results.size() > 0) {
                                    JsonObject courierObject = results.get(0).getAsJsonObject();
                                    JsonArray costs = courierObject.getAsJsonArray("costs");

                                    for (int i = 0; i < costs.size(); i++) {
                                        JsonObject serviceObject = costs.get(i).getAsJsonObject();
                                        JsonArray costArray = serviceObject.getAsJsonArray("cost");
                                        JsonObject costDetail = costArray.get(0).getAsJsonObject();

                                        Map<String, String> service = new HashMap<>();
                                        service.put("service", serviceObject.get("service").getAsString());
                                        service.put("description", serviceObject.get("description").getAsString());
                                        service.put("cost", costDetail.get("value").getAsString());
                                        service.put("etd", costDetail.get("etd").getAsString());

                                        shippingServices.add(service);
                                    }

                                    // Update UI with shipping services
                                    shippingServiceAdapter.submitList(new ArrayList<>(shippingServices));
                                } else {
                                    // Jika tidak ada layanan ditemukan
                                    Toasty.info(CheckoutActivity.this,
                                            "Tidak ada layanan pengiriman yang tersedia",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toasty.error(CheckoutActivity.this,
                                        "Error: " + status.get("description").getAsString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        Log.e("ShippingCost", "JSON parsing error", e);
                        Toasty.error(CheckoutActivity.this,
                                "Error parsing shipping data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toasty.error(CheckoutActivity.this,
                            "Failed to calculate shipping costs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                binding.servicesLoadingProgress.setVisibility(View.GONE);
                binding.recyclerViewServices.setVisibility(View.VISIBLE);

                Log.e("ShippingCost", "Network error", t);
                Toasty.error(CheckoutActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectShippingService(@NonNull Map<String, String> service) {
        selectedService = service.get("service");
        selectedDeliveryTime = service.get("etd") + " hari";
        selectedDeliveryService = service.get("etd");
        try {
            shippingCost = Double.parseDouble(service.get("cost"));
            updateOrderSummary();
        } catch (NumberFormatException e) {
            Log.e("Checkout", "Error parsing shipping cost", e);
        }
    }

    private void updateOrderSummary() {
        finalPrice = subtotal + shippingCost;
        binding.tvSubTotal.setText(formatRupiah(subtotal));
        binding.tvShippingCost.setText(formatRupiah(shippingCost));
        binding.tvFinalPrice.setText(formatRupiah(finalPrice));
    }

    private void updatePriceDisplay(double subtotal) {
        finalPrice = subtotal + shippingCost;
        binding.tvSubTotal.setText(formatRupiah(subtotal));
        binding.tvShippingCost.setText(formatRupiah(shippingCost));
        binding.tvFinalPrice.setText(formatRupiah(finalPrice));
    }

    private void setupPaymentMethodSelection() {
        binding.radioGroupPayment.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbBri) {
                selectedPaymentMethod = "bri";
                selectedPaymentNumber = "331234567890";
                updatePaymentUI();
            } else if (checkedId == R.id.rbMandiri) {
                selectedPaymentMethod = "mandiri";
                selectedPaymentNumber = "1234567890";
                updatePaymentUI();
            } else if (checkedId == R.id.rbBca) {
                selectedPaymentMethod = "bca";
                selectedPaymentNumber = "0987654321";
                updatePaymentUI();
            } else if (checkedId == R.id.rbCod) {
                selectedPaymentMethod = "cod";
                selectedPaymentNumber = null;
                binding.paymentCardView.setVisibility(View.GONE);
            }
        });
    }

    private void updatePaymentUI() {
        runOnUiThread(()->{
            binding.paymentCardView.setVisibility(View.VISIBLE);
            binding.tvBankName.setText(selectedPaymentMethod);
            binding.tvAccountNumber.setText(selectedPaymentNumber);
            binding.tvAccountName.setText("Kla Computer");
            binding.ivCopyAccountNumber.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Account Number", selectedPaymentNumber);
                clipboard.setPrimaryClip(clip);
                Toasty.success(CheckoutActivity.this, "Account number copied to clipboard", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void loadProductData() {
        SharedPreferences spProduct = getSharedPreferences("product", MODE_PRIVATE);
        String jsonProductList = spProduct.getString("listproduct", null);

        if (jsonProductList != null) {
            Gson gson = new Gson();
            Product[] productsArray = gson.fromJson(jsonProductList, Product[].class);
            productList = new ArrayList<>(Arrays.asList(productsArray));
        } else {
            productList = new ArrayList<>();
        }
    }

    private void setupAdapters() {
        // Adapter untuk tampilan terbatas (1 item)
        int limit = 1;
        List<Product> limitedList = productList.size() > limit ?
                productList.subList(0, limit) : productList;

        checkoutAdapter = new CheckoutAdapter(new ArrayList<>(limitedList));

        // Gunakan productList penuh untuk perhitungan total
        checkoutAdapter.setOnTotalPriceChangedListener(total -> {
            // Hitung total dari SEMUA produk, bukan hanya yang ditampilkan
            double fullTotal = calculateFullTotal();
            updatePriceDisplay(fullTotal);
        });

        RecyclerView rvCheckoutItem = findViewById(R.id.rvCheckoutItem);
        rvCheckoutItem.setLayoutManager(new LinearLayoutManager(this));
        rvCheckoutItem.setAdapter(checkoutAdapter);

        // Panggil update pertama kali
        checkoutAdapter.notifyTotalPriceChanged();

        // Setup untuk tampilan penuh
        TextView tvShowMoreItems = findViewById(R.id.tvMoreItems);
        tvShowMoreItems.setVisibility(productList.size() > limit ? View.VISIBLE : View.GONE);

        tvShowMoreItems.setOnClickListener(v -> {
            findViewById(R.id.conItemCheckout).setVisibility(View.GONE);
            findViewById(R.id.conProductList).setVisibility(View.VISIBLE);

            CheckoutAdapter fullAdapter = new CheckoutAdapter(productList);
            fullAdapter.setOnTotalPriceChangedListener(this::updatePriceDisplay);

            RecyclerView rvCheckoutItems = findViewById(R.id.rvCheckoutItems);
            rvCheckoutItems.setLayoutManager(new LinearLayoutManager(this));
            rvCheckoutItems.setAdapter(fullAdapter);
        });
    }

    private double calculateFullTotal() {
        double total = 0;
        for (Product p : productList) {
            total += p.getHargaJual() * p.getQty();
        }
        subtotal = total;
        return total;
    }

    private void loadAddressData() {
        SharedPreferences spUserID = getSharedPreferences("login_session", MODE_PRIVATE);
        userId = spUserID.getInt("user_id", -1);

        apiInterface = ServerAPI.getClient().create(RegisterAPI.class);
        Call<List<ShipAddress>> call = apiInterface.getActiveAddress(userId);

        call.enqueue(new Callback<List<ShipAddress>>() {
            @Override
            public void onResponse(Call<List<ShipAddress>> call, Response<List<ShipAddress>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ShipAddress> allAddresses = response.body();

                    // Filter hanya alamat yang aktif
                    ShipAddress activeAddress = null;
                    for (ShipAddress address : allAddresses) {
                        if (address.isActive()) {
                            activeAddress = address;
                            break;
                        }
                    }

                    if (activeAddress != null) {
                        binding.tvName.setText(activeAddress.getRecipt_name());
                        binding.tvCity.setText(activeAddress.getCity_name());
                        binding.tvProvince.setText(activeAddress.getProvince_name());
                        binding.tvAddress.setText(activeAddress.getAddress());
                        binding.tvPostalCode.setText(String.valueOf(activeAddress.getPostal_code()));
                        binding.tvPhone.setText(activeAddress.getNo_tlp());
                        addressId = activeAddress.getId();
                        destinationId = activeAddress.getCity_id();

                        // Panggil checkAvailableCouriers() setelah destinationId terisi
                        checkAvailableCouriers();
                    } else {
                        Toast.makeText(CheckoutActivity.this, "Alamat aktif tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(CheckoutActivity.this, "Gagal memuat alamat", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ShipAddress>> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processCheckout() {
        // Validasi data sebelum checkout
        if (addressId == -1) {
            Toasty.warning(this, "Please select a shipping address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCourier == null || selectedService == null) {
            Toasty.warning(this, "Please select a shipping method", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedPaymentMethod == null) {
            Toasty.warning(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tampilkan loading
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing your order...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Siapkan data produk untuk dikirim
        List<Map<String, Object>> productsData = new ArrayList<>();
        for (Product product : productList) {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("product_id", product.getKode());
            productMap.put("product_name", product.getMerk());
            productMap.put("qty", product.getQty());
            productMap.put("price", product.getHargaJual());
            productsData.add(productMap);
        }

        // Siapkan data order
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("user_id", userId);
        orderData.put("ship_address_id", addressId);
        orderData.put("sub_total", (Double) subtotal);
        orderData.put("shipping_cost", (Double) shippingCost);
        orderData.put("final_price", (Double) finalPrice);
        orderData.put("total_weight", calculateTotalWeight());
        orderData.put("courier", selectedCourier);
        orderData.put("courier_service", selectedService);
        orderData.put("estimated_day", selectedDeliveryService);
        orderData.put("total_weight", String.valueOf(calculateTotalWeight()));
        orderData.put("payment_method", selectedPaymentMethod);
        orderData.put("payment_status", "Unpaid");
        orderData.put("order_status", "Pending");
        orderData.put("products", productsData);

        // Konversi ke JSON
        Gson gson = new Gson();
        String jsonData = gson.toJson(orderData);

        // Buat request body
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonData);

        // Panggil API tanpa bukti transfer
        Call<ResponseBody> call = apiInterface.createOrder(requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        clearCart();
                        String jsonResponse = response.body().string();
                        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                        if (jsonObject.get("success").getAsBoolean()) {

                            Order order = createOrderObject(
                                    jsonObject.get("order_id").getAsInt(),
                                    jsonObject.get("order_number").getAsString()
                            );

                            if (selectedPaymentMethod.equals("cod")) {
                                order.setPaymentStatus("Paid");
                                Intent intent = new Intent(CheckoutActivity.this, OrderDetailActivity.class);
                                intent.putExtra("order_data", new Gson().toJson(order));
                                intent.putExtra("order_id", order.getOrderId());
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else{
                                openPaymentDetailActivity(order);
                            }

                        } else {
                            Toasty.error(CheckoutActivity.this,
                                    "Checkout failed: " + jsonObject.get("message").getAsString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toasty.error(CheckoutActivity.this,
                                "Failed to process order", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toasty.error(CheckoutActivity.this,
                            "Error processing response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toasty.error(CheckoutActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    private Order createOrderObject(int orderId, String orderNumber) {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderNumber(orderNumber);
        order.setUserId(userId);
        order.setShipAddressId(addressId);
        order.setSubTotal(subtotal);
        order.setShippingCost(shippingCost);
        order.setFinalPrice(finalPrice);
        order.setCourier(selectedCourier);
        order.setCourierService(selectedService);
        order.setEstimatedDay(selectedDeliveryService);
        order.setTotalWeight(calculateTotalWeight());
        order.setPaymentMethod(selectedPaymentMethod);
        order.setPaymentStatus("unpaid");
        order.setOrderStatus("Pending");
        return order;
    }

    private void openPaymentDetailActivity(Order order) {
        try {
            // Create payment info based on payment method
            JSONObject paymentInfo = new JSONObject();
            paymentInfo.put("bank", getBankName(order.getPaymentMethod()));
            paymentInfo.put("account_number", getAccountNumber(order.getPaymentMethod()));
            paymentInfo.put("account_name", "Kla Computer");

            // Convert order to JSON
            Gson gson = new Gson();
            String orderJson = gson.toJson(order);

            Intent intent = new Intent(CheckoutActivity.this, PaymentDetailActivity.class);
            intent.putExtra("order_data", orderJson);
            intent.putExtra("payment_info", paymentInfo.toString());

            // Clear back stack
            startActivity(intent);
            finish();

        } catch (Exception e) {
            Log.e("CheckoutDebug", "Error navigating to payment details", e);
            Toasty.error(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    private String getBankName(@NonNull String paymentMethod) {
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

    private void clearCart() {
        SharedPreferences spProduct = getSharedPreferences("product", MODE_PRIVATE);
        SharedPreferences.Editor editor = spProduct.edit();
        editor.remove("listproduct");
        editor.apply();

        // Broadcast that the cart has been cleared
        Intent intent = new Intent("com.example.uts_a22202302996.UPDATE_CART_BADGE");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        Log.d("CheckoutDebug", "Cart has been cleared");
    }
}