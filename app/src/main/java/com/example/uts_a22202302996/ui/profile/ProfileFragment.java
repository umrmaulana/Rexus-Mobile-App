package com.example.uts_a22202302996.ui.profile;

import static com.example.uts_a22202302996.auth.LoginActivity.URL;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.auth.LoginActivity;
import com.example.uts_a22202302996.databinding.FragmentProfileBinding;
import com.example.uts_a22202302996.profile.EditProfile;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding; // Binding untuk mengakses komponen UI

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inisialisasi ViewModel
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        // Inflate layout menggunakan ViewBinding
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Memuat nama pengguna dari SharedPreferences
        SharedPreferences sharedPreferences = requireActivity()
                .getSharedPreferences("login_session", Context.MODE_PRIVATE);
        String nama = sharedPreferences.getString("nama", "Guest");
        String email = sharedPreferences.getString("email", "Guest@gmail.com");
        String foto = sharedPreferences.getString("foto", "");
        String username = sharedPreferences.getString("username", "Guest");
        binding.txtWelcome.setText("Hi, " + nama);
        binding.txtEmail.setText(email);
        Log.d("ProfileFragment", "Foto URL: " + foto);
        Glide.with(this)
                .load(URL + "images/" + foto)
                .circleCrop()
                .error(R.drawable.ic_launcher_foreground)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(binding.imgProfile)
        ;

        // Mengatur listener untuk tombol edit profile
        binding.editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), EditProfile.class);
            intent.putExtra("email", email);
            intent.putExtra("nama", nama);
            intent.putExtra("foto", foto);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        // Mengatur listener untuk tombol whatsapp
        binding.whatsapp.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=62895616797777&text=Hello"));
            startActivity(intent);
        });

        // Mengatur listener untuk tombol instagram
        binding.instagram.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://www.instagram.com/klacomputer.id/"));
            startActivity(intent);
        });

        // Mengatur listener untuk tombol tiktok
        binding.tiktok.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://www.tiktok.com/@klacomputer.id"));
            startActivity(intent);
        });

        // Mengatur listener untuk tombol website
        binding.website.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://kla.co.id/"));
            startActivity(intent);
        });

        // Listener tombol logout
        binding.btnLogout.setOnClickListener(v -> {
            // Menghapus sesi login
            requireActivity().getSharedPreferences("login_session", Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            // Mengarahkan ke LoginActivity
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Hindari memory leak dengan menghapus binding
    }
}