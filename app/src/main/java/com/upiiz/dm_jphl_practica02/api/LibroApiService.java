package com.upiiz.dm_jphl_practica02.api;

import com.upiiz.dm_jphl_practica02.models.LibroDto;
import com.upiiz.dm_jphl_practica02.models.LibroRequest;
import com.upiiz.dm_jphl_practica02.models.LibrosResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface LibroApiService {

    @GET("api/v1/libros")
    Call<LibrosResponse> obtenerLibros();

    @GET("api/v1/libros/{id}")
    Call<LibroDto> obtenerLibroPorId(@Path("id") int id);

    @POST("api/v1/libros")
    Call<LibroDto> crearLibro(@Body LibroRequest libro);

    @PUT("api/v1/libros/{id}")
    Call<LibroDto> actualizarLibro(@Path("id") int id, @Body LibroRequest libro);

    @DELETE("api/v1/libros/{id}")
    Call<Void> eliminarLibro(@Path("id") int id);
}
