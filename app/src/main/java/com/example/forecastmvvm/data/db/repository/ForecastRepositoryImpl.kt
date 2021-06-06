package com.example.forecastmvvm.data.db.repository

import androidx.lifecycle.LiveData
import com.example.forecastmvvm.data.db.CurrentWeatherDao
import com.example.forecastmvvm.data.db.WeatherLocationDao
import com.example.forecastmvvm.data.db.entity.WeatherLocation
import com.example.forecastmvvm.data.db.provider.LocationProvider
import com.example.forecastmvvm.data.db.unitlocalized.UnitSpecificCurrentWeatherEntry
import com.example.forecastmvvm.data.network.WeatherNetworkDataSource
import com.example.forecastmvvm.data.network.response.CurrentWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime 

class ForecastRepositoryImpl(
        private val currentWeatherDao: CurrentWeatherDao,
        private val weatherLocationDao: WeatherLocationDao,
        private val weatherNetworkDataSource: WeatherNetworkDataSource,
        private val locationProvider: LocationProvider
) : ForecastRepository {

    init {
        weatherNetworkDataSource.downloadCurrentWeather.observeForever{
            persistentFetchedCurrentWeather(it)
        }
    }


    override suspend fun getCurrentWeather(metric: Boolean): LiveData<out UnitSpecificCurrentWeatherEntry> {
        return withContext(Dispatchers.IO){

            //Network update the weather ------------------------------------------
            initWeatherData()
            return@withContext currentWeatherDao.getWeatherMetric()
        }
    }

    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
        return withContext(Dispatchers.IO){
            return@withContext weatherLocationDao.getLocation()
        }
    }


    //-----------------------------------------------------------------


    private suspend fun initWeatherData() {
        val lastLocation = weatherLocationDao.getLocation().value
        if(lastLocation == null || locationProvider.hasLocationChanged(lastLocation)){
            fetchCurrentWeather()
            return
        }

        if(isFetchedCurrentNeeded(lastLocation.zonedDateTime)){
            fetchCurrentWeather()
        }
    }

    private suspend fun fetchCurrentWeather(){
        weatherNetworkDataSource.fetchCurrentWeather(locationProvider.getPreferredLocationString())
    }


    private fun isFetchedCurrentNeeded(lastFetchedTime: ZonedDateTime): Boolean{
        val minusMinutes = ZonedDateTime.now().minusMinutes(30)
        return lastFetchedTime.isBefore(minusMinutes)
    }

    fun persistentFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse){
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDao.upsert(fetchedWeather.currentWeatherEntry)
            weatherLocationDao.upsert(fetchedWeather.location )
        }
    }
}