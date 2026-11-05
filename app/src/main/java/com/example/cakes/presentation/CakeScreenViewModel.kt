package com.example.cakes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cakes.common.Result
import com.example.cakes.domain.Cake
import com.example.cakes.domain.GetCakesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CakeScreenViewModel(private val getCakesUseCase: GetCakesUseCase) : ViewModel() {

	private val _state = MutableStateFlow(
		CakeListUiState()
	)
	val state = _state.asStateFlow()

	init {
		viewModelScope.launch { loadCakes() }
	}

	private suspend fun loadCakes() {
		_state.value = _state.value.copy(isLoading = true)
		when (val cakes = getCakesUseCase()) {
			is Result.Error -> _state.value = _state.value.copy(
				isLoading = false,
				isError = true,
			)

			is Result.Success -> {
				_state.value = _state.value.copy(
					isLoading = false, isError = false, cakes = cakes.data
				)
			}
		}
	}

	fun selectCake(cake: Cake?) {
		_state.value = _state.value.copy(selectedCake = cake)
	}

	fun reload() = viewModelScope.launch {
		loadCakes()
	}
}