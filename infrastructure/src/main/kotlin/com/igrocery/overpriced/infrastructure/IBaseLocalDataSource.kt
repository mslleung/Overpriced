package com.igrocery.overpriced.infrastructure

import com.igrocery.overpriced.domain.Id

internal interface IBaseLocalDataSource<IdT : Id, RoomEntityT> {

    suspend fun insert(entity: RoomEntityT): IdT

    suspend fun update(entity: RoomEntityT)

    suspend fun delete(entity: RoomEntityT)

}
