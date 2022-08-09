package com.igrocery.overpriced.presentation.productlist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.presentation.shared.BackButton
import com.igrocery.overpriced.shared.Logger
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.shared.NoCategory

@Suppress("unused")
private val log = Logger { }

@Composable
fun ProductListScreen(
    categoryId: Long?,
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

    val products = viewModel.productsPagedFlow.collectAsLazyPagingItems()
    val state by rememberProductListScreenState()
    if (categoryId == null) {
        MainContent(
            category = NoCategory,
            productsPagingItems = products,
            state = state,
            onBackButtonClick = navigateUp,
            onSearchButtonClick = navigateToSearchProduct,
            onEditButtonClick = navigateToEditCategory,
            onProductClick = {},
            onFabClick = {}
        )
    } else {
        LaunchedEffect(key1 = Unit) {
            viewModel.setCategoryId(categoryId)
        }

        val category by viewModel.categoryFlow.collectAsState()
        MainContent(
            category = category,
            productsPagingItems = products,
            state = state,
            onBackButtonClick = navigateUp,
            onSearchButtonClick = navigateToSearchProduct,
            onEditButtonClick = navigateToEditCategory,
            onProductClick = {},
            onFabClick = {}
        )
    }

    BackHandler(enabled = false) {
        navigateUp()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun MainContent(
    category: Category?,
    productsPagingItems: LazyPagingItems<Product>,
    state: ProductListScreenStateHolder,
    onBackButtonClick: () -> Unit,
    onSearchButtonClick: () -> Unit,
    onEditButtonClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onFabClick: () -> Unit,
) {
    val topBarState = rememberTopAppBarState()
    val topBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(state = topBarState)
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    if (category != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = category.icon.iconRes),
                                contentDescription = category.name,
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .size(30.dp)
                                    .alpha(LocalContentColor.current.alpha),
                            )

                            Text(text = category.name)
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFabClick,
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding(),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_add_24),
                    contentDescription = stringResource(
                        id = R.string.category_list_new_price_fab_content_description
                    ),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) {
        if (state.isLazyListPagingFirstLoad && productsPagingItems.loadState.refresh is LoadState.Loading) {
            LaunchedEffect(key1 = productsPagingItems.loadState.refresh) {
                state.isLazyListPagingFirstLoad = false
            }
        }
        val isLoading by remember {
            derivedStateOf {
                state.isLazyListPagingFirstLoad || productsPagingItems.loadState.refresh is LoadState.Loading
            }
        }

        if (isLoading) {
            // loading state - show nothing
        } else {
            if (productsPagingItems.itemCount == 0) {
                EmptyListContent(
                    modifier = Modifier
                        .padding(it)
                        .navigationBarsPadding()
                        .fillMaxSize()
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
    Text(
        text = stringResource(id = R.string.category_detail_empty_text),
        textAlign = TextAlign.Center,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
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
            modifier = Modifier.fillMaxWidth()
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
//    MainContent(
//        categoryWithCountList = emptyList(),
//        state = CategoryDetailScreenStateHolder(),
//        onCategoryClick = {},
//        onSettingsClick = {},
//        onFabClick = {},
//        onNavBarPlannerClick = {}
//    )
}

@Preview
@Composable
private fun DefaultPreview() {
//    val categoryWithCountList = listOf(
//        CategoryWithProductCount(
//            category = Category(id = 0, icon = CategoryIcon.NoCategory, name = "Uncategorized"),
//            productCount = 25
//        ),
//        CategoryWithProductCount(
//            category = Category(id = 1, icon = CategoryIcon.Apple, name = "Fruits"),
//            productCount = 10
//        ),
//        CategoryWithProductCount(
//            category = Category(id = 2, icon = CategoryIcon.Carrot, name = "Vegetables"),
//            productCount = 500
//        ),
//        CategoryWithProductCount(
//            category = Category(id = 3, icon = CategoryIcon.Beer, name = "Beverages"),
//            productCount = 7
//        ),
//        CategoryWithProductCount(
//            category = Category(id = 4, icon = CategoryIcon.Cheese, name = "Dairy"),
//            productCount = 23
//        ),
//    )
//
//    MainContent(
//        categoryWithCountList = categoryWithCountList,
//        state = CategoryDetailScreenStateHolder(),
//        onCategoryClick = {},
//        onSettingsClick = {},
//        onFabClick = {},
//        onNavBarPlannerClick = {}
//    )
}
