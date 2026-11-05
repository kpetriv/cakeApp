package com.example.cakes.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class SafeRunCatchingTest {

	@Test
	fun `returns success when block completes`() {
		assertEquals(kotlin.Result.success(42), safeRunCatching { 42 })
	}

	@Test
	fun `returns failure when block throws`() {
		val error = IOException("no network")

		val result = safeRunCatching { throw error }

		assertSame(error, result.exceptionOrNull())
	}

	@Test(expected = CancellationException::class)
	fun `rethrows cancellation`() {
		safeRunCatching { throw CancellationException("cancelled") }
	}

	@Test
	fun `receiver variant exposes receiver and rethrows cancellation`() {
		assertEquals(kotlin.Result.success(5), "cakes".safeRunCatching { length })
		assertTrue(
			runCatching { "cakes".safeRunCatching { throw CancellationException() } }
				.exceptionOrNull() is CancellationException
		)
	}
}
