package com.example.uts_a22202302996.activity;

import static com.example.uts_a22202302996.api.ServerAPI.BASE_URL_IMAGE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.api.RegisterAPI;
import com.example.uts_a22202302996.api.ServerAPI;
import com.example.uts_a22202302996.model.DataUser;
import com.example.uts_a22202302996.model.Profile;
import com.example.uts_a22202302996.ui.profile.ProfileViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditProfileAvticity extends AppCompatActivity {

    private ProfileViewModel viewModel;

    private String username, nama, foto;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private String currentPhotoPath;

    int resultAvatar = 0;
    SharedPreferences.Editor editor;

    ImageView ivBack, imgProfile;
    TextView btnChangePhoto;
    EditText etProfile_Nama, etProfile_Email, etProfile_Alamat, etProfile_Kota, etProfile_Provinsi, etProfile_Telp, etProfile_Kodepos;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username = getIntent().getStringExtra("username");

        // Inisialisasi UI
        ivBack = findViewById(R.id.ivBack);
        imgProfile = findViewById(R.id.imgProfile);
        btnChangePhoto = findViewById(R.id.btnChangeImage);
        etProfile_Nama = findViewById(R.id.tiName);
        etProfile_Email = findViewById(R.id.tiEmail);
        etProfile_Alamat = findViewById(R.id.tiAddress);
        etProfile_Kota = findViewById(R.id.tiCity);
        etProfile_Provinsi = findViewById(R.id.tiProvince);
        etProfile_Telp = findViewById(R.id.tiTelp);
        etProfile_Kodepos = findViewById(R.id.tiPostalCode);
        btnSubmit = findViewById(R.id.btnSubmit);

        getProfil(username);

        btnChangePhoto.setOnClickListener(v -> showImagePickerDialog());

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Di onCreate() EditProfile
        viewModel.getProfile().observe(this, profile -> {
            if(profile != null) {
                // Update UI dengan data yang ada
                etProfile_Nama.setText(profile.nama);
                etProfile_Email.setText(profile.email);
                etProfile_Alamat.setText(profile.alamat);
                etProfile_Kota.setText(profile.kota);
                etProfile_Provinsi.setText(profile.provinsi);
                etProfile_Telp.setText(profile.telp);
                etProfile_Kodepos.setText(profile.kodepos);
            } else {
                // Handle kasus data null
                Toast.makeText(this, "Memuat data profil...", Toast.LENGTH_SHORT).show();
            }
        });

        btnSubmit.setOnClickListener(v -> {
            Profile currentProfile = viewModel.getProfile().getValue();

            if(currentProfile == null || currentProfile.username.isEmpty()) {
                Toast.makeText(this, "Tunggu hingga data selesai dimuat", Toast.LENGTH_SHORT).show();
                return;
            }

            Profile updatedProfile = new Profile(currentProfile.username);
            updatedProfile.nama = etProfile_Nama.getText().toString().trim();
            updatedProfile.email = etProfile_Email.getText().toString().trim();
            updatedProfile.alamat = etProfile_Alamat.getText().toString().trim();
            updatedProfile.kota = etProfile_Kota.getText().toString().trim();
            updatedProfile.provinsi = etProfile_Provinsi.getText().toString().trim();
            updatedProfile.telp = etProfile_Telp.getText().toString().trim();
            updatedProfile.kodepos = etProfile_Kodepos.getText().toString().trim();
            updatedProfile.foto = currentProfile.foto; // Pertahankan foto jika tidak diubah

            updateProfil(updatedProfile);
        });closeContextMenu();closeContextMenu();

        ivBack.setOnClickListener(v -> navigateToHome());

    }

    private void navigateToHome() {
        finish();
    }

    private void getProfil(String vusername) {
        ServerAPI urlAPI = new ServerAPI();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urlAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        api.getProfile(vusername).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        if ("1".equals(json.getString("result"))) {
                            JSONObject data = json.getJSONObject("data");
                            etProfile_Nama.setText(getValidString(data, "nama"));
                            etProfile_Email.setText(getValidString(data, "email"));
                            etProfile_Alamat.setText(getValidString(data, "alamat"));
                            etProfile_Kota.setText(getValidString(data, "kota"));
                            etProfile_Provinsi.setText(getValidString(data, "provinsi"));
                            etProfile_Telp.setText(getValidString(data, "telp"));
                            etProfile_Kodepos.setText(getValidString(data, "kodepos"));
                            foto = getValidString(data, "foto");
                            if (!foto.isEmpty()) {
                                Glide.with(EditProfileAvticity.this)
                                        .load(BASE_URL_IMAGE + "avatar/" + foto)
                                        .centerCrop()
                                        .placeholder(R.drawable.ic_user)
                                        .error(R.drawable.ic_user)
                                        .into(imgProfile);
                            } else {
                                imgProfile.setImageResource(R.drawable.ic_user);
                            }
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void updateProfil(Profile profile) {
        DataUser data = new DataUser();
        data.setNama(etProfile_Nama.getText().toString().trim());
        data.setEmail(etProfile_Email.getText().toString().trim());
        data.setAlamat(etProfile_Alamat.getText().toString().trim());
        data.setKota(etProfile_Kota.getText().toString().trim());
        data.setProvinsi(etProfile_Provinsi.getText().toString().trim());
        data.setTelp(etProfile_Telp.getText().toString().trim());
        data.setKodepos(etProfile_Kodepos.getText().toString().trim());
        data.setUsername(username);

        ServerAPI urlAPI = new ServerAPI();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urlAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        api.updateProfile(data.getNama(), data.getAlamat(), data.getKota(), data.getProvinsi(),
                        data.getTelp(), data.getKodepos(), data.getEmail(), data.getUsername())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (response.body() != null) {
                                JSONObject json = new JSONObject(response.body().string());

                                Toast.makeText(EditProfileAvticity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                getProfil(data.getUsername());

                                String nama = json.getJSONObject("data").getString("nama");
                                String username = json.getJSONObject("data").getString("username");
                                String email = json.getJSONObject("data").getString("email");
                                String foto = json.getJSONObject("data").getString("foto");

                                // Simpan data login ke SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("login_session", MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("username", username);
                                editor.putString("nama", nama);
                                editor.putString("email", email);
                                editor.putString("foto", foto);
                                editor.apply();


                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        new AlertDialog.Builder(EditProfileAvticity.this)
                                .setMessage("Simpan Gagal, Error: " + t.toString())
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }
                });
    }

    private String getValidString(JSONObject json, String key) {
        try {
            if (json.has(key) && !json.isNull(key)) {
                return json.getString(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Gambar");
        builder.setItems(new CharSequence[]{"Galeri", "Kamera"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    openGallery();
                    break;
                case 1:
                    openCamera();
                    break;
            }
        });
        builder.show();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    @NonNull
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                imgProfile.setImageURI(selectedImageUri);
                uploadImage(selectedImageUri);
            } else if (requestCode == CAMERA_REQUEST) {
                File imgFile = new File(currentPhotoPath);
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imgProfile.setImageBitmap(bitmap);
                    uploadImage(Uri.fromFile(imgFile));
                }
            }
        }
    }

    private void uploadImage(Uri imageUri) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mengunggah foto...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            // Ambil input stream dari URI
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                progressDialog.dismiss();
                Toast.makeText(this, "Gagal membaca gambar", Toast.LENGTH_SHORT).show();
                return;
            }

            String path = getPathFromUri(imageUri);
            if (path == null) {
                progressDialog.dismiss();
                Toast.makeText(this, "Tidak dapat menemukan path dari gambar", Toast.LENGTH_SHORT).show();
                return;
            }
            File imageFile = new File(path);


            // Buat RequestBody dan MultipartBody
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            MultipartBody.Part fotoPart = MultipartBody.Part.createFormData("foto", imageFile.getName(), requestFile);
            RequestBody usernamePart = RequestBody.create(MediaType.parse("text/plain"), username); // Ganti 'username' sesuai variabel global kamu

            // Inisialisasi Retrofit
            ServerAPI urlAPI = new ServerAPI();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(urlAPI.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RegisterAPI api = retrofit.create(RegisterAPI.class);
            api.uploadFoto(usernamePart, fotoPart).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    progressDialog.dismiss();

                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            String resString = response.body().string();
                            JSONObject json = new JSONObject(resString);
                            Toast.makeText(EditProfileAvticity.this, json.getString("message"), Toast.LENGTH_SHORT).show();

                            if (json.getInt("result") == 1) {
                                // Refresh profil atau aksi lain
                                getProfil(username);
                            }
                        } else {
                            // Coba baca errorBody jika response.body() null
                            String error = response.errorBody() != null ? response.errorBody().string() : "Tidak diketahui";
                            Log.e("UploadFoto", "Error body: " + error);
                            Toast.makeText(EditProfileAvticity.this, "Gagal mengunggah: " + error, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(EditProfileAvticity.this, "Gagal parsing response", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(EditProfileAvticity.this, "Gagal terhubung: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            progressDialog.dismiss();
            e.printStackTrace();
            Toast.makeText(this, "Terjadi kesalahan saat memproses gambar", Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    private String getPathFromUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }

}