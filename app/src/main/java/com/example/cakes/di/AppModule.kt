package com.example.cakes.di

import com.example.cakes.common.CakeRepository
import com.example.cakes.data.CakeRepositoryImpl
import com.example.cakes.domain.GetCakesUseCase
import com.example.cakes.network.CakeService
import com.example.cakes.presentation.CakeScreenViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val appModule = module {
	single { CakeService.provide() }
	single<CakeRepository> { CakeRepositoryImpl(cakeService = get()) }
	single { GetCakesUseCase(cakeRepository = get()) }

	viewModel<CakeScreenViewModel>()
}