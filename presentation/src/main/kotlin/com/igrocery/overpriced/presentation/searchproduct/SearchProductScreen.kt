package com.igrocery.overpriced.presentation.searchproduct

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.productpricehistory.dtos.ProductWithMinMaxPrices
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantity
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantityUnit
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.productlist.ProductListItem
import com.igrocery.overpriced.presentation.shared.BackButton
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.presentation.shared.UseAnimatedFadeTopBarColorForStatusBarColor
import com.igrocery.overpriced.presentation.shared.UseDefaultSystemNavBarColor
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.*
import java.util.*

@Suppress("unused")
private val log = Logger { }

@Composable
fun SearchProductScreen(
    viewModel: SearchProductScreenViewModel,
    navigateUp: () -> Unit,
    navigateToProductDetails: (ProductId) -> Unit,
) {
    log.debug("Composing SearchProductScreen")

    val state by rememberSearchProductScreenState()
    val productPagingItems =
        viewModel.productsWithMinMaxPricesPagingDataFlow.collectAsLazyPagingItems()
    MainContent(
        viewModelState = viewModel,
        state = state,
        productPagingItems = productPagingItems,
        onBackButtonClick = navigateUp,
        onFirstFocusRequest = {
            state.isRequestingFirstFocus = false
        },
        onQueryChanged = {
            viewModel.updateQuery(it.take(100))
            productPagingItems.refresh()
        },
        onProductClick = navigateToProductDetails,
    )

    BackHandler {
        log.debug("SearchProductScreen: BackHandler")
        navigateUp()
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
private fun MainContent(
    viewModelState: SearchProductScreenViewModelState,
    state: SearchProductScreenStateHolder,
    productPagingItems: LazyPagingItems<ProductWithMinMaxPrices>,
    onBackButtonClick: () -> Unit,
    onFirstFocusRequest: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onProductClick: (ProductId) -> Unit,
) {
    val topBarScrollState = rememberTopAppBarState()
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topBarScrollState)

    UseAnimatedFadeTopBarColorForStatusBarColor(topBarScrollState)
    UseDefaultSystemNavBarColor()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val focusRequester = remember { FocusRequester() }
                    val keyboardController = LocalSoftwareKeyboardController.current

                    val query by viewModelState.queryFlow.collectAsState()
                    TextField(
                        value = query,
                        onValueChange = onQueryChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.search_product_search_bar_hint),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                ClearButton(
                                    onClick = {
                                        onQueryChanged("")
                                    },
                                    modifier = Modifier
                                        .padding(14.dp)
                                        .size(24.dp, 24.dp)
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                keyboardController?.hide()
                            }
                        ),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        )
                    )
                    if (state.isRequestingFirstFocus) {
                        onFirstFocusRequest()
                        LaunchedEffect(key1 = Unit) {
                            focusRequester.requestFocus()
                        }
                    }
                },
                navigationIcon = {
                    BackButton(
                        onClick = onBackButtonClick,
                        modifier = Modifier
                            .padding(14.dp)
                            .size(24.dp, 24.dp)
                    )
                },
                scrollBehavior = topBarScrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) {
        if (productPagingItems.itemCount == 0) {
            EmptyListContent(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            )
        } else {
            val currency by viewModelState.currencyFlow.collectAsState()
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
            ) {
                items(
                    items = productPagingItems,
                    key = { item -> item.product.id }
                ) { item ->
                    if (item != null) {
                        ProductListItem(
                            item = item,
                            currency = currency,
                            onClick = onProductClick,
                            modifier = Modifier
                                .animateItemPlacement()
                                .fillMaxWidth()
                                .height(60.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClearButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_close_24),
            contentDescription = stringResource(R.string.search_product_clear_button_content_description),
        )
    }
}

@Composable
private fun EmptyListContent(
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.search_product_no_result_text),
            modifier = Modifier.wrapContentSize(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview
@Composable
private fun EmptyPreview() {
    val viewModelState = object : SearchProductScreenViewModelState {
        override val currencyFlow: StateFlow<LoadingState<Currency>> =
            MutableStateFlow(LoadingState.Success(Currency.getInstance(Locale.US)))
        override val queryFlow: StateFlow<String> = MutableStateFlow("apple")
        override val productsWithMinMaxPricesPagingDataFlow: Flow<PagingData<ProductWithMinMaxPrices>> =
            emptyFlow()
    }

    val state by rememberSearchProductScreenState()
    val productPagingItems =
        viewModelState.productsWithMinMaxPricesPagingDataFlow.collectAsLazyPagingItems()
    MainContent(
        viewModelState = viewModelState,
        state = state,
        productPagingItems = productPagingItems,
        onBackButtonClick = {},
        onFirstFocusRequest = {},
        onQueryChanged = {},
    ) {}
}

@Preview
@Composable
private fun DefaultPreview() {
    val viewModelState = object : SearchProductScreenViewModelState {
        override val currencyFlow: StateFlow<LoadingState<Currency>> =
            MutableStateFlow(LoadingState.Success(Currency.getInstance(Locale.US)))
        override val queryFlow: StateFlow<String> = MutableStateFlow("apple")
        override val productsWithMinMaxPricesPagingDataFlow: Flow<PagingData<ProductWithMinMaxPrices>> =
            flowOf(
                PagingData.from(
                    listOf(
                        ProductWithMinMaxPrices(
                            product = Product(
                                name = "Apple",
                                quantity = ProductQuantity(1.0, ProductQuantityUnit.Baskets),
                                categoryId = null
                            ),
                            minPrice = 5.0,
                            maxPrice = 8.0,
                            lastUpdatedTimestamp = 1668651806992000L
                        )
                    )
                )
            )
    }
    val state by rememberSearchProductScreenState()
    val productPagingItems =
        viewModelState.productsWithMinMaxPricesPagingDataFlow.collectAsLazyPagingItems()
    MainContent(
        viewModelState = viewModelState,
        state = state,
        productPagingItems = productPagingItems,
        onBackButtonClick = {},
        onFirstFocusRequest = {},
        onQueryChanged = {},
    ) {}
}
