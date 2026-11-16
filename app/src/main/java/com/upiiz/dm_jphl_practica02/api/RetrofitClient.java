package com.upiiz.dm_jphl_practica02.api;

import com.google.gson.Gson;
import com.upiiz.dm_jphl_practica02.models.ApiErrorResponse;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static final String BASE_URL = "https://ejercicio-08-documentacion-y-endpoints.onrender.com/";

    private static volatile Retrofit retrofitInstance;
    private static volatile LibroApiService apiServiceInstance;

    private RetrofitClient() {
    }

    public static Retrofit getRetrofit() {
        if (retrofitInstance == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitInstance == null) {
                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(45, TimeUnit.SECONDS)
                            .readTimeout(45, TimeUnit.SECONDS)
                            .writeTimeout(45, TimeUnit.SECONDS)
                            .build();

                    retrofitInstance = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(okHttpClient)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofitInstance;
    }

    public static LibroApiService getApiService() {
        if (apiServiceInstance == null) {
            synchronized (RetrofitClient.class) {
                if (apiServiceInstance == null) {
                    apiServiceInstance = getRetrofit().create(LibroApiService.class);
                }
            }
        }
        return apiServiceInstance;
    }

    public static String obtenerMensajeError(Response<?> response) {
        if (response == null) {
            return "Sin respuesta del servidor";
        }
        try (ResponseBody errorBody = response.errorBody()) {
            if (errorBody != null) {
                String json = errorBody.string();
                if (!json.isEmpty()) {
                    ApiErrorResponse errorResponse = new Gson().fromJson(json, ApiErrorResponse.class);
                    if (errorResponse != null && errorResponse.getMensajeDetalle() != null) {
                        return errorResponse.getMensajeDetalle();
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return "No fue posible completar la operación en este momento";
    }
}
