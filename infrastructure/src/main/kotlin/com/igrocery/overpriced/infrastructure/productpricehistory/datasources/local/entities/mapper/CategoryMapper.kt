package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.mapper

import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.CategoryRoomEntity
import javax.inject.Singleton

internal class CategoryMapper {

    fun mapToData(category: Category): CategoryRoomEntity {
        return CategoryRoomEntity(
            id = category.id,
            icon = category.icon.name,
            name = category.name,
            creationTimestamp = category.creationTimestamp,
            updateTimestamp = category.updateTimestamp,
        )
    }

    fun mapFromData(categoryRoomEntity: CategoryRoomEntity): Category {
        return Category(
            id = categoryRoomEntity.id,
            icon = CategoryIcon.valueOf(categoryRoomEntity.icon),
            name = categoryRoomEntity.name,
            creationTimestamp = categoryRoomEntity.creationTimestamp,
            updateTimestamp = categoryRoomEntity.updateTimestamp,
        )
    }

}
