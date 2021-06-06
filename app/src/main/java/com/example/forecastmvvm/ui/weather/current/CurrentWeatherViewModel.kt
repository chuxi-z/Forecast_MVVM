package com.example.forecastmvvm.ui.weather.current

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.forecastmvvm.data.db.provider.UnitProvider
import com.example.forecastmvvm.data.db.repository.ForecastRepository
import com.example.forecastmvvm.internal.LazyDeferred
import com.example.forecastmvvm.internal.UnitSystem

class CurrentWeatherViewModel(
        private val forecastRepository: ForecastRepository,
        unitProvider: UnitProvider
) : ViewModel() {
    private val unitSystem = unitProvider.getUnitSystem()

    val isMetric: Boolean
        get() = unitSystem == UnitSystem.METRIC


    val weather by LazyDeferred {
        forecastRepository.getCurrentWeather(isMetric)
    }

    val weatherLocation by LazyDeferred {
        forecastRepository.getWeatherLocation()
    }
}