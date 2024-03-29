package com.kudziechase.travelmantics.model

import java.io.Serializable

data class TravelDeal(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var price: String = "",
    var imageUrl: String = "",
    var imageName: String = ""
) : Serializable