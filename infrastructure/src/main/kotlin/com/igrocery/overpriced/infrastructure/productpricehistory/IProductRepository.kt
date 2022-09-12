package com.igrocery.overpriced.infrastructure.productpricehistory

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.infrastructure.BaseRepository
import kotlinx.coroutines.flow.Flow

interface IProductRepository : BaseRepository<Product> {

    fun searchProductsByNamePaging(query: String): PagingSource<Int, Product>

    fun getProductByNameAndDescription(
        name: String,
        description: String?
    ): Flow<Product?>

    fun getProductsByCategoryIdPaging(categoryId: Long?): PagingSource<Int, Product>

}