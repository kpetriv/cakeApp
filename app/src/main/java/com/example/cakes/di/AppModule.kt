package com.example.cakes.di

import com.example.cakes.network.CakeService
import org.koin.dsl.module

val appModule = module {
	single { CakeService.provide() }
}