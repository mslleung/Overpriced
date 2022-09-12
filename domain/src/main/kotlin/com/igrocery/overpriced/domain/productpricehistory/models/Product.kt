package com.igrocery.overpriced.domain.productpricehistory.models

import com.igrocery.overpriced.domain.AggregateRoot

class Product : AggregateRoot {

    class BlankNameException : IllegalArgumentException("Name should not be blank.")
    class NameLengthExceededException : IllegalArgumentException("Name exceeded maximum length.")
    class DescriptionLengthExceededException :
        IllegalArgumentException("Description exceeded maximum length.")

    constructor(
        id: Long = 0,
        creationTimestamp: Long = 0,
        updateTimestamp: Long = 0,
        name: String,
        description: String,   // the product brand, weight/size/flavor etc.
        categoryId: Long?,
    ) : super(id, creationTimestamp, updateTimestamp) {
        this.name = name
        this.description = description
        this.categoryId = categoryId
    }

    constructor(product: Product) : super(
        product.id,
        product.creationTimestamp,
        product.updateTimestamp
    ) {
        this.name = product.name
        this.description = product.description
        this.categoryId = product.categoryId
    }

    var name: String
        set(value) {
            if (value.isBlank()) throw BlankNameException()
            if (value.length > 100) throw NameLengthExceededException()
            field = value
        }

    var description: String
        set(value) {
            if (value.length > 100) throw DescriptionLengthExceededException()
            field = value
        }

    var categoryId: Long?

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Product

        if (name != other.name) return false
        if (description != other.description) return false
        if (categoryId != other.categoryId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + categoryId.hashCode()
        return result
    }

}
