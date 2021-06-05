package com.example.forecastmvvm.ui.weather.current

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.forecastmvvm.R
import com.example.forecastmvvm.internal.glide.GlideApp
import com.example.forecastmvvm.ui.base.ScopeFragment
import kotlinx.android.synthetic.main.current_weather_fragment.*
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class CurrentWeatherFragment : ScopeFragment(), KodeinAware {

    override val kodein: Kodein by closestKodein()

    private val viewModelFactory by instance<CurrentWeatherViewModelFactory>()


    private lateinit var viewModel: CurrentWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.current_weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CurrentWeatherViewModel::class.java)
        // TODO: Use the ViewModel

//        val apiService = ApixuWeatherApiService(ConnectivityInterceptorImpl(this.context!!))
//        val weatherNetworkDataSourceImpl = WeatherNetworkDataSourceImpl(apiService)
//        weatherNetworkDataSourceImpl.downloadCurrentWeather.observe(viewLifecycleOwner, Observer {
//            textView.text = it.toString()
//        })
//
//        GlobalScope.launch(Dispatchers.Main) {
//            weatherNetworkDataSourceImpl.fetchCurrentWeather("London")
//        }

        bindUI()
    }

    private fun bindUI() = launch {
        val weather = viewModel.weather.await()
        weather.observe(viewLifecycleOwner, Observer {
            if(it == null) return@Observer

            group_loading.visibility = View.GONE
            updateDateToToday()
            updateLocation("London")
            updateCondition(it.conditionText.substring(2, it.conditionText.length-2))
            updateTemperatures(it.temperature, it.feelsLikeTemperature)
            updateVisibility(it.visibilityDistance)
            updateWind(it.windDirection, it.windSpeed)
            updatePrecipitation(it.precipitationVolume)
            GlideApp.with(this@CurrentWeatherFragment)
                    .load("${it.conditionIconUrl.get(0)}")
                    .into(imageView_condition_icon)
        })
    }


    private fun updateLocation(location: String){
        (activity as? AppCompatActivity)?.supportActionBar?.title = location
    }

    private fun updateDateToToday(){
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Today"
    }

    private fun updateTemperatures(temperature: Double, feelsLike: Double){
        val unitAbbreviation = if (viewModel.isMetric) "°C" else "°F"
        textView_temperature.text = "$temperature$unitAbbreviation"
        textView_feels_like_temperature.text = "Feels like $feelsLike$unitAbbreviation"
    }

    private fun updateCondition(condition: String){
        textView_condition.text = condition
    }

    private fun updatePrecipitation(volumn: Double){
        val unitAbbreviation = if (viewModel.isMetric) "mm" else "in"
        textView_precipitation.text = "Precipitation: $volumn$unitAbbreviation"
    }

    private fun updateWind(windDirection: String, windSpeed: Double){
        val unitAbbreviation = if (viewModel.isMetric) "kph" else "mph"
        textView_wind.text = "Wind: $windDirection, $windSpeed $unitAbbreviation"
    }

    private fun updateVisibility(distance: Double){
        val unitAbbreviation = if (viewModel.isMetric) "km" else "mi."
        textView_visibility.text = "VisibilityDistance: $distance $unitAbbreviation"
    }
}