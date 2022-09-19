package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.mapper

import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.CategoryRoomEntity

internal class CategoryMapper {

    fun mapToData(category: Category): CategoryRoomEntity {
        return CategoryRoomEntity(
            id = category.id,
            creationTimestamp = category.creationTimestamp,
            updateTimestamp = category.updateTimestamp,
            icon = category.icon.name,
            name = category.name,
        )
    }

    fun mapFromData(categoryRoomEntity: CategoryRoomEntity): Category {
        return Category(
            id = categoryRoomEntity.id,
            creationTimestamp = categoryRoomEntity.creationTimestamp,
            updateTimestamp = categoryRoomEntity.updateTimestamp,
            icon = CategoryIcon.valueOf(categoryRoomEntity.icon),
            name = categoryRoomEntity.name,
        )
    }

}
