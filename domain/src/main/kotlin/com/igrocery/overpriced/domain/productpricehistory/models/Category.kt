package com.igrocery.overpriced.domain.productpricehistory.models

import android.net.Uri
import com.igrocery.overpriced.domain.AggregateRoot

class Category : AggregateRoot {

    class BlankNameException: IllegalArgumentException("Name should not be blank.")
    class NameLengthExceededException: IllegalArgumentException("Name exceeded maximum length.")

    constructor(
        id: Long = 0,
        creationTimestamp: Long = 0,
        updateTimestamp: Long = 0,
        icon: CategoryIcon,
        name: String,
    ) : super(id, creationTimestamp, updateTimestamp) {
        this.name = name
    }

    var name: String
        set(value) {
            if (value.isBlank()) throw BlankNameException()
            if (value.length > 100) throw NameLengthExceededException()
            field = value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Category

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

}
