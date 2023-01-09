package com.igrocery.overpriced.infrastructure

import androidx.room.Database
import androidx.room.RoomDatabase
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.daos.GroceryListDao
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.daos.GroceryListItemDao
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.GroceryListItemRoomEntity
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.GroceryListRoomEntity
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos.CategoryDao
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos.PriceRecordDao
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos.ProductDao
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos.StoreDao
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.PriceRecordRoomEntity
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.ProductFtsRoomEntity
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.ProductRoomEntity
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.StoreRoomEntity
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.CategoryRoomEntity

@Database(
    entities = [
        CategoryRoomEntity::class,
        GroceryListRoomEntity::class,
        GroceryListItemRoomEntity::class,
        PriceRecordRoomEntity::class,
        ProductFtsRoomEntity::class,
        ProductRoomEntity::class,
        StoreRoomEntity::class,
    ],
    version = 1
)
internal abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val DB_NAME = "db"
    }

    abstract fun categoryDao(): CategoryDao

    abstract fun groceryListDao(): GroceryListDao

    abstract fun groceryListItemDao(): GroceryListItemDao

    abstract fun priceRecordDao(): PriceRecordDao

    abstract fun productDao(): ProductDao

    abstract fun storeDao(): StoreDao

}
