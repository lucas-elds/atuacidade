package br.edu.ifpb.atuacidade.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://nominatim.openstreetmap.org/"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain: Interceptor.Chain ->
            val request: Request = chain.request().newBuilder()
                .header("User-Agent", "ATUACIDADE/1.0")
                .build()
            chain.proceed(request)
        }
        .build()

    val instance: OpenStreetMapApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenStreetMapApi::class.java)
    }
}