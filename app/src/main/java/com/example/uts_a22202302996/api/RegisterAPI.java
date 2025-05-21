package com.example.uts_a22202302996.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RegisterAPI {
    @FormUrlEncoded
    @POST("get_login.php")
    Call<ResponseBody> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("post_register.php")
    Call<ResponseBody> register(
            @Field("email") String email,
            @Field("nama") String nama,
            @Field("username") String username,
            @Field("password") String password
    );

    @GET("get_profile.php")
    Call<ResponseBody> getProfile(
            @Query("username") String username
    );

    @FormUrlEncoded
    @POST("post_profile.php")
    Call<ResponseBody> updateProfile(
            @Field("nama") String nama,
            @Field("alamat") String alamat,
            @Field("kota") String kota,
            @Field("provinsi") String provinsi,
            @Field("telp") String telp,
            @Field("kodepos") String kodepos,
            @Field("email") String email,
            @Field("username") String username
    );

    @GET("get_product.php")
    Call<ProductResponse> getProducts(@Query("kategori") String kategori,
                                      @Query("search") String search);

    @GET("get_populer_product.php")
    Call<ProductResponse> getPopulerProducts();

    @GET("get_category_product.php")
    Call<ProductResponse> getCategoryProducts(@Query("kategori") String kategori);

    @GET("get_categories.php")
    Call<CategoryResponse> getCategories();

    @Multipart
    @POST("upload_profile.php")
    Call<ResponseBody> uploadFoto(
            @Part("username") RequestBody username,
            @Part MultipartBody.Part foto
    );

    @FormUrlEncoded
    @POST("post_view.php")
    Call<ResponseBody> postView(
            @Field("kode") String kode,
            @Field("view") int view
    );
}
