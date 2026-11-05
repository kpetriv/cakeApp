package com.example.cakes

import com.example.cakes.domain.CakeRepository
import com.example.cakes.common.Result
import com.example.cakes.domain.Cake

/**
 * Test double for [CakeRepository]. Returns whatever [result] is set to.
 */
class FakeCakeRepository(
	var result: Result<List<Cake>> = Result.Success(emptyList())
) : CakeRepository {
	override suspend fun getCakes(): Result<List<Cake>> = result
}

fun cake(title: String) = Cake(
	title = title,
	description = "$title description",
	thumbnail = "https://www.example.com/$title.jpg"
)
