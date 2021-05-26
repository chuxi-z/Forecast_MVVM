package com.example.forecastmvvm.data

import com.example.forecastmvvm.data.response.CurrentWeatherResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


const val API_KEY = "4c2b96c4fd3022febb78a21d8676d3c7"

//http://api.weatherstack.com/current?access_key=4c2b96c4fd3022febb78a21d8676d3c7&query=London

interface ApixuWeatherApiService {

    @GET("current")
    fun getCurrentWeather(
        @Query("query") location: String
//        @Query("language") language: String = "en"
    ): Deferred<CurrentWeatherResponse>


    companion object{
        operator fun invoke(): ApixuWeatherApiService{
            val requestInterceptor = Interceptor{chain ->  
                val url = chain.request()
                        .url()
                        .newBuilder()
                        .addQueryParameter("access_key", API_KEY)
                        .build()
                val request = chain.request()
                        .newBuilder()
                        .url(url)
                        .build()

                return@Interceptor chain.proceed(request)
            }

            val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(requestInterceptor)
                    .build()

            return Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("http://api.weatherstack.com/")
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApixuWeatherApiService::class.java)
        }
    }

}