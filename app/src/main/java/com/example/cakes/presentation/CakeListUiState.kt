package com.example.cakes.presentation

import com.example.cakes.domain.Cake

data class CakeListUiState(
	val cakes: List<Cake> = emptyList(),
	val isLoading: Boolean = true, // the list loads as soon as the screen starts
	val isError: Boolean = false,
	val selectedCake: Cake? = null,
)