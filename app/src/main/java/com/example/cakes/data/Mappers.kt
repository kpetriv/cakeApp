package com.example.cakes.data

import com.example.cakes.domain.Cake
import com.example.cakes.network.CakeDto

internal fun CakeDto.toDomain() = Cake(title, description, thumbnail)