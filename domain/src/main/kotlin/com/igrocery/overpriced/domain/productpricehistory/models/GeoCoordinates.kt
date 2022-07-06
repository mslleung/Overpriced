package com.igrocery.overpriced.domain.productpricehistory.models

data class GeoCoordinates(
    val latitude: Double,
    val longitude: Double
) {
    class InvalidGeoCoordinatesException: IllegalArgumentException("Geo coordinates are not valid.")

    init {
        if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0)
            throw InvalidGeoCoordinatesException()
    }

    override fun toString(): String {
        return "$latitude, $longitude"
    }
}
