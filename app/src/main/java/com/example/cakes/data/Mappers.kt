package com.example.cakes.data

import com.example.cakes.domain.Cake
import com.example.cakes.network.CakeDto

// TODO add validation logic to the urls and fields make sure the images are correctly parsed or dropped
internal fun CakeDto.toDomain() = Cake(title, description, thumbnail)