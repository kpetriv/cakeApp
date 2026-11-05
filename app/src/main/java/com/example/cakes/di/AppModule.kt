package com.example.cakes.di

import com.example.cakes.BuildConfig
import com.example.cakes.domain.CakeRepository
import com.example.cakes.data.CakeRepositoryImpl
import com.example.cakes.domain.GetCakesUseCase
import com.example.cakes.network.CakeService
import com.example.cakes.presentation.CakeScreenViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val appModule = module {

	single {
		HttpLoggingInterceptor().apply {
			level = if (BuildConfig.DEBUG) {
				HttpLoggingInterceptor.Level.BODY
			} else {
				HttpLoggingInterceptor.Level.NONE
			}
		}
	}
	single {
		OkHttpClient.Builder()
			.addInterceptor(get<HttpLoggingInterceptor>())
			.build()
	}

	single { CakeService.provide(client = get()) }
	single<CakeRepository> { CakeRepositoryImpl(cakeService = get()) }
	single { GetCakesUseCase(cakeRepository = get()) }

	viewModel<CakeScreenViewModel>()
}