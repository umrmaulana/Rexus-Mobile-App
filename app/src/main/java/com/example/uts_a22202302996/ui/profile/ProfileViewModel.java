package com.example.uts_a22202302996.ui.profile;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.uts_a22202302996.model.Profile;
import com.example.uts_a22202302996.profile.ProfileRepository;

public class ProfileViewModel extends AndroidViewModel { // Ganti ke AndroidViewModel
    private final ProfileRepository repository;
    private final LiveData<Profile> profile;
    private final MutableLiveData<Profile> _currentProfile = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new ProfileRepository(application);
        SharedPreferences prefs = application.getSharedPreferences("login_session", MODE_PRIVATE);
        String username = prefs.getString("username", "Guest");

        profile = repository.getProfile(username);

        // Tambahkan observer untuk profile
        profile.observeForever(profile -> {
            if(profile != null) {
                _currentProfile.setValue(profile);
            }
        });
    }

    public LiveData<Profile> getProfile() {
        return _currentProfile;
    }
}