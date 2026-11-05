package com.example.cakes.common

import kotlin.coroutines.cancellation.CancellationException

/**
 * Like [runCatching], but rethrows [CancellationException] so coroutine cancellation
 * keeps propagating instead of being turned into a failure.
 */
inline fun <R> safeRunCatching(block: () -> R): kotlin.Result<R> =
	try {
		kotlin.Result.success(block())
	} catch (e: CancellationException) {
		throw e
	} catch (e: Throwable) {
		kotlin.Result.failure(e)
	}

/**
 * Receiver variant of [safeRunCatching], mirroring [T.runCatching].
 */
inline fun <T, R> T.safeRunCatching(block: T.() -> R): kotlin.Result<R> =
	try {
		kotlin.Result.success(block())
	} catch (e: CancellationException) {
		throw e
	} catch (e: Throwable) {
		kotlin.Result.failure(e)
	}
