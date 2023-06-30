package com.igrocery.overpriced.application.productpricehistory

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.domain.productpricehistory.dtos.ProductWithMinMaxPrices
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantity
import com.igrocery.overpriced.domain.productpricehistory.models.SaleQuantity
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.productpricehistory.IProductRepository
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("unused")
private val log = Logger { }

@Singleton
class ProductService @Inject constructor(
    private val priceRecordService: PriceRecordService,
    private val productRepository: IProductRepository,
    private val transaction: Transaction
) {

    suspend fun createProductWithPriceRecord(
        productName: String,
        productQuantity: String,
        categoryId: CategoryId?,
        priceAmountText: String,
        quantity: SaleQuantity,
        isSale: Boolean,
        storeId: StoreId,
    ) {
        transaction.execute {
            val product = Product(
                name = productName.trim(),
                quantity = productQuantity,
                categoryId = categoryId,
            )

            val productId = productRepository.insert(product)

            priceRecordService.createPriceRecord(
                productId = productId,
                priceAmountText = priceAmountText,
                quantity = quantity,
                isSale = isSale,
                storeId = storeId,
            )
        }
    }

    suspend fun updateProduct(product: Product) {
        transaction.execute {
            productRepository.update(product)
        }
    }

    fun searchProductsPaging(query: String): PagingSource<Int, Product> {
        return productRepository.searchProductsPaging(query)
    }

    fun searchProductsWithMinMaxPricesPaging(
        query: String,
        currency: Currency
    ): PagingSource<Int, ProductWithMinMaxPrices> {
        return productRepository.searchProductsPaging(query, currency)
    }

    fun getProduct(name: String, quantity: String): Flow<Product?> {
        return productRepository.getProduct(name, quantity)
    }

    fun getProduct(productId: ProductId): Flow<Product> {
        return productRepository.getProduct(productId)
    }

    fun getProductsPaging(categoryId: CategoryId?): PagingSource<Int, Product> {
        return productRepository.getProductsPaging(categoryId)
    }

    fun getProductWithMinMaxPrices(
        productId: ProductId,
        currency: Currency
    ): Flow<ProductWithMinMaxPrices?> {
        return productRepository.getProductWithMinMaxPrices(
            productId,
            currency
        )
    }

    fun getProductsWithMinMaxPricesPaging(
        categoryId: CategoryId?,
        currency: Currency
    ): PagingSource<Int, ProductWithMinMaxPrices> {
        return productRepository.getProductsWithMinMaxPricesPaging(
            categoryId,
            currency
        )
    }

}
