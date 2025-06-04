package com.example.uts_a22202302996.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class Profile {
    @PrimaryKey
    @NonNull
    public String username;

    public String nama;
    public String email;
    public String foto;
    public String alamat;
    public String kota;
    public String provinsi;
    public String telp;
    public String kodepos;
    public String password;
    public long lastUpdated;

    public Profile(@NonNull String username) {
        this.username = username;
        // Inisialisasi default value
        this.nama = "";
        this.email = "";
        this.foto = "";
        this.alamat = "";
        this.kota = "";
        this.provinsi = "";
        this.telp = "";
        this.kodepos = "";
        this.password = "";
    }
}

