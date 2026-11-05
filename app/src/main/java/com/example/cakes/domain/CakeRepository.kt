package com.example.cakes.domain

import com.example.cakes.common.Result

interface CakeRepository {
	suspend fun getCakes(): Result<List<Cake>>
}