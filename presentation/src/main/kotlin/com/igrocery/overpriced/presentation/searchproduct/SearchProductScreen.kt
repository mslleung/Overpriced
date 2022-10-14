package com.igrocery.overpriced.presentation.searchproduct

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.shared.BackButton
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.flowOf

@Suppress("unused")
private val log = Logger { }

@Composable
fun SearchProductScreen(
    viewModel: SearchProductScreenViewModel,
    navigateUp: () -> Unit,
    navigateToProductDetails: (Product) -> Unit,
) {
    log.debug("Composing SearchProductScreen")

    val systemUiController = rememberSystemUiController()
    val statusBarColor = MaterialTheme.colorScheme.surface
    val navBarColor = MaterialTheme.colorScheme.surface
    SideEffect {
        systemUiController.setStatusBarColor(
            statusBarColor,
            transformColorForLightContent = { color -> color })
        systemUiController.setNavigationBarColor(
            navBarColor,
            navigationBarContrastEnforced = false,
            transformColorForLightContent = { color -> color })
    }

    val state by rememberSearchProductScreenState()
    val productPagingItems = viewModel.productsPagingDataFlow.collectAsLazyPagingItems()
    MainContent(
        viewModelState = viewModel,
        state = state,
        productsPagingItems = productPagingItems,
        onBackButtonClick = navigateUp,
        onFirstFocusRequest = {
            state.isRequestingFirstFocus = false
        },
        onQueryChanged = {
            state.query = it.take(100)
        },
        onProductClick = navigateToProductDetails,
    )

    LaunchedEffect(key1 = state) {
        snapshotFlow { state.query }
            .collect {
                viewModel.query = it
                productPagingItems.refresh()
            }
    }

    BackHandler(enabled = false) {
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
    productsPagingItems: LazyPagingItems<Product>,
    onBackButtonClick: () -> Unit,
    onFirstFocusRequest: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onProductClick: (Product) -> Unit,
) {
    val topBarScrollState = rememberTopAppBarState()
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topBarScrollState)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val focusRequester = remember { FocusRequester() }
                    val keyboardController = LocalSoftwareKeyboardController.current
                    TextField(
                        value = state.query,
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
                            if (state.query.isNotEmpty()) {
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
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
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
                modifier = Modifier.statusBarsPadding()
            )
        },
    ) {
        if (productsPagingItems.itemCount == 0) {
            EmptyListContent(
                modifier = Modifier
                    .padding(it)
                    .navigationBarsPadding()
                    .imePadding()
                    .fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .navigationBarsPadding()
                    .imePadding()
                    .fillMaxSize()
                    .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
            ) {
                items(
                    items = productsPagingItems,
                    key = { product -> product.id }
                ) { product ->
                    if (product != null) {
                        ProductListItem(
                            product = product,
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

@Composable
private fun ProductListItem(
    product: Product,
    onClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clickable { onClick(product) }
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = product.name,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        if (product.description.isNotBlank()) {
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.alpha(0.6f)
            )
        }
    }
}

@Preview
@Composable
private fun EmptyPreview() {
    val viewModelState = object : SearchProductScreenViewModelState {}
    val emptyPagingItems = flowOf(PagingData.empty<Product>()).collectAsLazyPagingItems()
    MainContent(
        viewModelState = viewModelState,
        state = SearchProductScreenStateHolder(),
        productsPagingItems = emptyPagingItems,
        onBackButtonClick = {},
        onFirstFocusRequest = {},
        onQueryChanged = {},
        onProductClick = {},
    )
}

@Preview
@Composable
private fun DefaultPreview() {
    val viewModelState = object : SearchProductScreenViewModelState {}
    val pagingItems = flowOf(
        PagingData.from(
            listOf(
                Product(name = "Apple", description = "Fuji", categoryId = null)
            )
        )
    ).collectAsLazyPagingItems()

    MainContent(
        viewModelState = viewModelState,
        state = SearchProductScreenStateHolder(),
        productsPagingItems = pagingItems,
        onBackButtonClick = {},
        onFirstFocusRequest = {},
        onQueryChanged = {},
        onProductClick = {},
    )
}
