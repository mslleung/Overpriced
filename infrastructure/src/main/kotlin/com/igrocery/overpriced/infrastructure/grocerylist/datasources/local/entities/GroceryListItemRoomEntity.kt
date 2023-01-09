package com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities

import androidx.room.*
import com.igrocery.overpriced.domain.grocerylist.models.GroceryListItem

@Entity(
    tableName = "grocery_list_items",
    foreignKeys = [
        ForeignKey(
            GroceryListRoomEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("grocery_list_id"),
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [
        Index(value = ["grocery_list_id"]),
    ]
)
internal data class GroceryListItemRoomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "creation_timestamp")
    val creationTimestamp: Long,
    @ColumnInfo(name = "update_timestamp")
    val updateTimestamp: Long,

    @ColumnInfo(name = "grocery_list_id")
    val groceryListId: Long,
    @ColumnInfo(name = "product_id")
    val productId: Long,
    @ColumnInfo(name = "quantity")
    val quantity: String,
)


// mapping functions
internal fun GroceryListItemRoomEntity.toDomain(): GroceryListItem {
    return GroceryListItem(
        id = id,
        creationTimestamp = creationTimestamp,
        updateTimestamp = updateTimestamp,
        groceryListId = groceryListId,
        productId = productId,
        quantity = quantity,
    )
}

internal fun GroceryListItem.toData(): GroceryListItemRoomEntity {
    return GroceryListItemRoomEntity(
        id = id,
        creationTimestamp = creationTimestamp,
        updateTimestamp = updateTimestamp,
        groceryListId = groceryListId,
        productId = productId,
        quantity = quantity,
    )
}