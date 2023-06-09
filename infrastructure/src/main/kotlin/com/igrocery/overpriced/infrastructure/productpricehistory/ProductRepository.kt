package com.igrocery.overpriced.infrastructure.productpricehistory

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.productpricehistory.dtos.ProductWithMinMaxPrices
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantity
import com.igrocery.overpriced.infrastructure.MappedPagingSource
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.di.DataSourceModule.LocalDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.ILocalProductDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.toData
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.toDomain
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.Currency
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("unused")
private val log = Logger { }

@Singleton
class ProductRepository @Inject internal constructor(
    @LocalDataSource private val localProductDataSource: ILocalProductDataSource,
    private val transaction: Transaction,
) : IProductRepository {

    override suspend fun insert(item: Product): ProductId {
        return transaction.execute {
            localProductDataSource.insert(item.toData())
        }
    }

    override suspend fun update(item: Product) {
        transaction.execute {
            localProductDataSource.update(item.toData())
        }
    }

    override suspend fun delete(item: Product) {
        transaction.execute {
            localProductDataSource.delete(item.toData())
        }
    }

    override fun searchProductsPaging(query: String): PagingSource<Int, Product> {
        return MappedPagingSource(
            dataPagingSource = localProductDataSource.searchProductsPaging(query),
            mapper = { it.toDomain() }
        )
    }

    override fun searchProductsPaging(
        query: String,
        currency: Currency
    ): PagingSource<Int, ProductWithMinMaxPrices> {
        return MappedPagingSource(
            dataPagingSource = localProductDataSource.searchProductsWithMinMaxPricesPaging(
                query,
                currency
            ),
            mapper = {
                ProductWithMinMaxPrices(
                    it.productRoomEntity.toDomain(),
                    it.minPrice,
                    it.maxPrice,
                    it.lastUpdatedTimestamp
                )
            }
        )
    }

    override fun getProduct(productId: ProductId): Flow<Product> {
        return localProductDataSource.getProduct(productId)
            .map { it.toDomain() }
            .distinctUntilChanged()
    }

    override fun getProduct(
        name: String,
        quantity: String
    ): Flow<Product?> {
        return localProductDataSource.getProduct(name, quantity)
            .map { it?.toDomain() }
    }

    override fun getProductsPaging(categoryId: CategoryId?): PagingSource<Int, Product> {
        return MappedPagingSource(
            dataPagingSource = localProductDataSource.getProductPaging(categoryId),
            mapper = { it.toDomain() }
        )
    }

    override fun getProductWithMinMaxPrices(
        productId: ProductId,
        currency: Currency
    ): Flow<ProductWithMinMaxPrices?> {
        return localProductDataSource.getProductWithMinMaxPrices(
            productId,
            currency
        ).map {
            it?.let {
                ProductWithMinMaxPrices(
                    it.productRoomEntity.toDomain(),
                    it.minPrice,
                    it.maxPrice,
                    it.lastUpdatedTimestamp
                )
            }
        }.distinctUntilChanged()
    }

    override fun getProductsWithMinMaxPricesPaging(
        categoryId: CategoryId?,
        currency: Currency
    ): PagingSource<Int, ProductWithMinMaxPrices> {
        return MappedPagingSource(
            dataPagingSource = localProductDataSource.getProductsWithMinMaxPricesPaging(
                categoryId,
                currency
            ),
            mapper = {
                ProductWithMinMaxPrices(
                    it.productRoomEntity.toDomain(),
                    it.minPrice,
                    it.maxPrice,
                    it.lastUpdatedTimestamp
                )
            }
        )
    }
}
