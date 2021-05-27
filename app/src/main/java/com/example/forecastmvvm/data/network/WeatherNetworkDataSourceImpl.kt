package com.example.forecastmvvm.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.forecastmvvm.data.ApixuWeatherApiService
import com.example.forecastmvvm.data.network.response.CurrentWeatherResponse
import java.io.NotActiveException
import java.lang.reflect.Field

class WeatherNetworkDataSourceImpl(
        private val apixuWeatherApiService: ApixuWeatherApiService
) : WeatherNetworkDataSource {

    private val _downloadCurrentWeather = MutableLiveData<CurrentWeatherResponse>()

    override val downloadCurrentWeather: LiveData<CurrentWeatherResponse>
        get() = _downloadCurrentWeather

    override suspend fun fetchCurrentWeather(location: String) {
        try {
            val apixuWeatherApiService = apixuWeatherApiService
                    .getCurrentWeather(location)
                    .await()
            _downloadCurrentWeather.postValue(apixuWeatherApiService)
        }
        catch (e:NotActiveException){
            Log.e("Connectivity", "No net connected", e )
        }
    }
}