package com.igrocery.overpriced.domain.productpricehistory.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GeoCoordinates(
    val latitude: Double,
    val longitude: Double
) : Parcelable {

    init {
        require(latitude in -90.0..90.0)
        require(longitude in -180.0..180.0)
    }

    override fun toString(): String {
        return "$latitude, $longitude"
    }

}
