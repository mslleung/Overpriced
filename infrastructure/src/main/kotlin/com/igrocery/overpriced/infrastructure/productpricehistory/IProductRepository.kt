package com.igrocery.overpriced.infrastructure.productpricehistory

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.productpricehistory.dtos.ProductWithMinMaxPrices
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.infrastructure.BaseRepository
import kotlinx.coroutines.flow.Flow
import java.util.Currency

interface IProductRepository : BaseRepository<Product> {

    fun searchProductsByNamePaging(query: String): PagingSource<Int, Product>

    fun searchProductsByNameWithMinMaxPricesPaging(
        query: String,
        currency: Currency
    ): PagingSource<Int, ProductWithMinMaxPrices>

    fun getProductById(productId: Long): Flow<Product?>

    fun getProductByNameAndDescription(
        name: String,
        description: String?
    ): Flow<Product?>

    fun getProductsByCategoryIdPaging(categoryId: Long?): PagingSource<Int, Product>

    fun getProductsWithMinMaxPricesByProductIdAndCurrency(
        productId: Long,
        currency: Currency
    ): Flow<ProductWithMinMaxPrices?>

    fun getProductsWithMinMaxPricesByCategoryIdAndCurrencyPaging(
        categoryId: Long?,
        currency: Currency
    ): PagingSource<Int, ProductWithMinMaxPrices>

}