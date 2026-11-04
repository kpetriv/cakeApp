package com.example.cakes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cakes.domain.GetCakesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CakeScreenViewModel(val getCakesUseCase: GetCakesUseCase) : ViewModel() {

	private val _state = MutableStateFlow(
		CakeListUiState()
	)
	val state = _state.asStateFlow()

	init {
		viewModelScope.launch { loadCakes() }
	}

	private suspend fun loadCakes() {
		_state.value = _state.value.copy(isLoading = true)
		val cakes = getCakesUseCase()
		_state.value = _state.value.copy(
			isLoading = false,
			cakes = cakes
		)
	}
}