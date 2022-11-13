package com.igrocery.overpriced.domain

abstract class Entity(
    open val id: Long,
    open val creationTimestamp: Long,
    open val updateTimestamp: Long,
)

