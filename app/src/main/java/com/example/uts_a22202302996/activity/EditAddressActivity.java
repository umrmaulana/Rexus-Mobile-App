package com.example.uts_a22202302996.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.api.RegisterAPI;
import com.example.uts_a22202302996.api.ServerAPI;
import com.example.uts_a22202302996.model.Kota;
import com.example.uts_a22202302996.model.Provinsi;
import com.example.uts_a22202302996.model.ShipAddress;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditAddressActivity extends AppCompatActivity {
    Provinsi provinsi;
    Kota kota;

    int addressId;

    int idCity;
    int postal_code = 0;
    EditText tiName, tiAddress, tiPostalCode;
    Spinner spinnerProvince, spinnerCity;
    String provinsi_id, provinsi_name, kota_id, kota_name;

    ArrayList<String> province_name = new ArrayList<>();
    ArrayList<String> city_name = new ArrayList<>();
    ArrayList<Integer> province_id = new ArrayList<>();
    ArrayList<Integer> city_id = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_address);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addressId = getIntent().getIntExtra("address_id", 0);

        // Inisialisasi komponen UI dan logika lainnya di sini
        spinnerProvince = findViewById(R.id.spinProvinsi);
        spinnerCity = findViewById(R.id.spinKota);
        tiName = findViewById(R.id.tiName);
        tiAddress = findViewById(R.id.tiAddress);
        Button btnSave = findViewById(R.id.btnSubmit);

        getAddress();

        loadProvinsi();

        spinnerProvince.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                int idProvinsi = province_id.get(position);
                provinsi_id = String.valueOf(idProvinsi);
                provinsi_name = province_name.get(position);
                loadKota(idProvinsi);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Kosongkan atau abaikan
            }
        });

        spinnerCity.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                idCity = city_id.get(position);;
                kota_id = String.valueOf(idCity);
                kota_name = city_name.get(position);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Optional: handle case when nothing is selected
            }
        });

        btnSave.setOnClickListener(v -> {
            String name = tiName.getText().toString();
            String address = tiAddress.getText().toString();

            if(name.isEmpty() || address.isEmpty()){
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            updateAddress(name, address, provinsi_id, provinsi_name, kota_id, kota_name, postal_code);
        });

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> {
            finish();
        });

    }

    private void loadKota(int idProvinsi) {
        ServerAPI urlAPI = new ServerAPI();
        String URL = urlAPI.BASE_URL;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RegisterAPI api = retrofit.create(RegisterAPI.class);
        api.getKota(idProvinsi).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("Info Respon 101 : ", response.toString());
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    JSONArray kotaArray = json.getJSONObject("rajaongkir").getJSONArray("results");
                    city_name.clear();
                    city_id.clear();
                    for (int i = 0; i < kotaArray.length(); i++) {
                        kota = new Kota();
                        kota.setCity_id(kotaArray.getJSONObject(i).getInt("city_id"));
                        kota.setProvince_id(kotaArray.getJSONObject(i).getInt("province_id"));
                        kota.setCity_name(kotaArray.getJSONObject(i).getString("city_name"));
                        kota.setPostal_code(kotaArray.getJSONObject(i).getInt("postal_code"));
                        city_id.add(kota.getCity_id());
                        city_name.add(kota.getCity_name());
                        postal_code = kota.getPostal_code();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(EditAddressActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, city_name);
                    spinnerCity.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("Info Respon 102 ", e.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(EditAddressActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                Log.i("Info Respon 103 ", t.toString());
            }
        });
    }

    private void loadProvinsi() {
        ServerAPI urlAPI = new ServerAPI();
        String URL = urlAPI.BASE_URL;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RegisterAPI api = retrofit.create(RegisterAPI.class);
        api.getProvinsi().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("Info Respon 101 : ", response.toString());
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    JSONArray provinsiArray = json.getJSONObject("rajaongkir").getJSONArray("results");
                    for (int i = 0; i < provinsiArray.length(); i++) {
                        provinsi = new Provinsi();
                        provinsi.setProvince_id(provinsiArray.getJSONObject(i).getInt("province_id"));
                        provinsi.setProvince(provinsiArray.getJSONObject(i).getString("province"));
                        province_id.add(provinsi.getProvince_id());
                        province_name.add(provinsi.getProvince());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditAddressActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, province_name);
                    spinnerProvince.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("Info Respon 102 ", e.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(EditAddressActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                Log.i("Info Respon 103 ", t.toString());
            }
        });
    }

    private void updateAddress(String name, String address, String provinsi, String provinsi_name, String kota, String kota_name, int postal_code) {

        RegisterAPI api = ServerAPI.getClient().create(RegisterAPI.class);
        api.updateAddress(addressId, provinsi, provinsi_name, kota, kota_name, address, name, postal_code).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditAddressActivity.this, "Alamat diperbarui", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditAddressActivity.this, "Gagal memperbarui alamat", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditAddressActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAddress() {
        RegisterAPI api = ServerAPI.getClient().create(RegisterAPI.class);
        api.getAddressDetail(addressId).enqueue(new Callback<ShipAddress>() {
            @Override
            public void onResponse(Call<ShipAddress> call, Response<ShipAddress> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ShipAddress address = response.body();
                    tiName.setText(address.getRecipt_name());
                    tiAddress.setText(address.getAddress());
                    provinsi_id = String.valueOf(address.getProvince_id());
                    provinsi_name = address.getProvince_name();
                    kota_id = String.valueOf(address.getCity_id());
                    kota_name = address.getCity_name();
                    postal_code = address.getPostal_code();
                    // Set spinner selections
                    ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(EditAddressActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, province_name);
                    spinnerProvince.setAdapter(provinceAdapter);
                    int provincePosition = province_id.indexOf(address.getProvince_id());
                    if (provincePosition >= 0) {
                        spinnerProvince.setSelection(provincePosition);
                    }
                    ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(EditAddressActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, city_name);
                    spinnerCity.setAdapter(cityAdapter);
                    int cityPosition = city_id.indexOf(address.getCity_id());
                    if (cityPosition >= 0) {
                        spinnerCity.setSelection(cityPosition);
                    }
                } else {
                    Toast.makeText(EditAddressActivity.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ShipAddress> call, Throwable t) {
                Toast.makeText(EditAddressActivity.this, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show();
            }
        });
    }
}