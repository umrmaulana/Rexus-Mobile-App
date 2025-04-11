package com.example.uts_a22202302996.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.uts_a22202302996.auth.LoginActivity;
import com.example.uts_a22202302996.databinding.FragmentProfileBinding;

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
        binding.txtWelcome.setText("Hi, " + nama);

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