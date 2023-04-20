package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon

@Entity(
    tableName = "categories",
)
internal data class CategoryRoomEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "creation_timestamp")
    val creationTimestamp: Long,
    @ColumnInfo(name = "update_timestamp")
    val updateTimestamp: Long,

    @ColumnInfo(name = "icon")
    val icon: String,
    @ColumnInfo(name = "name")
    val name: String,
)



// mapping functions
internal fun CategoryRoomEntity.toDomain(): Category {
    return Category(
        id = CategoryId(id),
        creationTimestamp = creationTimestamp,
        updateTimestamp = updateTimestamp,
        icon = CategoryIcon.valueOf(icon),
        name = name,
    )
}

internal fun Category.toData(): CategoryRoomEntity {
    return CategoryRoomEntity(
        id = id.value,
        creationTimestamp = creationTimestamp,
        updateTimestamp = updateTimestamp,
        icon = icon.name,
        name = name,
    )
}
