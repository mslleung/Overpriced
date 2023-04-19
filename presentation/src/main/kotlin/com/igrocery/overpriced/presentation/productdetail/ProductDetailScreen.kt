package com.igrocery.overpriced.presentation.productdetail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.domain.productpricehistory.dtos.ProductWithMinMaxPrices
import com.igrocery.overpriced.domain.productpricehistory.dtos.StoreWithMinMaxPrices
import com.igrocery.overpriced.domain.productpricehistory.models.*
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.productlist.*
import com.igrocery.overpriced.presentation.shared.*
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.*

@Suppress("unused")
private val log = Logger { }

@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailScreenViewModel,
    navigateUp: () -> Unit,
    navigateToStorePriceDetail: (StoreId) -> Unit,
    modifier: Modifier = Modifier,
) {
    log.debug("Composing ProductDetailScreen")

    val state by rememberProductDetailScreenState()
    MainContent(
        viewModelState = viewModel,
        state = state,
        onBackButtonClick = navigateUp,
        onStoreClick = { navigateToStorePriceDetail(it.store.id) },
        modifier = modifier
    )

    BackHandler {
        log.debug("ProductDetailScreen: BackHandler")
        navigateUp()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun MainContent(
    viewModelState: ProductDetailScreenViewModelState,
    state: ProductDetailScreenStateHolder,
    onBackButtonClick: () -> Unit,
    onStoreClick: (StoreWithMinMaxPrices) -> Unit,
    modifier: Modifier = Modifier,
) {
    val topBarState = rememberTopAppBarState()
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topBarState)

    UseAnimatedFadeTopBarColorForStatusBarColor(topAppBarState = topBarState)
    UseDefaultSystemNavBarColor()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val productWithPrices by viewModelState.productWithPricesFlow.collectAsState()
                    productWithPrices.ifLoaded {
                        Text(
                            text = it.product.name,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.headlineSmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(end = 8.dp),
                        )
                    }
                },
//                actions = {
//                    IconButton(
//                        onClick = onSearchButtonClick,
//                        modifier = Modifier
//                            .size(48.dp)
//                    ) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_baseline_search_24),
//                            contentDescription = stringResource(id = R.string.product_list_search_button_content_description)
//                        )
//                    }
//
//                    val category by viewModelState.categoryFlow.collectAsState()
//                    category.ifLoaded {
//                        if (it != null) {
//                            IconButton(
//                                onClick = onEditButtonClick,
//                                modifier = Modifier
//                                    .size(48.dp)
//                            ) {
//                                Icon(
//                                    painter = painterResource(id = R.drawable.ic_baseline_edit_24),
//                                    contentDescription = stringResource(id = R.string.product_list_edit_button_content_description)
//                                )
//                            }
//                        }
//                    }
//                },
                navigationIcon = {
                    BackButton(
                        onClick = onBackButtonClick,
                        modifier = Modifier
                            .size(48.dp)
                    )
                },
                scrollBehavior = topBarScrollBehavior,
            )
        },
        modifier = modifier
    ) {
        val currency by viewModelState.currencyFlow.collectAsState()
        val storesWithMinMaxPrices =
            viewModelState.storesWithMinMaxPricesPagingDataFlow.collectAsLazyPagingItems()
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
        ) {
            item(
                contentType = { "product detail" },
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                ) {
                    val currencyLoadState by viewModelState.currencyFlow.collectAsState()
                    val productWithPricesLoadState by viewModelState.productWithPricesFlow.collectAsState()
                    currencyLoadState.ifLoaded { currency ->
                        productWithPricesLoadState.ifLoaded { (product, minPrice, maxPrice) ->
                            Text(
                                text = product.quantity.getDisplayString(),
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .padding(bottom = 8.dp, end = 8.dp),
                            )

                            val priceRangeText = if (minPrice == maxPrice) {
                                "${currency.symbol} $minPrice"
                            } else {
                                "${currency.symbol} $minPrice - $maxPrice"
                            }
                            Text(
                                text = priceRangeText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }
            }

            items(
                items = storesWithMinMaxPrices,
                key = { item -> item.store.id },
            ) { item ->
                if (item != null) {
                    StoreListItem(
                        item = item,
                        currency = currency,
                        onClick = onStoreClick,
                        modifier = Modifier
                            .animateItemPlacement()
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun StoreListItem(
    item: StoreWithMinMaxPrices,
    currency: LoadingState<Currency>,
    onClick: (StoreWithMinMaxPrices) -> Unit,
    modifier: Modifier = Modifier
) {
    val (store, minPrice, maxPrice, lastUpdatedTimeStamp) = item
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onClick(item) }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text(
                text = store.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge
            )

            val addressLines = store.address.lines
            if (addressLines.isNullOrBlank()) {
                Text(
                    text = stringResource(id = R.string.product_detail_store_no_address),
                    maxLines = 1,
                    fontStyle = Italic,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.alpha(0.6f)
                )
            } else {
                Text(
                    text = addressLines,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.alpha(0.6f)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            currency.ifLoaded {
                val currencySymbol = it.symbol
                val priceRangeText = if (minPrice == maxPrice) {
                    "$currencySymbol $minPrice"
                } else {
                    "$currencySymbol $minPrice - $maxPrice"
                }
                Text(
                    text = priceRangeText,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            val timeAgoDuration by produceState(
                initialValue = Clock.System.now()
                    .minus(Instant.fromEpochMilliseconds(lastUpdatedTimeStamp))
            ) {
                while (isActive) {
                    delay(1000)
                    value = Clock.System.now()
                        .minus(Instant.fromEpochMilliseconds(lastUpdatedTimeStamp))
                }
            }

            // note that the numbers are floored due to numerical precision
            val timeAgoText = if (timeAgoDuration.inWholeDays > 0) {
                pluralStringResource(
                    id = R.plurals.product_detail_days_ago,
                    count = timeAgoDuration.inWholeDays.toInt(),
                    timeAgoDuration.inWholeDays
                )
            } else if (timeAgoDuration.inWholeHours > 0) {
                pluralStringResource(
                    id = R.plurals.product_detail_hours_ago,
                    count = timeAgoDuration.inWholeHours.toInt(),
                    timeAgoDuration.inWholeHours
                )
            } else if (timeAgoDuration.inWholeMinutes > 0) {
                pluralStringResource(
                    id = R.plurals.product_detail_minutes_ago,
                    count = timeAgoDuration.inWholeMinutes.toInt(),
                    timeAgoDuration.inWholeMinutes
                )
            } else {
                stringResource(id = R.string.product_detail_moments_ago)
            }

            val updatedLabel = stringResource(id = R.string.product_detail_updated_label)
            Text(
                text = "$updatedLabel $timeAgoText",
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0.6f)
            )
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    val viewModelState = object : ProductDetailScreenViewModelState {
        override val currencyFlow: StateFlow<LoadingState<Currency>>
            get() = MutableStateFlow(LoadingState.Success(Currency.getInstance(Locale.US)))
        override val productWithPricesFlow: StateFlow<LoadingState<ProductWithMinMaxPrices>>
            get() = MutableStateFlow(
                LoadingState.Success(
                    ProductWithMinMaxPrices(
                        product = Product(
                            name = "Apple",
                            quantity = ProductQuantity(1.0, ProductQuantityUnit.Baskets),
                            categoryId = null
                        ),
                        minPrice = 5.0,
                        maxPrice = 6.0,
                        lastUpdatedTimestamp = 0
                    )
                )
            )
        override val storesWithMinMaxPricesPagingDataFlow: Flow<PagingData<StoreWithMinMaxPrices>>
            get() = MutableStateFlow(
                PagingData.from(
                    listOf(
                        StoreWithMinMaxPrices(
                            store = Store(
                                name = "Apple",
                                address = Address("Example street", GeoCoordinates(0.0, 0.0))
                            ),
                            minPrice = 5.0,
                            maxPrice = 6.0,
                            lastUpdatedTimestamp = 1
                        )
                    )
                )
            )
    }

    MainContent(
        viewModelState = viewModelState,
        state = ProductDetailScreenStateHolder(),
        onBackButtonClick = {},
        onStoreClick = {},
    )
}
