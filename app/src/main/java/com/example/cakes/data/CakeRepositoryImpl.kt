package com.example.cakes.data

import com.example.cakes.common.CakeRepository
import com.example.cakes.domain.Cake
import com.example.cakes.network.CakeService

class CakeRepositoryImpl(val cakeService: CakeService) : CakeRepository {
	override suspend fun getCakes(): List<Cake> =
		cakeService.getCakes().map { cakeDto -> cakeDto.toDomain() }
}
