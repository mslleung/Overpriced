package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.mapper

import com.igrocery.overpriced.domain.productpricehistory.models.*
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.ProductRoomEntity

// data mapper for the Product aggregate root
internal class ProductMapper {

    fun mapToData(product: Product): ProductRoomEntity {
        return ProductRoomEntity(
            id = product.id,
            name = product.name,
            description = product.description,
            creationTimestamp = product.creationTimestamp,
            updateTimestamp = product.updateTimestamp,
            categoryId = product.categoryId,
        )
    }

    fun mapFromData(productRoomEntity: ProductRoomEntity): Product {
        return Product(
            id = productRoomEntity.id,
            name = productRoomEntity.name,
            description = productRoomEntity.description,
            creationTimestamp = productRoomEntity.creationTimestamp,
            updateTimestamp = productRoomEntity.updateTimestamp,
            categoryId = productRoomEntity.categoryId
        )
    }
}
