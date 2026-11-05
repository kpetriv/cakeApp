package com.example.cakes

import android.app.Application
import com.example.cakes.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class CakeApp : Application() {
	override fun onCreate() {
		super.onCreate()

		startKoin {
			if (BuildConfig.DEBUG) androidLogger()
			androidContext(this@CakeApp)
			modules(appModule)
		}
	}
}