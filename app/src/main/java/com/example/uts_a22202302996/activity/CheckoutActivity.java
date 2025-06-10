package com.example.uts_a22202302996.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.adapter.CheckoutAdapter;
import com.example.uts_a22202302996.api.RegisterAPI;
import com.example.uts_a22202302996.api.ServerAPI;
import com.example.uts_a22202302996.model.Product;
import com.example.uts_a22202302996.model.ShipAddress;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private CheckoutAdapter checkoutAdapter;
    private ArrayList<Product> productList;

    private RegisterAPI apiInterface;

    private String selectedCourierName = "";
    private String selectedCourierEstimated = "";
    private int selectedCourierCost = 0;

    // Address
    TextView tvName, tvCity, tvProvince, tvAddress, tvPostalCode, editAddress;

    // Courier
    TextView tvCourierName, tvEstimatedArrival, editCourier;
    TextView tvJne, tvJneEstimated, tvJnePrice;
    TextView tvTiki, tvTikiEstimated, tvTikiPrice;
    TextView tvPos, tvPosEstimated, tvPosPrice;
    TextView tvPick, tvPickEstimated, tvPickPrice;
    RadioButton rbPick, rbJne, rbTiki, rbPos;
    CardView pick, jne, tiki, pos;

    // Bank
    TextView tvBankName, tvAccountNumber, editBank;
    TextView tvBri, tvBriNumber;
    TextView tvBca, tvBcaNumber;
    TextView tvMandiri, tvMandiriNumber;
    TextView tvCod, tvCodNumber;
    ImageView imgIconBank, imgIconBri, imgIconBca, imgIconMandiri, imgIconCod;
    RadioButton rbBri, rbBca, rbMandiri, rbCod;
    CardView bri, bca, mandiri, cod;

    // Price
    TextView tvSubTotal, tvShippingCost, tvFinalPrice;

    ImageView ivBack, ivClose, ivCloseCourier, ivCloseBank;
    Button btnChoosePayment;
    TextView tvShowMoreItems;
    RecyclerView rvCheckoutItem, rvCheckoutItems;
    LinearLayout conItemCheckout, conProductList, conCourierList, conBankList, linearLayout;
    SharedPreferences spUserID, spProduct;

    int userId;
    int destinationId;
    Double subtotal, shippingCost = 0.0, finalPrice;

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
        setContentView(R.layout.activity_checkout);

        // Set up edge-to-edge mode for the activity
        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // Initialize views
        ivBack = findViewById(R.id.ivBack);

        tvName = findViewById(R.id.tvName);
        tvCity = findViewById(R.id.tvCity);
        tvProvince = findViewById(R.id.tvProvince);
        tvAddress = findViewById(R.id.tvAddress);
        tvPostalCode = findViewById(R.id.tvPostalCode);
        editAddress = findViewById(R.id.editAddress);

        tvCourierName = findViewById(R.id.tvBankName);
        tvEstimatedArrival = findViewById(R.id.tvEstimatedArrival);
        editCourier = findViewById(R.id.editBank);

        rbPick = findViewById(R.id.rbPick);
        rbJne = findViewById(R.id.rbJne);
        rbTiki = findViewById(R.id.rbTiki);
        rbPos = findViewById(R.id.rbPos);

        pick = findViewById(R.id.pick);
        jne = findViewById(R.id.jne);
        tiki = findViewById(R.id.tiki);
        pos = findViewById(R.id.pos);

        tvJne = findViewById(R.id.tvJne);
        tvJneEstimated = findViewById(R.id.tvJneEstimated);
        tvJnePrice = findViewById(R.id.tvJnePrice);
        tvTiki = findViewById(R.id.tvTiki);
        tvTikiEstimated = findViewById(R.id.tvTikiEstimated);
        tvTikiPrice = findViewById(R.id.tvTikiPrice);
        tvPos = findViewById(R.id.tvPos);
        tvPosEstimated = findViewById(R.id.tvPosEstimated);
        tvPosPrice = findViewById(R.id.tvPosPrice);
        tvPick = findViewById(R.id.tvPick);
        tvPickEstimated = findViewById(R.id.tvPickEstimated);
        tvPickPrice = findViewById(R.id.tvPickPrice);

        tvBankName = findViewById(R.id.tvBankName);
        tvAccountNumber = findViewById(R.id.tvAccountNumber);
        editBank = findViewById(R.id.editBank);
        imgIconBank = findViewById(R.id.imgIconBank);

        tvBri = findViewById(R.id.tvBri);
        tvBriNumber = findViewById(R.id.tvBriNumber);
        tvBca = findViewById(R.id.tvBca);
        tvBcaNumber = findViewById(R.id.tvBcaNumber);
        tvMandiri = findViewById(R.id.tvMandiri);
        tvMandiriNumber = findViewById(R.id.tvMandiriNumber);
        tvCod = findViewById(R.id.tvCod);
        tvCodNumber = findViewById(R.id.tvCodNumber);

        imgIconBri = findViewById(R.id.imgIconBri);
        imgIconBca = findViewById(R.id.imgIconBca);
        imgIconMandiri = findViewById(R.id.imgIconMandiri);
        imgIconCod = findViewById(R.id.imgIconCod);

        tvSubTotal = findViewById(R.id.tvSubTotal);
        tvShippingCost = findViewById(R.id.tvShippingCost);
        tvFinalPrice = findViewById(R.id.tvFinalPrice);

        btnChoosePayment = findViewById(R.id.btnPayment);
        rvCheckoutItem = findViewById(R.id.rvCheckoutItem);
        rvCheckoutItems = findViewById(R.id.rvCheckoutItems);

        tvShowMoreItems = findViewById(R.id.tvMoreItems);
        ivClose = findViewById(R.id.ivClose);
        conItemCheckout = findViewById(R.id.conItemCheckout);
        conProductList = findViewById(R.id.conProductList);

        linearLayout = findViewById(R.id.linearLayout);
        conCourierList = findViewById(R.id.conCourierList);
        ivCloseCourier = findViewById(R.id.ivCloseCourier);
        conBankList = findViewById(R.id.conPaymentMethod);
        ivCloseBank = findViewById(R.id.ivCloseBank);

        editAddress.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, ShippingAddressActivity.class);
            startActivityForResult(intent, 100);
        });

        editCourier.setOnClickListener(v -> {
            conItemCheckout.setVisibility(View.GONE);
            conProductList.setVisibility(View.GONE);
            conCourierList.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            conBankList.setVisibility(View.GONE);
            loadCourierData();
        });

        editBank.setOnClickListener(v -> {
            conItemCheckout.setVisibility(View.GONE);
            conProductList.setVisibility(View.GONE);
            conCourierList.setVisibility(View.GONE);
            conBankList.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);

            // Load bank data
            tvBriNumber.setText("1234567890");
            tvBcaNumber.setText("0987654321");
            tvMandiriNumber.setText("1122334455");
            tvCodNumber.setText("Bayar di tempat");
        });

        ivClose.setOnClickListener(v -> {
            conItemCheckout.setVisibility(View.VISIBLE);
            conProductList.setVisibility(View.GONE);
            conCourierList.setVisibility(View.GONE);
        });

        ivCloseCourier.setOnClickListener(v -> {
            conItemCheckout.setVisibility(View.VISIBLE);
            conProductList.setVisibility(View.GONE);
            conCourierList.setVisibility(View.GONE);
            conBankList.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        });

        ivCloseBank.setOnClickListener(v -> {
            conItemCheckout.setVisibility(View.VISIBLE);
            conProductList.setVisibility(View.GONE);
            conCourierList.setVisibility(View.GONE);
            conBankList.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        });

        ivBack.setOnClickListener(v -> onBackPressed());

        rbPick.setChecked(false);
        rbJne.setChecked(false);
        rbPos.setChecked(false);
        rbTiki.setChecked(false);

        View.OnClickListener courierClickListener = v -> {
            rbPick.setChecked(v.getId() == R.id.rbPick);
            rbJne.setChecked(v.getId() == R.id.rbJne);
            rbTiki.setChecked(v.getId() == R.id.rbTiki);
            rbPos.setChecked(v.getId() == R.id.rbPos);
        };

        rbBca.setChecked(false);
        rbBri.setChecked(false);
        rbMandiri.setChecked(false);
        rbCod.setChecked(false);

        View.OnClickListener bankClickListener = v -> {
            rbBri.setChecked(v.getId() == R.id.rbBri);
            rbBca.setChecked(v.getId() == R.id.rbBca);
            rbMandiri.setChecked(v.getId() == R.id.rbMandiri);
            rbCod.setChecked(v.getId() == R.id.rbCod);
        };

        // Load product data
        loadProductData();

        // Setup RecyclerView dan adapter
        setupAdapters();

        // Load address data
        loadAddressData();
    }

    private int parsePrice(String priceString) {
        // Contoh: "Rp. 11.000" -> 11000
        priceString = priceString.replace("Rp", "")
                .replace(".", "")
                .replace(",", "")
                .replaceAll("[^0-9]", "");
        try {
            return Integer.parseInt(priceString);
        } catch (NumberFormatException e) {
            return 0;
        }
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
            findViewById(R.id.conCourierList).setVisibility(View.GONE);

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

    private void updatePriceDisplay(double subtotal) {
        finalPrice = subtotal + shippingCost;
        tvSubTotal.setText(formatRupiah(subtotal));
        tvShippingCost.setText(formatRupiah(shippingCost));
        tvFinalPrice.setText(formatRupiah(finalPrice));
    }

    @NonNull
    private String formatRupiah(Double harga) {
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatRupiah.format(harga).replace(",00", "");
    }

    private void loadCourierData() {
        RegisterAPI apiService = ServerAPI.getClient().create(RegisterAPI.class);

        String[] couriers = {"jne", "tiki", "pos"};
        String origin = "399";
        String destination = String.valueOf(destinationId);
        int weight = 1000;

        // Loop untuk kurir dari RajaOngkir
        for (String courier : couriers) {
            Call<ResponseBody> call = apiService.cekongkir(origin, destination, weight, courier);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String json = response.body().string();
                            JSONObject jsonObject = new JSONObject(json);
                            JSONArray results = jsonObject.getJSONObject("rajaongkir").getJSONArray("results");

                            for (int i = 0; i < results.length(); i++) {
                                JSONObject courierObj = results.getJSONObject(i);
                                String courierName = courierObj.getString("code");

                                JSONArray costs = courierObj.getJSONArray("costs");

                                if (costs.length() > 0) {
                                    JSONObject serviceObj = costs.getJSONObject(0);
                                    String service = serviceObj.getString("service");
                                    JSONObject costDetail = serviceObj.getJSONArray("cost").getJSONObject(0);

                                    int cost = costDetail.getInt("value");
                                    String etd = costDetail.getString("etd");

                                    updateCourierView(courierName, etd, cost);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(CheckoutActivity.this, "Gagal parsing data kurir", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CheckoutActivity.this, "Respon tidak valid", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(CheckoutActivity.this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Tangani pickup secara manual karena tidak ada di RajaOngkir
        updateCourierView("pickup", "Ruko Mataram Plaza, D8, RW.9, Jagalan, Kec. Semarang Tengah, Kota Semarang, Jawa Tengah 50613", 0);
    }


    private void updateCourierView(String courier, String estimated, int cost) {
        String formattedCost = formatRupiah((double) cost);
        switch (courier) {
            case "jne":
                tvJneEstimated.setText("Estimated Arrival " + estimated + " days");
                tvJnePrice.setText(formattedCost);
                rbJne.setOnClickListener(v -> selectCourier("JNE", estimated, cost));
                break;
            case "tiki":
                tvTikiEstimated.setText("Estimated Arrival " + estimated + " days");
                tvTikiPrice.setText(formattedCost);
                rbTiki.setOnClickListener(v -> selectCourier("TIKI", estimated, cost));
                break;
            case "pos":
                tvPosEstimated.setText("Estimated Arrival " + estimated + " days");
                tvPosPrice.setText(formattedCost);
                rbPos.setOnClickListener(v -> selectCourier("POS", estimated, cost));
                break;
            case "pickup":
                tvPickEstimated.setText(estimated);
                tvPickPrice.setText("Gratis");
                rbPick.setOnClickListener(v -> selectCourier("PICKUP", "0", 0));
                break;
        }
    }

    private void selectCourier(String name, String estimated, int cost) {
        // Uncheck all radio buttons
        rbPick.setChecked(false);
        rbJne.setChecked(false);
        rbTiki.setChecked(false);
        rbPos.setChecked(false);

        // Check the selected one
        switch (name.toUpperCase()) {
            case "JNE": rbJne.setChecked(true); break;
            case "TIKI": rbTiki.setChecked(true); break;
            case "POS": rbPos.setChecked(true); break;
            case "PICKUP": rbPick.setChecked(true); break;
        }

        selectedCourierName = name;
        selectedCourierEstimated = estimated;
        selectedCourierCost = cost;
        shippingCost = (double) cost;

        updatePriceDisplay(subtotal);

        tvCourierName.setText(name);
        if (name.equalsIgnoreCase("pickup")) {
            tvEstimatedArrival.setText("Pickup at store location");
        } else {
            tvEstimatedArrival.setText("Estimated Arrival " + estimated + " days");
        }

        conItemCheckout.setVisibility(View.VISIBLE);
        conProductList.setVisibility(View.GONE);
        conCourierList.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
    }

    private void loadAddressData() {
        spUserID = getSharedPreferences("login_session", MODE_PRIVATE);
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
                        tvName.setText(activeAddress.getRecipt_name());
                        tvCity.setText(activeAddress.getCity_name());
                        tvProvince.setText(activeAddress.getProvince_name());
                        tvAddress.setText(activeAddress.getAddress());
                        tvPostalCode.setText(String.valueOf(activeAddress.getPostal_code()));
                        destinationId = activeAddress.getCity_id();
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
}