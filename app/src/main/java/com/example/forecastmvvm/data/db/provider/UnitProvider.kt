package com.example.forecastmvvm.data.db.provider

import com.example.forecastmvvm.internal.UnitSystem

interface UnitProvider {
    fun getUnitSystem(): UnitSystem
}