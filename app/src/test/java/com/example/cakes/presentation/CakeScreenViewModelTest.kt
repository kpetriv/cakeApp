package com.example.cakes.presentation

import com.example.cakes.FakeCakeRepository
import com.example.cakes.MainDispatcherRule
import com.example.cakes.cake
import com.example.cakes.common.Result
import com.example.cakes.domain.GetCakesUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class CakeScreenViewModelTest {

	@get:Rule
	val mainDispatcherRule = MainDispatcherRule()

	private val repository = FakeCakeRepository()

	private fun createViewModel() = CakeScreenViewModel(GetCakesUseCase(repository))

	@Test
	fun `on start with successful load, cakes are presented deduplicated and sorted`() = runTest {
		val victoria = cake("Victoria sponge")
		val banana = cake("Banana cake")
		repository.result = Result.Success(listOf(victoria, banana, victoria))

		val viewModel = createViewModel()

		assertEquals(
			CakeListUiState(cakes = listOf(banana, victoria), isLoading = false, isError = false),
			viewModel.state.value
		)
	}

	@Test
	fun `on start with failed load, error is presented`() = runTest {
		repository.result = Result.Error(IOException("no network"))

		val viewModel = createViewModel()

		assertEquals(
			CakeListUiState(cakes = emptyList(), isLoading = false, isError = true),
			viewModel.state.value
		)
	}

	@Test
	fun `on start with successful empty load, empty state is presented`() = runTest {
		repository.result = Result.Success(emptyList())

		val viewModel = createViewModel()

		assertEquals(
			CakeListUiState(cakes = emptyList(), isLoading = false, isError = false),
			viewModel.state.value
		)
	}

	@Test
	fun `reload after error clears error and presents cakes`() = runTest {
		repository.result = Result.Error(IOException("no network"))
		val viewModel = createViewModel()
		val banana = cake("Banana cake")
		repository.result = Result.Success(listOf(banana))

		viewModel.reload()

		assertEquals(
			CakeListUiState(cakes = listOf(banana), isLoading = false, isError = false),
			viewModel.state.value
		)
	}

	@Test
	fun `failed reload keeps cakes on screen and flags error`() = runTest {
		val banana = cake("Banana cake")
		repository.result = Result.Success(listOf(banana))
		val viewModel = createViewModel()
		repository.result = Result.Error(IOException("no network"))

		viewModel.reload()

		assertEquals(
			CakeListUiState(cakes = listOf(banana), isLoading = false, isError = true),
			viewModel.state.value
		)
	}

	@Test
	fun `refreshErrorShown clears error and keeps cakes`() = runTest {
		val banana = cake("Banana cake")
		repository.result = Result.Success(listOf(banana))
		val viewModel = createViewModel()
		repository.result = Result.Error(IOException("no network"))
		viewModel.reload()

		viewModel.refreshErrorShown()

		assertEquals(
			CakeListUiState(cakes = listOf(banana), isLoading = false, isError = false),
			viewModel.state.value
		)
	}

	@Test
	fun `selectCake updates selected cake`() = runTest {
		val banana = cake("Banana cake")
		repository.result = Result.Success(listOf(banana))
		val viewModel = createViewModel()

		viewModel.selectCake(banana)
		assertEquals(banana, viewModel.state.value.selectedCake)

		viewModel.selectCake(null)
		assertEquals(null, viewModel.state.value.selectedCake)
	}
}
