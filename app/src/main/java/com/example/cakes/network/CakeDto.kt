package com.example.cakes.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CakeDto(
	@SerialName("title")
	val title: String,
	@SerialName("desc")
	val description: String,
	@SerialName("image")
	val thumbnail: String,
)
