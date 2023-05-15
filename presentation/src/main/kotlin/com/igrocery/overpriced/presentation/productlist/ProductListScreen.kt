package com.igrocery.overpriced.presentation.productlist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.productpricehistory.dtos.ProductWithMinMaxPrices
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantity
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantityUnit
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.shared.*
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import java.util.*

@Suppress("unused")
private val log = Logger { }

@Composable
fun ProductListScreen(
    viewModel: ProductListScreenViewModel,
    navigateUp: () -> Unit,
    navigateToSearchProduct: () -> Unit,
    navigateToEditCategory: () -> Unit,
    navigateToProductDetail: (ProductId) -> Unit,
    modifier: Modifier = Modifier,
) {
    log.debug("Composing ProductListScreen")

    val state by rememberProductListScreenState()
    MainContent(
        viewModelState = viewModel,
        state = state,
        onBackButtonClick = navigateUp,
        onSearchButtonClick = navigateToSearchProduct,
        onEditButtonClick = navigateToEditCategory,
        onProductClick = { navigateToProductDetail(it) },
        modifier = modifier
    )

    BackHandler {
        log.debug("ProductListScreen: BackHandler")
        navigateUp()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun MainContent(
    viewModelState: ProductListScreenViewModelState,
    state: ProductListScreenStateHolder,
    onBackButtonClick: () -> Unit,
    onSearchButtonClick: () -> Unit,
    onEditButtonClick: () -> Unit,
    onProductClick: (ProductId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val topBarState = rememberTopAppBarState()
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topBarState)

    UseAnimatedFadeTopBarColorForStatusBarColor(topBarState)
    UseDefaultSystemNavBarColor()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val category by viewModelState.categoryFlow.collectAsState()
                    category.ifLoaded {
                        val displayCategory = it ?: NoCategory
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = displayCategory.icon.iconRes),
                                contentDescription = displayCategory.name,
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .size(30.dp)
                                    .alpha(LocalContentColor.current.alpha),
                            )

                            Text(text = displayCategory.name)
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onSearchButtonClick,
                        modifier = Modifier
                            .size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_search_24),
                            contentDescription = stringResource(id = R.string.product_list_search_button_content_description)
                        )
                    }

                    val category by viewModelState.categoryFlow.collectAsState()
                    category.ifLoaded {
                        if (it != null) {
                            IconButton(
                                onClick = onEditButtonClick,
                                modifier = Modifier
                                    .size(48.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_edit_24),
                                    contentDescription = stringResource(id = R.string.product_list_edit_button_content_description)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    BackButton(
                        onClick = onBackButtonClick,
                        modifier = Modifier
                            .size(48.dp)
                    )
                },
                scrollBehavior = topBarScrollBehavior,
                windowInsets = WindowInsets.statusBars
            )
        },
        modifier = modifier
    ) {
        val productsPagingItems =
            viewModelState.productsWithMinMaxPricesPagingDataFlow.collectAsLazyPagingItems()
        val currency by viewModelState.currencyFlow.collectAsState()
        if (productsPagingItems.isInitialLoadCompleted()) {
            if (productsPagingItems.itemCount == 0) {
                val scrollState = rememberScrollState()
                EmptyListContent(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                        .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
                        .verticalScroll(scrollState)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 120.dp),
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                        .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
                ) {
                    items(
                        count = productsPagingItems.itemCount,
                        key = productsPagingItems.itemKey(key = { item -> item.product.id }),
                        contentType = productsPagingItems.itemContentType()
                    ) { index ->
                        val item = productsPagingItems[index]
                        if (item != null) {
                            ProductListItem(
                                item = item,
                                currency = currency,
                                onClick = onProductClick,
                                modifier = Modifier
                                    .animateItemPlacement()
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyListContent(
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        Text(
            text = stringResource(id = R.string.product_list_empty_text),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ProductListItem(
    item: ProductWithMinMaxPrices,
    currency: LoadingState<Currency>,
    onClick: (ProductId) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onClick(item.product.id) }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(40.dp)
    ) {
        val (product, minPrice, maxPrice, _) = item

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text(
                text = product.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = product.quantity.getDisplayString(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alpha(0.6f)
            )
        }

        if (currency is LoadingState.Success) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End,
            ) {
                val currencySymbol = currency.data.symbol
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
        }
    }
}

@Preview
@Composable
private fun EmptyPreview() {
    val viewModelState = object : ProductListScreenViewModelState {
        override val categoryFlow: StateFlow<LoadingState<Category?>>
            get() = MutableStateFlow(LoadingState.Success(null))
        override val currencyFlow: StateFlow<LoadingState<Currency>>
            get() = MutableStateFlow(LoadingState.Success(Currency.getInstance(Locale.US)))
        override val productsWithMinMaxPricesPagingDataFlow: Flow<PagingData<ProductWithMinMaxPrices>>
            get() = flowOf(PagingData.from(emptyList()))
    }

    MainContent(
        viewModelState = viewModelState,
        state = ProductListScreenStateHolder(),
        onBackButtonClick = {},
        onSearchButtonClick = {},
        onEditButtonClick = {},
        onProductClick = {},
    )
}

@Preview
@Composable
private fun DefaultPreview() {
    val viewModelState = object : ProductListScreenViewModelState {
        override val categoryFlow: StateFlow<LoadingState<Category?>>
            get() = MutableStateFlow(LoadingState.Success(null))
        override val currencyFlow: StateFlow<LoadingState<Currency>>
            get() = MutableStateFlow(LoadingState.Success(Currency.getInstance(Locale.US)))
        override val productsWithMinMaxPricesPagingDataFlow: Flow<PagingData<ProductWithMinMaxPrices>>
            get() = flowOf(
                PagingData.from(
                    listOf(
                        ProductWithMinMaxPrices(
                            product = Product(
                                name = "Apple",
                                quantity = ProductQuantity(1.0, ProductQuantityUnit.Baskets),
                                categoryId = null
                            ),
                            minPrice = null,
                            maxPrice = null,
                            lastUpdatedTimestamp = null
                        )
                    )
                )
            )
    }

    MainContent(
        viewModelState = viewModelState,
        state = ProductListScreenStateHolder(),
        onBackButtonClick = {},
        onSearchButtonClick = {},
        onEditButtonClick = {},
        onProductClick = {},
    )
}
