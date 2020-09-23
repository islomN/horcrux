package uz.sicnt.eimzo.connection

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.Serializable

interface ApiInterface {

    @GET("timestamp")
    fun getMovies(@Query("signatureHex") signatureHex: String): Call<ResponseData>

}

data class ResponseData(
    @SerializedName("data") val data: String? = null
) : Serializable