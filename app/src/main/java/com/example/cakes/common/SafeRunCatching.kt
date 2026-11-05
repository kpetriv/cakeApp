package com.example.cakes.common

import kotlin.coroutines.cancellation.CancellationException

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
