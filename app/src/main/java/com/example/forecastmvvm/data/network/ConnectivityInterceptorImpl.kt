package com.example.forecastmvvm.data.network

import android.content.Context
import android.net.ConnectivityManager
import com.example.forecastmvvm.internal.NoConnectivityException
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ConnectivityInterceptorImpl(
        context: Context
) : ConnectivityInterceptor {

    private val appContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if(!isOnline())
            throw NoConnectivityException()

        return chain.proceed(chain.request())
    }

    private fun isOnline(): Boolean{
        val systemService = appContext.getSystemService(Context.CONNECTIVITY_SERVICE)
        as ConnectivityManager
        val activeNetworkInfo = systemService.activeNetworkInfo

        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}