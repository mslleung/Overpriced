package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.mapper

import com.igrocery.overpriced.domain.productpricehistory.models.*
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.ProductRoomEntity

// data mapper for the Product aggregate root
internal class ProductMapper {

    fun mapToData(product: Product): ProductRoomEntity {
        return ProductRoomEntity(
            id = product.id,
            creationTimestamp = product.creationTimestamp,
            updateTimestamp = product.updateTimestamp,
            name = product.name,
            description = product.description,
            categoryId = product.categoryId,
        )
    }

    fun mapFromData(productRoomEntity: ProductRoomEntity): Product {
        return Product(
            id = productRoomEntity.id,
            creationTimestamp = productRoomEntity.creationTimestamp,
            updateTimestamp = productRoomEntity.updateTimestamp,
            name = productRoomEntity.name,
            description = productRoomEntity.description,
            categoryId = productRoomEntity.categoryId
        )
    }
}
