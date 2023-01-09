package com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.igrocery.overpriced.domain.grocerylist.models.GroceryList

@Entity(
    tableName = "grocery_lists",
)
internal data class GroceryListRoomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "creation_timestamp")
    val creationTimestamp: Long,
    @ColumnInfo(name = "update_timestamp")
    val updateTimestamp: Long,

    @ColumnInfo(name = "name")
    val name: String,
)


// mapping functions
internal fun GroceryListRoomEntity.toDomain(): GroceryList {
    return GroceryList(
        id = id,
        creationTimestamp = creationTimestamp,
        updateTimestamp = updateTimestamp,
        name = name,
    )
}

internal fun GroceryList.toData(): GroceryListRoomEntity {
    return GroceryListRoomEntity(
        id = id,
        creationTimestamp = creationTimestamp,
        updateTimestamp = updateTimestamp,
        name = name,
    )
}
