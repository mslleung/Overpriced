package com.igrocery.overpriced.application.productpricehistory

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.productpricehistory.dtos.ProductWithMinMaxLatestPriceRecords
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.productpricehistory.IProductRepository
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.Flow
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
        productDescription: String,
        categoryId: Long?,
        priceAmountText: String,
        storeId: Long,
    ) {
        transaction.execute {
            val product = Product(
                name = productName.trim(),
                description = productDescription.trim(),
                categoryId = categoryId,
            )

            val productId = productRepository.insert(product)

            priceRecordService.createPriceRecord(
                priceAmountText = priceAmountText,
                productId = productId,
                storeId = storeId
            )
        }
    }

    suspend fun updateProduct(product: Product) {
        transaction.execute {
            productRepository.update(product)
        }
    }

    fun searchProductsByNamePaging(query: String): PagingSource<Int, Product> {
        return productRepository.searchProductsByNamePaging(query)
    }

    fun getProduct(name: String, description: String?): Flow<Product?> {
        return productRepository.getProductByNameAndDescription(name, description)
    }

    fun getProductsByCategoryIdPaging(categoryId: Long?): PagingSource<Int, Product> {
        return productRepository.getProductsByCategoryIdPaging(categoryId)
    }

    fun getProductsWithMinMaxLatestPriceRecordsByCategoryIdPaging(categoryId: Long?): PagingSource<Int, ProductWithMinMaxLatestPriceRecords> {
        return productRepository.getProductsWithMinMaxPriceRecordsByCategoryPaging(categoryId)
    }

}
