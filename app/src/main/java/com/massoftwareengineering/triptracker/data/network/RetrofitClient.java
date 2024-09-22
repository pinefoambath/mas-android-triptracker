package com.massoftwareengineering.triptracker.data.network;

import com.massoftwareengineering.triptracker.data.repository.TripService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://tripmanager.azurewebsites.net/";
    private static final long CONNECT_TIMEOUT_SECONDS = 60L;
    private static final long READ_TIMEOUT_SECONDS = 60L;
    private static final long WRITE_TIMEOUT_SECONDS = 60L;

    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static TripService getTripService() {
        return getRetrofitInstance().create(TripService.class);
    }
}
