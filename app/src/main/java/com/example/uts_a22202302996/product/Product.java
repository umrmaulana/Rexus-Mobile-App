package com.example.uts_a22202302996.product;

import com.google.gson.annotations.SerializedName;

public class Product {
    private int qty = 1;

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    @SerializedName("kode")
    private String kode;

    @SerializedName("merk")
    private String merk;

    @SerializedName("kategori")
    private String kategori;

    @SerializedName("hargabeli")
    private String hargaBeli;

    @SerializedName("hargajual")
    private String hargaJual;

    @SerializedName("stok")
    private String stok;

    @SerializedName("foto")
    private String foto;

    @SerializedName("deskripsi")
    private String deskripsi;

    public String getKode() { return kode; }
    public String getMerk() { return merk; }
    public String getKategori() { return kategori; }
    public String getHargaBeli() { return hargaBeli; }
    public String getHargaJual() { return hargaJual; }
    public String getStok() { return stok; }
    public String getFoto() { return foto; }
    public String getDeskripsi() { return deskripsi; }
}
