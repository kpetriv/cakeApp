package com.example.cakes.di

import com.example.cakes.common.CakeRepository
import com.example.cakes.data.CakeRepositoryImpl
import com.example.cakes.domain.GetCakesUseCase
import com.example.cakes.network.CakeService
import org.koin.dsl.module

val appModule = module {
	single { CakeService.provide() }
	single<CakeRepository> { CakeRepositoryImpl(cakeService = get()) }
	single { GetCakesUseCase(cakeRepository = get()) }
}