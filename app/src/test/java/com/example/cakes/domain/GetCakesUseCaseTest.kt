package com.example.cakes.domain

import com.example.cakes.FakeCakeRepository
import com.example.cakes.cake
import com.example.cakes.common.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import java.io.IOException

class GetCakesUseCaseTest {

	private val repository = FakeCakeRepository()
	private val useCase = GetCakesUseCase(repository)

	@Test
	fun `removes duplicate cakes`() = runTest {
		val lemon = cake("Lemon cheesecake")
		repository.result = Result.Success(listOf(lemon, lemon.copy(), lemon))

		val result = useCase()

		assertEquals(Result.Success(listOf(lemon)), result)
	}

	@Test
	fun `sorts cakes by title`() = runTest {
		val victoria = cake("Victoria sponge")
		val banana = cake("Banana cake")
		val lemon = cake("Lemon cheesecake")
		repository.result = Result.Success(listOf(victoria, lemon, banana))

		val result = useCase()

		assertEquals(Result.Success(listOf(banana, lemon, victoria)), result)
	}

	@Test
	fun `removes duplicates and sorts in one pass`() = runTest {
		val victoria = cake("Victoria sponge")
		val banana = cake("Banana cake")
		repository.result = Result.Success(listOf(victoria, banana, victoria, banana))

		val result = useCase()

		assertEquals(Result.Success(listOf(banana, victoria)), result)
	}

	@Test
	fun `passes repository error through unchanged`() = runTest {
		val error = Result.Error(IOException("no network"), "no network")
		repository.result = error

		val result = useCase()

		assertSame(error, result)
	}
}
