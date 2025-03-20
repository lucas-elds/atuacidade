package br.edu.ifpb.atuacidade.util

import android.util.Log
import br.edu.ifpb.atuacidade.data.model.AddressDetails
import br.edu.ifpb.atuacidade.data.model.GeocodeResponse
import br.edu.ifpb.atuacidade.data.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun fetchAddress(lat: Double, lon: Double, callback: (AddressDetails?) -> Unit) {
    val call = RetrofitClient.instance.getAddress(lat, lon)

    call.enqueue(object : Callback<GeocodeResponse> {
        override fun onResponse(
            call: Call<GeocodeResponse>,
            response: Response<GeocodeResponse>
        ) {
            if (response.isSuccessful) {
                val geocodeResponse = response.body()
                geocodeResponse?.let {
                    callback(it.address)
                    return
                }
            }
            callback(null)
        }

        override fun onFailure(call: Call<GeocodeResponse>, t: Throwable) {
            Log.e("API", "Erro na requisição: ${t.message}")
            callback(null)
        }
    })
}

