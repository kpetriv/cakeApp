package com.example.cakes.presentation

import com.example.cakes.domain.Cake

data class CakeListUiState(
	val cakes: List<Cake> = emptyList(),
	val isLoading: Boolean = false,
	val errorMessage: String? = null,
	val selectedCake: Cake? = null,
)