package com.igrocery.overpriced.infrastructure.productpricehistory

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.infrastructure.BaseRepository
import kotlinx.coroutines.flow.Flow

interface IProductRepository : BaseRepository<Product> {

    fun getProductsPagingSource(query: String? = null): PagingSource<Int, Product>

    fun getProductByNameAndDescription(
        name: String,
        description: String?
    ): Flow<Product?>

    fun getProductByBarcode(
        barcode: String
    ): Flow<Product?>

}