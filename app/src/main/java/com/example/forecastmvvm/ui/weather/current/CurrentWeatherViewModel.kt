package com.example.forecastmvvm.ui.weather.current

import androidx.lifecycle.ViewModel
import com.example.forecastmvvm.data.db.repository.ForecastRepository
import com.example.forecastmvvm.internal.LazyDeferred
import com.example.forecastmvvm.internal.UnitSystem

class CurrentWeatherViewModel(
        private val forecastRepository: ForecastRepository
) : ViewModel() {
    private val unitSystem = UnitSystem.METRIC

    val isMetric: Boolean
        get() = unitSystem == UnitSystem.METRIC


    val weather by LazyDeferred {
        forecastRepository.getCurrentWeather(isMetric)
    }
}