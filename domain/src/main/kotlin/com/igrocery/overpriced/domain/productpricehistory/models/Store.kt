package com.igrocery.overpriced.domain.productpricehistory.models

import com.igrocery.overpriced.domain.AggregateRoot

class Store : AggregateRoot {

    class BlankNameException: IllegalArgumentException("Name should not be blank.")
    class NameLengthExceededException: IllegalArgumentException("Name exceeded maximum length.")

    constructor(
        id: Long = 0,
        creationTimestamp: Long = 0,
        updateTimestamp: Long = 0,
        name: String,
        address: Address,
    ) : super(id, creationTimestamp, updateTimestamp) {
        this.name = name
        this.address = address
    }

    var name: String
        set(value) {
            if (value.isBlank()) throw BlankNameException()
            if (value.length > 100) throw NameLengthExceededException()
            field = value
        }
    var address: Address

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Store

        if (name != other.name) return false
        if (address != other.address) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + address.hashCode()
        return result
    }
}
