package com.example.uts_a22202302996.activity;

import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddAddressActivity extends AppCompatActivity {
    Provinsi provinsi;
    Kota kota;

    String provinsi_id, provinsi_name, kota_id, kota_name;
    Spinner spinnerProvinsi, spinnerKota;

    ArrayList<String> province_name = new ArrayList<>();
    ArrayList<String> city_name = new ArrayList<>();
    ArrayList<Integer> province_id = new ArrayList<>();
    ArrayList<Integer> city_id = new ArrayList<>();

    int idCity;
    int postal_code = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_address);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the back button
        spinnerProvinsi = findViewById(R.id.spinProvinsi);
        spinnerKota = findViewById(R.id.spinKota);
        EditText tiName = findViewById(R.id.tiName);
        EditText tiAddress = findViewById(R.id.tiAddress);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        loadProvinsi();

        spinnerProvinsi.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
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

        spinnerKota.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
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

        // and set its click listener to finish the activity
        btnSubmit.setOnClickListener(v -> {
            // Handle the submit action here
            String name = tiName.getText().toString();
            String address = tiAddress.getText().toString();

            if(name.isEmpty() || address.isEmpty()){
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            prossesAddAddress(name, address, provinsi_id, provinsi_name, kota_id, kota_name, postal_code);
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddAddressActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, city_name);
                    spinnerKota.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("Info Respon 102 ", e.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AddAddressActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddAddressActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, province_name);
                    spinnerProvinsi.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("Info Respon 102 ", e.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AddAddressActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                Log.i("Info Respon 103 ", t.toString());
            }
        });
    }

    private void prossesAddAddress(String name, String address, String provinsi, String provinsi_name, String kota, String kota_name, int postal_code) {
        SharedPreferences sharedPreferences = getSharedPreferences("login_session", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", 1);

        RegisterAPI apiInterface = ServerAPI.getClient().create(RegisterAPI.class);

        Call<ResponseBody> call = apiInterface.addShipAddress(userId, provinsi, provinsi_name, kota, kota_name,address, name, postal_code);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        boolean success = jsonObject.getBoolean("success");
                        String message = jsonObject.getString("message");

                        if (success) {
                            Toast.makeText(AddAddressActivity.this, "Alamat berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(AddAddressActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(AddAddressActivity.this, "Terjadi kesalahan pada data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddAddressActivity.this, "Gagal menghubungi server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AddAddressActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}