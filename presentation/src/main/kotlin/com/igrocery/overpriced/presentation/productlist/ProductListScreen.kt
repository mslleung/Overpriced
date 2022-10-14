package com.igrocery.overpriced.presentation.productlist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
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
import androidx.paging.compose.items
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.shared.*
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

@Suppress("unused")
private val log = Logger { }

@Composable
fun ProductListScreen(
    viewModel: ProductListScreenViewModel,
    navigateUp: () -> Unit,
    navigateToSearchProduct: () -> Unit,
    navigateToEditCategory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    log.debug("Composing ProductListScreen")

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

    val state by rememberProductListScreenState()
    MainContent(
        viewModelState = viewModel,
        state = state,
        onBackButtonClick = navigateUp,
        onSearchButtonClick = navigateToSearchProduct,
        onEditButtonClick = navigateToEditCategory,
        onProductClick = {},
        modifier = modifier
    )

    BackHandler(enabled = false) {
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
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier,
) {
    val topBarState = rememberTopAppBarState()
    val topBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(state = topBarState)
    Scaffold(
        topBar = {
            LargeTopAppBar(
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
                            contentDescription = stringResource(id = R.string.category_detail_search_button_content_description)
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
                                    contentDescription = stringResource(id = R.string.category_detail_edit_button_content_description)
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
                modifier = Modifier.statusBarsPadding()
            )
        },
        modifier = modifier
    ) {
        val productsPagingItems = viewModelState.productsPagingDataFlow.collectAsLazyPagingItems()
        if (productsPagingItems.isInitialLoadCompleted()) {
            if (productsPagingItems.itemCount == 0) {
                val scrollState = rememberScrollState()
                EmptyListContent(
                    modifier = Modifier
                        .padding(it)
                        .navigationBarsPadding()
                        .fillMaxSize()
                        .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
                        .verticalScroll(scrollState)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 120.dp),
                    modifier = Modifier
                        .padding(it)
                        .navigationBarsPadding()
                        .fillMaxSize()
                        .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
                ) {
                    items(
                        items = productsPagingItems,
                        key = { product -> product.id }
                    ) { product ->
                        if (product != null) {
                            CategoryWithCountListItem(
                                product = product,
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
            text = stringResource(id = R.string.category_detail_empty_text),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CategoryWithCountListItem(
    product: Product,
    onClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onClick(product) }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(40.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = product.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )

            if (product.description.isNotBlank()) {
                Text(
                    text = product.description,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.alpha(0.6f)
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.wrapContentWidth()
        ) {
//            Text(
//                text = product.,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//                style = MaterialTheme.typography.bodyMedium,
//                modifier = Modifier.alpha(0.6f)
//            )
        }
    }
}

@Preview
@Composable
private fun EmptyPreview() {
    val viewModelState = object : ProductListScreenViewModelState {
        override val categoryFlow: StateFlow<LoadingState<Category?>>
            get() = MutableStateFlow(LoadingState.Success(null))
        override val productsPagingDataFlow: Flow<PagingData<Product>>
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
        override val productsPagingDataFlow: Flow<PagingData<Product>>
            get() = flowOf(
                PagingData.from(
                    listOf(
                        Product(name = "Apple", description = "Fuji", categoryId = null)
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
