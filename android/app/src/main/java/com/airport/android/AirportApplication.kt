package com.airport.android

import android.app.Application
import com.airport.android.api.RetrofitClient
import com.airport.android.util.SessionManager

/**
 * Application class - initializes global dependencies
 */
class AirportApplication : Application() {

    lateinit var sessionManager: SessionManager
        private set

    override fun onCreate() {
        super.onCreate()
        
        // Initialize session manager
        sessionManager = SessionManager(this)
        
        // Initialize Retrofit with session manager
        RetrofitClient.init(sessionManager)
    }
}
