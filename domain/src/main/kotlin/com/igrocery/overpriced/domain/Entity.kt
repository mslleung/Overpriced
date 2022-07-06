package com.igrocery.overpriced.domain

abstract class Entity(
    id: Long,
    creationTimestamp: Long,
    updateTimestamp: Long,
) {

    class InvalidIdException: IllegalArgumentException("Id is invalid.")
    class InvalidCreationTimestampException: IllegalArgumentException("Creation timestamp is not valid.")
    class InvalidUpdateTimestampException: IllegalArgumentException("Update timestamp is not valid.")

    var id: Long = id
        set(value) {
            if (value < 0) throw InvalidIdException()
            field = value
        }

    var creationTimestamp: Long = creationTimestamp // UTC time in milliseconds
        set(value) {
            if (value < 0) throw InvalidCreationTimestampException()
            field = value
        }

    var updateTimestamp: Long = updateTimestamp // UTC time in milliseconds
        set(value) {
            if (value < 0) throw InvalidUpdateTimestampException()
            field = value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Entity

        if (id != other.id) return false
        if (creationTimestamp != other.creationTimestamp) return false
        if (updateTimestamp != other.updateTimestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + creationTimestamp.hashCode()
        result = 31 * result + updateTimestamp.hashCode()
        return result
    }

}
