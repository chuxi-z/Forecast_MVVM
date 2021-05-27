package com.example.forecastmvvm.data.db

import android.content.Context
import androidx.room.*
import com.example.forecastmvvm.data.db.entity.CurrentWeatherEntry


@Database(entities = [CurrentWeatherEntry::class], version = 1, exportSchema = false)
@TypeConverters(ListToStringConvertor::class)
abstract class ForecastDatabase : RoomDatabase() {
    abstract fun currentWeatherDao(): CurrentWeatherDao

    companion object{
        private var instance:ForecastDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also{ instance = it }
        }

        private fun buildDatabase(context: Context):ForecastDatabase{
            return Room.databaseBuilder(context.applicationContext, ForecastDatabase::class.java, "forecast.db").build()
        }

    }
}
