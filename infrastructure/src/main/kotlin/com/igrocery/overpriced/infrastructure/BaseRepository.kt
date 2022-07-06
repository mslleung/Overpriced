package com.igrocery.overpriced.infrastructure

import com.igrocery.overpriced.domain.AggregateRoot

interface BaseRepository<T : AggregateRoot> {

    suspend fun insert(item: T) : Long

    suspend fun update(item: T)

    suspend fun delete(item: T)

}
