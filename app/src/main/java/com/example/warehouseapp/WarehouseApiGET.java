package com.example.warehouseapp;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface WarehouseApiGET {
    @POST("Warehouses")
    Call<Warehouse> createPost(@Body Warehouse warehouse);

    @PUT("Warehouses/{id}")
    Call<Warehouse> createPut(@Path("id") int id, @Body Warehouse warehouse);

    @DELETE("Warehouses/{id}")
    Call<Warehouse> createDelete(@Path("id") int id);
}
