package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities

import androidx.room.*
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantity
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantityUnit

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            CategoryRoomEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("category_id"),
            onDelete = ForeignKey.SET_NULL
        ),
    ],
    indices = [
        Index(value = ["name", "quantity_amount", "quantity_unit"], unique = true),
        Index(value = ["category_id"]),
    ]
)
internal data class ProductRoomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "creation_timestamp")
    val creationTimestamp: Long,
    @ColumnInfo(name = "update_timestamp")
    val updateTimestamp: Long,

    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "quantity")
    val quantity: String,
    @ColumnInfo(name = "category_id")
    val categoryId: Long?,
)



// mapping functions

internal fun ProductRoomEntity.toDomain(): Product {
    return Product(
        id = ProductId(id),
        creationTimestamp = creationTimestamp,
        updateTimestamp = updateTimestamp,
        name = name,
        quantity = quantity,
        categoryId = categoryId?.let { CategoryId(categoryId) }
    )
}

internal fun Product.toData(): ProductRoomEntity {
    return ProductRoomEntity(
        id = id.value,
        creationTimestamp = creationTimestamp,
        updateTimestamp = updateTimestamp,
        name = name,
        quantity = quantity,
        categoryId = categoryId?.value,
    )
}