package com.example.cakes.data

import com.example.cakes.domain.CakeRepository
import com.example.cakes.common.Result
import com.example.cakes.common.safeRunCatching
import com.example.cakes.domain.Cake
import com.example.cakes.network.CakeService

class CakeRepositoryImpl(val cakeService: CakeService) : CakeRepository {
	override suspend fun getCakes(): Result<List<Cake>> = safeRunCatching {
		cakeService.getCakes().map { cakeDto -> cakeDto.toDomain() }
	}.fold(
		onSuccess = { Result.Success(it) },
		onFailure = { Result.Error(it, message = it.message) }
	)
}
