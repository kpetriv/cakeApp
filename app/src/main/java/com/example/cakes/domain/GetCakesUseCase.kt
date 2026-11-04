package com.example.cakes.domain

import com.example.cakes.common.CakeRepository

class GetCakesUseCase(private val cakeRepository: CakeRepository) {
	suspend operator fun invoke(): List<Cake> = cakeRepository.getCakes()
		.distinct() // Must have 1. Remove duplicate entries
		.sortedBy { it.title } // Must have 2. Sort by name
}