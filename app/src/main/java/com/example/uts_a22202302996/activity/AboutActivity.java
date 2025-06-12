package com.example.uts_a22202302996.activity;

import android.os.Bundle;
import android.widget.TextView;
import com.example.uts_a22202302996.databinding.ActivityAboutBinding;
import androidx.appcompat.app.AppCompatActivity;
import com.example.uts_a22202302996.R;

public class AboutActivity extends AppCompatActivity {
    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivBack.setOnClickListener(v -> onBackPressed());
    }
}