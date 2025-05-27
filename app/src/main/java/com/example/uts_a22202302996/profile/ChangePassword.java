package com.example.uts_a22202302996.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.api.RegisterAPI;
import com.example.uts_a22202302996.api.ServerAPI;
import com.example.uts_a22202302996.ui.profile.ProfileViewModel;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends AppCompatActivity {

    private String username;
    private ProfileViewModel viewModel;
    SharedPreferences.Editor editor;

    EditText etOld_Password, etPassword, etConfirm_Password;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        viewModel = new ProfileViewModel(getApplication());

        username = getIntent().getStringExtra("username");

        etOld_Password = findViewById(R.id.tiOldPassword);
        etPassword = findViewById(R.id.tiPassword);
        etConfirm_Password = findViewById(R.id.tiConfirmPassword);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(view -> {
            String oldPass = etOld_Password.getText().toString().trim();
            String newPass = etPassword.getText().toString().trim();
            String confirmPass = etConfirm_Password.getText().toString().trim();

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Password baru tidak cocok", Toast.LENGTH_SHORT).show();
                return;
            }

            RegisterAPI registerAPI = ServerAPI.getClient().create(RegisterAPI.class);
            Call<ResponseBody> call = registerAPI.changePassword(username, oldPass, newPass);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            String result = response.body().string();
                            JSONObject json = new JSONObject(result);
                            String status = json.getString("status");
                            String message = json.getString("message");

                            Toast.makeText(ChangePassword.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChangePassword.this, "Gagal menghubungi server", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(ChangePassword.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

    }
}
