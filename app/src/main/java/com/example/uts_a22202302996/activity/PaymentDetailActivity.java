package com.example.uts_a22202302996.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.api.RegisterAPI;
import com.example.uts_a22202302996.api.ServerAPI;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentDetailActivity extends AppCompatActivity {

    private TextView tvBankName, tvAccountNumber, tvAccountName, tvTransferAmount, tvOrderNumber, tvInstructions;
    private ImageView imgPaymentProof;
    private Button btnSelectImage, btnUploadProof, btnViewOrders;
    private ProgressBar progressBar;
    private MaterialCardView cardUploadProof;

    private String orderId;
    private String orderNumber;
    private double transferAmount;
    private String paymentMethod;
    private String paymentNumber;
    private String paymentName;
    private Bitmap proofBitmap;

    private boolean isPaid = false;
    private String proofImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);

        // Initialize views
        tvBankName = findViewById(R.id.tvBankName);
        tvAccountNumber = findViewById(R.id.tvAccountNumber);
        tvAccountName = findViewById(R.id.tvAccountName);
        tvTransferAmount = findViewById(R.id.tvTransferAmount);
        tvOrderNumber = findViewById(R.id.tvOrderNumber);
        tvInstructions = findViewById(R.id.tvInstructions);
        imgPaymentProof = findViewById(R.id.imgPaymentProof);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnUploadProof = findViewById(R.id.btnUploadProof);
        btnViewOrders = findViewById(R.id.btnViewOrders);
        progressBar = findViewById(R.id.progressBar);
        cardUploadProof = findViewById(R.id.cardUploadProof);
        ImageView imgBack = (ImageView) findViewById(R.id.ivClose);

        // Get order data from intent
        try {
            Intent intent = getIntent();
            if (intent.hasExtra("order_data") && intent.hasExtra("payment_info")) {
                JSONObject orderData = new JSONObject(intent.getStringExtra("order_data"));
                JSONObject paymentInfo = new JSONObject(intent.getStringExtra("payment_info"));

                orderId = orderData.getString("id");
                orderNumber = orderData.getString("orderNumber");
                transferAmount = orderData.getDouble("finalPrice");
                paymentMethod = orderData.getString("paymentMethod");
                paymentNumber = paymentInfo.getString("account_number");
                paymentName = paymentInfo.getString("account_name");

                isPaid = intent.getBooleanExtra("is_paid", false);
                proofImageUrl = intent.getStringExtra("proof_image_url");

                setupPaymentInfo(paymentInfo);
                setupPaymentProofSection();
            } else {
                Toasty.error(this, "Data pembayaran tidak ditemukan", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (JSONException e) {
            Log.e("PaymentDetail", "Error parsing data", e);
            Toasty.error(this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }

        // Setup button listeners
        btnSelectImage.setOnClickListener(v -> selectPaymentProof());
        btnUploadProof.setOnClickListener(v -> uploadPaymentProof());
        btnViewOrders.setOnClickListener(v -> viewMyOrders());
        imgBack.setOnClickListener(v -> finish());
    }

    private void setupPaymentProofSection() {
        if (isPaid) {
            // Sembunyikan tombol upload & tampilkan bukti
            btnSelectImage.setVisibility(View.GONE);
            btnUploadProof.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);

            if (proofImageUrl != null && !proofImageUrl.isEmpty()) {
                // Tampilkan gambar bukti transfer (gunakan library seperti Glide)
                Glide.with(this)
                        .load(ServerAPI.BASE_URL + proofImageUrl)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .into(imgPaymentProof);
            } else {
                imgPaymentProof.setImageResource(R.drawable.ic_image_placeholder);
            }

            // Optional: disable klik
            imgPaymentProof.setClickable(false);
        } else {
            // Tampilkan form upload
            cardUploadProof.setVisibility(View.VISIBLE);
            btnSelectImage.setVisibility(View.VISIBLE);
            btnUploadProof.setVisibility(View.VISIBLE);
            imgPaymentProof.setImageResource(R.drawable.ic_image_placeholder);
            btnUploadProof.setEnabled(false);
        }
    }


    private void setupPaymentInfo(JSONObject paymentInfo) throws JSONException {
        // Set bank details based on payment method
        switch (paymentMethod.toLowerCase()) {
            case "bri":
                tvBankName.setText("BRI");
                tvAccountNumber.setText(paymentNumber);
                tvAccountName.setText(paymentName);
                break;
            case "bca":
                tvBankName.setText("BCA");
                tvAccountNumber.setText(paymentNumber);
                tvAccountName.setText(paymentName);
                break;
            case "mandiri":
                tvBankName.setText("Mandiri");
                tvAccountNumber.setText(paymentNumber);
                tvAccountName.setText(paymentName);
                break;
            default:
                tvBankName.setText("Bank");
                tvAccountNumber.setText(paymentNumber);
                tvAccountName.setText(paymentName);
        }

        // Set amount and order number
        tvTransferAmount.setText(formatRupiah(transferAmount));
        tvOrderNumber.setText("Order #" + orderNumber);
    }

    private void selectPaymentProof() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                proofBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                imgPaymentProof.setImageBitmap(proofBitmap);
                btnUploadProof.setEnabled(true);
            } catch (IOException e) {
                Log.e("PaymentDetail", "Error loading image", e);
                Toasty.error(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadPaymentProof() {
        if (proofBitmap == null) {
            Toasty.warning(this, "Pilih bukti transfer terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mengunggah bukti pembayaran...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            // Convert bitmap to file
            File file = new File(getCacheDir(), "payment_proof_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            proofBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();

            Log.d("UploadDebug", "File created: " + file.getAbsolutePath() + ", size: " + file.length());

            // Create multipart request
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            MultipartBody.Part proofPart = MultipartBody.Part.createFormData(
                    "proof_transfer", // Nama parameter harus sama dengan di PHP
                    file.getName(),
                    requestFile
            );

            RequestBody orderIdBody = RequestBody.create(MediaType.parse("text/plain"), orderId);
            Log.d("UploadDebug", "Order ID: " + orderId);

            // Call API
            RegisterAPI apiInterface = ServerAPI.getClient().create(RegisterAPI.class);
            Call<ResponseBody> call = apiInterface.uploadPaymentProof(orderIdBody, proofPart);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    progressDialog.dismiss();
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = response.body().string();
                            Log.d("UploadDebug", "Response: " + jsonResponse);

                            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

                            if (jsonObject.get("success").getAsBoolean()) {
                                Toasty.success(PaymentDetailActivity.this,
                                        "Bukti pembayaran berhasil diunggah",
                                        Toast.LENGTH_SHORT).show();

                                // Hide upload section
                                cardUploadProof.setVisibility(View.GONE);
                                btnUploadProof.setEnabled(false);

                                // PERBAIKAN: Simpan path bukti transfer jika diperlukan
                                if (jsonObject.has("proof_path")) {
                                    String proofPath = jsonObject.get("proof_path").getAsString();
                                    Log.d("UploadDebug", "Proof path: " + proofPath);
                                }
                            } else {
                                String errorMsg = jsonObject.has("message") ?
                                        jsonObject.get("message").getAsString() : "Unknown error";

                                Toasty.error(PaymentDetailActivity.this,
                                        "Gagal: " + errorMsg,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toasty.error(PaymentDetailActivity.this,
                                    "Gagal mengunggah: " + response.code() + " - " + response.message(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("PaymentDetail", "Error processing response", e);
                        Toasty.error(PaymentDetailActivity.this,
                                "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e("UploadDebug", "Network error", t);
                    Toasty.error(PaymentDetailActivity.this,
                            "Jaringan error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            progressDialog.dismiss();
            Log.e("PaymentDetail", "Error creating file", e);
            Toasty.error(this, "Error membuat file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void viewMyOrders() {
        Intent intent = new Intent(this, OrderHistoryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private String formatRupiah(double number) {
        java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("id", "ID"));
        return format.format(number).replace(",00", "");
    }
}