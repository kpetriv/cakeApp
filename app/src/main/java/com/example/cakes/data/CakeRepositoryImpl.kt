package com.example.cakes.data

import com.example.cakes.domain.CakeRepository
import com.example.cakes.common.Result
import com.example.cakes.common.safeRunCatching
import com.example.cakes.domain.Cake
import com.example.cakes.network.CakeService

class CakeRepositoryImpl(private val cakeService: CakeService) : CakeRepository {
	override suspend fun getCakes(): Result<List<Cake>> = safeRunCatching {
		cakeService.getCakes().map { cakeDto -> cakeDto.toDomain() }
	}.fold(
		onSuccess = { Result.Success(it) },
		onFailure = {
			// TODO add a logger object and collect errors, so the interface is abstarcted and
			// connect it to whatever logging being used, crashlytics, otel, etc.
			Result.Error(it, message = it.message)
		}
	)
}
