package com.example.uts_a22202302996.api;

import com.example.uts_a22202302996.model.ShipAddress;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
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

    @GET("get_discount_product.php")
    Call<ProductResponse> getDiscountProducts();

    @Multipart
    @POST("upload_profile.php")
    Call<ResponseBody> uploadFoto(
            @Part("username") RequestBody username,
            @Part MultipartBody.Part foto
    );

    @POST("post_change_password.php")
    Call<ResponseBody> changePassword(
            @Query("username") String username,
            @Query("old_password") String oldPassword,
            @Query("new_password") String newPassword
    );

    @FormUrlEncoded
    @POST("post_view.php")
    Call<ResponseBody> postView(
            @Field("kode") String kode,
            @Field("view") int view
    );

    @GET("get_address.php")
    Call<List<ShipAddress>> getShipAddresses(@Query("user_id") int userId);

    @FormUrlEncoded
    @POST("post_active_address.php")
    Call<Void> setActiveAddress(@Field("id") int addressId);

    @GET("get_provinsi.php")
    Call<ResponseBody> getProvinsi();

    @GET("get_kota.php")
    Call<ResponseBody> getKota(
            @Query("province_id") int province_id
    );

    @FormUrlEncoded
    @POST("post_ship_address.php")
    Call<ResponseBody> addShipAddress(
            @Field("user_id") int userId,
            @Field("province_id") String province_id,
            @Field("province_name") String province_name,
            @Field("city_id") String city_id,
            @Field("city_name") String city_name,
            @Field("address") String address,
            @Field("recipt_name") String recipt_name,
            @Field("postal_code") int postal_code
    );

    @DELETE("delete_address.php")
    Call<Void> deleteAddress(@Query("id") int id);

    @GET("get_address_detail.php")
    Call<ShipAddress> getAddressDetail(@Query("id") int id);

    @FormUrlEncoded
    @POST("update_address.php")
    Call<Void> updateAddress(
            @Field("id") int id,
            @Field("province_id") String province_id,
            @Field("province_name") String province_name,
            @Field("city_id") String city_id,
            @Field("city_name") String city_name,
            @Field("address") String address,
            @Field("recipt_name") String recipt_name,
            @Field("postal_code") int postal_code
    );

}
