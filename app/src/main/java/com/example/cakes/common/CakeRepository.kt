package com.example.cakes.common

import com.example.cakes.domain.Cake

interface CakeRepository {
	suspend fun getCakes(): List<Cake>
}