package br.edu.ifpb.atuacidade.data.network

import br.edu.ifpb.atuacidade.data.model.GeocodeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenStreetMapApi {
    @GET("reverse")
    fun getAddress(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("format") format: String = "json"
    ): Call<GeocodeResponse>
}