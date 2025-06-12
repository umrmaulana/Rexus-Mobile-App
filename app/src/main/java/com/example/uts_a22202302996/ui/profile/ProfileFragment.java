package com.example.uts_a22202302996.ui.profile;

import static com.example.uts_a22202302996.api.ServerAPI.BASE_URL_IMAGE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.activity.ShippingAddressActivity;
import com.example.uts_a22202302996.api.RegisterAPI;
import com.example.uts_a22202302996.api.ServerAPI;
import com.example.uts_a22202302996.auth.LoginActivity;
import com.example.uts_a22202302996.databinding.FragmentProfileBinding;
import com.example.uts_a22202302996.activity.ChangePasswordActivity;
import com.example.uts_a22202302996.activity.EditProfileAvticity;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;

    private FragmentProfileBinding binding; // Binding untuk mengakses komponen UI

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inisialisasi ViewModel
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        // Memuat nama pengguna dari SharedPreferences
        SharedPreferences sharedPreferences = requireActivity()
                .getSharedPreferences("login_session", Context.MODE_PRIVATE);

        String username = sharedPreferences.getString("username", "Guest");
        Log.d("ProfileFragment", "Username: " + username);

        if ("Guest".equals(username)) {
            // Inflate fragment_guest jika user adalah guest
            Log.d("ProfileFragment", "Inflating fragment_guest");
            View view = inflater.inflate(R.layout.fragment_guest, container, false);


            // Set up the OnClickListener for the guestLoginButton
            view.findViewById(R.id.guestLoginButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Clear the login session
                    SharedPreferences sharedPreferences = requireActivity()
                            .getSharedPreferences("login_session", Context.MODE_PRIVATE);
                    sharedPreferences.edit().clear().apply();

                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                }
            });

            return view;

        } else{
            // Inflate layout menggunakan ViewBinding
            Log.d("ProfileFragment", "Inflating fragment_profile");
            binding = FragmentProfileBinding.inflate(inflater, container, false);
            View root = binding.getRoot();

            loadProfileData();

            // Mengatur listener untuk tombol edit profile
            binding.editProfile.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), EditProfileAvticity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            });

            // Mengatur listener untuk tombol ganti password
            binding.changePassword.setOnClickListener(v->{
                Intent intent = new  Intent(requireContext(), ChangePasswordActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            });

            // Mengatur listener untuk tombol alamat pengiriman
            binding.shippingAddress.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), ShippingAddressActivity.class);
                startActivity(intent);
            });

            binding.orderHistory.setOnClickListener( v -> {
                Intent intent = new Intent(requireContext(), com.example.uts_a22202302996.activity.OrderHistoryActivity.class);
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
                // Menghapus data product views dari SharedPreferences
                requireActivity().getSharedPreferences("product_views", Context.MODE_PRIVATE)
                        .edit()
                        .clear()
                        .apply();
                // Menghapus data keranjang dari SharedPreferences
                requireActivity().getSharedPreferences("product", Context.MODE_PRIVATE)
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

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                binding.txtWelcome.setText(profile.nama);
                binding.txtEmail.setText(profile.email);
                Glide.with(this)
                        .load(ServerAPI.BASE_URL_IMAGE + "avatar/" + profile.foto)
                        .apply(new RequestOptions().circleCrop())
                        .into(binding.imgProfile);
            }
        });
    }

    private void loadProfileData() {
        // Ambil username dari SharedPreferences (hanya untuk inisialisasi)
        SharedPreferences sharedPreferences = requireActivity()
                .getSharedPreferences("login_session", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        ServerAPI urlAPI = new ServerAPI();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urlAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        api.getProfile(username).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        if (json.getInt("result") == 1) {
                            JSONObject data = json.getJSONObject("data");
                            updateUI(
                                    data.getString("nama"),
                                    data.getString("email"),
                                    data.getString("foto")
                            );
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Gagal memuat data profil");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                binding.loadingProgressBar.setVisibility(View.GONE);
                showError("Koneksi gagal: " + t.getMessage());
            }
        });
    }

    private void updateUI(String nama, String email, String foto) {
        binding.txtWelcome.setText(nama);
        binding.txtEmail.setText(email);
        Glide.with(this)
                .load(BASE_URL_IMAGE + "avatar/" + foto)
                .apply(new RequestOptions()
                        .circleCrop()
                        .error(R.drawable.ic_user)
                        .placeholder(R.drawable.ic_user)
                        .diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(binding.imgProfile);
    }

    private void showError(String message) {
        if (isAdded()) { // Check if the fragment is attached to an activity
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        } else {
            Log.e("ProfileFragment", "Fragment not attached to context. Error: " + message);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        loadProfileData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Hindari memory leak dengan menghapus binding
    }
}