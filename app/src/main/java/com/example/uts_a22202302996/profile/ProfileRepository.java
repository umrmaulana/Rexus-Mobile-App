package com.example.uts_a22202302996.profile;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.uts_a22202302996.api.RegisterAPI;
import com.example.uts_a22202302996.api.ServerAPI;

import org.json.JSONObject;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileRepository {
    private final ProfileDao profileDao;
    private final RegisterAPI apiService;

    public ProfileRepository(Application application) {
        this.profileDao = AppDatabase.getInstance(application).profileDao();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.apiService = retrofit.create(RegisterAPI.class);
    }

    public LiveData<Profile> getProfile(String username) {
        refreshProfile(username); // Refresh data dari API
        return profileDao.getProfile(username);
    }

    private void refreshProfile(String username) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Response<ResponseBody> response = apiService.getProfile(username).execute();
                if(response.isSuccessful()) {
                    JSONObject json = new JSONObject(response.body().string());
                    if(json.getInt("result") == 1) {
                        JSONObject data = json.getJSONObject("data");

                        Profile profile = new Profile(username);
                        profile.nama = data.getString("nama");
                        profile.email = data.getString("email");
                        profile.foto = data.getString("foto");
                        profile.alamat = data.getString("alamat");
                        profile.kota = data.getString("kota");
                        profile.provinsi = data.getString("provinsi");
                        profile.telp = data.getString("telp");
                        profile.kodepos = data.getString("kodepos");
                        profile.lastUpdated = System.currentTimeMillis();

                        profileDao.insert(profile);
                    }
                }
            } catch (Exception e) {
                Log.e("Repository", "Error refreshing profile", e);
            }
        });
    }
}
