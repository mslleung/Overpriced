package com.igrocery.overpriced.infrastructure.productpricehistory

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.productpricehistory.dtos.ProductWithMinMaxPrices
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantity
import com.igrocery.overpriced.infrastructure.BaseRepository
import kotlinx.coroutines.flow.Flow
import java.util.Currency

interface IProductRepository : BaseRepository<ProductId, Product> {

    fun searchProductsPaging(query: String): PagingSource<Int, Product>

    fun searchProductsPaging(
        query: String,
        currency: Currency
    ): PagingSource<Int, ProductWithMinMaxPrices>

    fun getProduct(productId: ProductId): Flow<Product>

    fun getProduct(
        name: String,
        quantity: String
    ): Flow<Product?>

    fun getProductsPaging(categoryId: CategoryId?): PagingSource<Int, Product>

    fun getProductWithMinMaxPrices(
        productId: ProductId,
        currency: Currency
    ): Flow<ProductWithMinMaxPrices?>

    fun getProductsWithMinMaxPricesPaging(
        categoryId: CategoryId?,
        currency: Currency
    ): PagingSource<Int, ProductWithMinMaxPrices>

}