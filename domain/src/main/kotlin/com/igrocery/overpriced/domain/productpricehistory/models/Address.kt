package com.igrocery.overpriced.domain.productpricehistory.models

data class Address(
    val lines: String?,
    val geoCoordinates: GeoCoordinates
) {
    class AddressLinesLengthExceededException: IllegalArgumentException("Address lines length exceeded.")

    init {
        lines?.let {
            if (it.length > 500)
                throw AddressLinesLengthExceededException()
        }
    }

    override fun toString(): String {
        return lines ?: geoCoordinates.toString()
    }
}
