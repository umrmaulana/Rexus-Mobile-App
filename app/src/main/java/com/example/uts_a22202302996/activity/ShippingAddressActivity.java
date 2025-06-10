package com.example.uts_a22202302996.activity;

import static com.example.uts_a22202302996.api.ServerAPI.BASE_URL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.adapter.ShipAddressAdapter;
import com.example.uts_a22202302996.api.RegisterAPI;
import com.example.uts_a22202302996.api.ServerAPI;
import com.example.uts_a22202302996.model.ShipAddress;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShippingAddressActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ShipAddressAdapter adapter;
    private List<ShipAddress> addressList = new ArrayList<>();
    private RegisterAPI apiInterface;
    private int userId;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            loadAddresses();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shipping_address);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.rvShippingAddresses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        apiInterface = ServerAPI.getClient().create(RegisterAPI.class);

        loadAddresses();

        FloatingActionButton fabAddAddress = findViewById(R.id.fabAddAddress);
        fabAddAddress.setOnClickListener(v -> {
            Intent intent = new Intent(ShippingAddressActivity.this, AddAddressActivity.class);
            startActivityForResult(intent, 100);
        });

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> {
            finish();
        });

    }

    private void loadAddresses() {
        SharedPreferences sharedPreferences = getSharedPreferences("login_session", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", 0);

        Call<List<ShipAddress>> call = apiInterface.getShipAddresses(userId);
        call.enqueue(new Callback<List<ShipAddress>>() {
            @Override
            public void onResponse(Call<List<ShipAddress>> call, Response<List<ShipAddress>> response) {
                if (response.isSuccessful()) {
                    addressList = response.body();
                    Log.d("ADDRESS_DATA", new Gson().toJson(addressList));
                    adapter = new ShipAddressAdapter(ShippingAddressActivity.this, addressList, new ShipAddressAdapter.OnAddressSelectedListener() {
                        @Override
                        public void onSelected(int addressId) {
                            updateActiveAddress(addressId);
                        }

                        @Override
                        public void onDelete(int addressId) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ShippingAddressActivity.this);
                            builder.setTitle("Hapus Alamat");
                            builder.setMessage("Apakah kamu yakin ingin menghapus alamat ini?");
                            builder.setPositiveButton("Ya", (dialog, which) -> {
                                deleteAddress(addressId); // Panggil fungsi delete jika user yakin
                            });
                            builder.setNegativeButton("Batal", (dialog, which) -> {
                                dialog.dismiss(); // Tutup dialog jika dibatalkan
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                        @Override
                        public void onEdit(int addressId) {
                            Intent intent = new Intent(ShippingAddressActivity.this, EditAddressActivity.class);
                            intent.putExtra("address_id", addressId);
                            startActivityForResult(intent, 100);
                        }

                    });
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("API_RESPONSE", "Response failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ShipAddress>> call, Throwable t) {
                Toast.makeText(ShippingAddressActivity.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateActiveAddress(int id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI apiService = retrofit.create(RegisterAPI.class);
        Call<Void> call = apiService.setActiveAddress(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ShippingAddressActivity.this, "Alamat aktif diperbarui", Toast.LENGTH_SHORT).show();
                    // Refresh data setelah update
                    loadAddresses();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(ShippingAddressActivity.this, "Gagal memperbarui alamat", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ShippingAddressActivity.this, "Gagal mengupdate alamat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteAddress(int addressId) {
        Call<Void> call = apiInterface.deleteAddress(addressId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ShippingAddressActivity.this, "Alamat berhasil dihapus", Toast.LENGTH_SHORT).show();
                    loadAddresses();
                } else {
                    Toast.makeText(ShippingAddressActivity.this, "Gagal menghapus alamat", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ShippingAddressActivity.this, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show();
            }
        });
    }

}