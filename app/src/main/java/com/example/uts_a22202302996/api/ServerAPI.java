package com.example.uts_a22202302996.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerAPI {
//    public String BASE_URL = "http://10.0.2.2/webservice/";
public static final String BASE_URL="https://android.umrmaulana.my.id/api/";
public static final String BASE_URL_IMAGE=BASE_URL+"images/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
