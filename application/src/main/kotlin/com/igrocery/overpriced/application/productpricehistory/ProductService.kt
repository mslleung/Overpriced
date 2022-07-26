package com.igrocery.overpriced.application.productpricehistory

import androidx.paging.PagingSource
import com.igrocery.overpriced.shared.Logger
import com.igrocery.overpriced.infrastructure.productpricehistory.IProductRepository
import com.igrocery.overpriced.domain.productpricehistory.models.*
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.productpricehistory.ProductRepository
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
        categoryId: Long,
        productBarcode: String?,
        priceAmountText: String,
        storeId: Long,
    ) {
        transaction.execute {
            val product = Product(
                name = productName,
                description = productDescription,
                barcode = productBarcode,
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

    fun getProductsPagingSource(query: String? = null): PagingSource<Int, Product> {
        return productRepository.getProductsPagingSource(query)
    }

    fun getProduct(name: String, description: String?): Flow<Product?> {
        return productRepository.getProductByNameAndDescription(name, description)
    }

    fun getProduct(barcode: String): Flow<Product?> {
        return productRepository.getProductByBarcode(barcode)
    }

}
