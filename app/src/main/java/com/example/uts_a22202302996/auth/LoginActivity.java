package com.example.uts_a22202302996.auth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uts_a22202302996.MainActivity;
import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.api.RegisterAPI;
import com.example.uts_a22202302996.api.ServerAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    public static final String URL = new ServerAPI().BASE_URL;

    ProgressDialog pd;
    Button btnLogin;
    EditText etUsername, etPassword;
    TextView tv_register, tv_forgot_password, tv_guest_login;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cek apakah user sudah login
        sharedPreferences = getSharedPreferences("login_session", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            // Jika sudah login, langsung buka MainActivity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        // Tampilkan layout login
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi komponen UI
        tv_register = findViewById(R.id.tvRegister);
        etUsername = findViewById(R.id.tiEmail);
        etPassword = findViewById(R.id.tiPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tv_forgot_password = findViewById(R.id.tvForgotPassword);
        tv_guest_login = findViewById(R.id.tvGuestLogin);

        // Klik "Belum punya akun?"
        tv_register.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        // Klik "Login sebagai tamu"
        tv_guest_login.setOnClickListener(view -> {
            // Simpan status login tamu
            editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putString("username", "Guest");
            editor.apply();

            // Buka MainActivity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });

        // Klik tombol login
        btnLogin.setOnClickListener(view -> {
            pd = new ProgressDialog(LoginActivity.this);
            pd.setTitle("Login...");
            pd.setMessage("Tunggu Sebentar...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            prosesLogin(etUsername.getText().toString(), etPassword.getText().toString());
        });
    }

    /**
     * Proses login ke server
     */
    void prosesLogin(String vusername, String vpassword) {
        pd.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);

        api.login(vusername, vpassword).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pd.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();
                        Log.d("LoginResponse", responseString);

                        JSONObject json = new JSONObject(responseString);

                        // Cek hasil login dari server
                        if (json.getString("result").equals("1")) {
                            // Ambil data dari response
                            String nama = json.getJSONObject("data").getString("nama");
                            String username = json.getJSONObject("data").getString("username");
                            String email = json.getJSONObject("data").getString("email");
                            String foto = json.getJSONObject("data").getString("foto");

                            // Simpan data login ke SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("login_session", MODE_PRIVATE);
                            editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("username", username);
                            editor.putString("nama", nama);
                            editor.putString("email", email);
                            editor.putString("foto", foto);
                            editor.apply();

                            Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            showErrorDialog("Username atau Password salah.");
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        showErrorDialog("Terjadi kesalahan saat memproses login.");
                    }
                } else {
                    showErrorDialog("Server tidak merespon dengan benar.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
                showErrorDialog("Gagal terhubung ke server.");
                Log.e("Login Error", t.toString());
            }
        });
    }

    /**
     * Tampilkan dialog error
     */
    void showErrorDialog(String message) {
        AlertDialog.Builder msg = new AlertDialog.Builder(LoginActivity.this);
        msg.setMessage(message)
                .setNegativeButton("Retry", null)
                .create()
                .show();
    }
}
