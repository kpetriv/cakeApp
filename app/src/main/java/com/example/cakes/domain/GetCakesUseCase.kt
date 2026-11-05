package com.example.cakes.domain

import com.example.cakes.common.Result

class GetCakesUseCase(private val cakeRepository: CakeRepository) {
	suspend operator fun invoke(): Result<List<Cake>> =
		when (val result = cakeRepository.getCakes()) {
			is Result.Success -> Result.Success(
				data = result.data
					.distinct() // Must have 1. Remove duplicate entries. Using data class comp methods
					.sortedBy { it.title } // Must have 2. Sort by name
			)

			is Result.Error -> result
		}
}
