package com.igrocery.overpriced.domain.productpricehistory.models

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
        this.icon = icon
        this.name = name.trim()
    }

    var icon: CategoryIcon

    var name: String
        set(value) {
            val trimmedValue = value.trim()
            if (trimmedValue.isBlank()) throw BlankNameException()
            if (trimmedValue.length > 100) throw NameLengthExceededException()
            field = trimmedValue
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Category

        if (icon != other.icon) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

}
