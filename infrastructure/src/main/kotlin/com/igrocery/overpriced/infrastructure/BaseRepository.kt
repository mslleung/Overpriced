package com.igrocery.overpriced.infrastructure

import com.igrocery.overpriced.domain.AggregateRoot
import com.igrocery.overpriced.domain.Id

interface BaseRepository<IdT : Id, T : AggregateRoot> {

    suspend fun insert(item: T) : IdT

    suspend fun update(item: T)

    suspend fun delete(item: T)

}
