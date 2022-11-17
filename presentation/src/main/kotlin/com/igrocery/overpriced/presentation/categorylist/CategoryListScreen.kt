package com.igrocery.overpriced.presentation.categorylist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.igrocery.overpriced.domain.productpricehistory.dtos.CategoryWithProductCount
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.shared.*
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.MutableStateFlow

@Suppress("unused")
private val log = Logger { }

@Composable
fun CategoryListScreen(
    categoryListScreenViewModel: CategoryListScreenViewModel,
    navigateToSettings: () -> Unit,
    navigateToSearchProduct: () -> Unit,
    navigateToProductList: (Category?) -> Unit,
    navigateToNewPrice: () -> Unit,
    navigateToShoppingList: () -> Unit,
    modifier: Modifier = Modifier,
) {
    log.debug("Composing CategoryListScreen")

    val state by rememberCategoryListScreenState()
    MainContent(
        viewModelState = categoryListScreenViewModel,
        state = state,
        onSettingsClick = navigateToSettings,
        onNewPriceFabClick = navigateToNewPrice,
        onSearchBarClick = navigateToSearchProduct,
        onCategoryClick = navigateToProductList,
        onNavBarShoppingListClick = navigateToShoppingList,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun MainContent(
    viewModelState: CategoryListScreenViewModelState,
    state: CategoryListScreenStateHolder,
    onNewPriceFabClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSearchBarClick: () -> Unit,
    onCategoryClick: (Category?) -> Unit,
    onNavBarShoppingListClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val topBarState = rememberTopAppBarState()
    val topBarScrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state = topBarState)

    UseDefaultStatusBarColor()
    UseDefaultBottomNavBarColourForSystemNavBarColor()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    // TODO replace with brand icon
                    Text(text = stringResource(id = R.string.app_name))
                },
                actions = {
                    SettingsButton(
                        onClick = onSettingsClick,
                        modifier = Modifier
                            .padding(14.dp)
                            .size(24.dp, 24.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
                scrollBehavior = topBarScrollBehavior,
                windowInsets = WindowInsets.statusBars
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(text = stringResource(id = R.string.category_product_new_price_fab_text))
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_add_24),
                        contentDescription = stringResource(
                            id = R.string.category_product_new_price_fab_content_description
                        ),
                        modifier = Modifier.size(24.dp)
                    )
                },
                onClick = onNewPriceFabClick,
                // somehow the nav bar padding doesn't get applied in landscape
                modifier = Modifier.padding(
                    WindowInsets.navigationBars.only(WindowInsetsSides.End).asPaddingValues()
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_attach_money_24),
                            contentDescription = stringResource(id = R.string.category_product_bottom_nav_content_description),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(text = stringResource(id = R.string.category_product_bottom_nav_label)) },
                    selected = true,
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_shopping_cart_24),
                            contentDescription = stringResource(id = R.string.shopping_lists_bottom_nav_content_description),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(text = stringResource(id = R.string.shopping_lists_bottom_nav_label)) },
                    selected = false,
                    onClick = onNavBarShoppingListClick
                )
            }
        },
        modifier = modifier,
    ) { scaffoldPadding ->
        val categoryWithCountList by viewModelState.categoryWithProductCountFlow.collectAsState()
        categoryWithCountList.ifLoaded {
            if (it.isEmpty()) {
                EmptyListContent(
                    modifier = Modifier
                        .padding(scaffoldPadding)
                        .fillMaxSize()
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 120.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .padding(scaffoldPadding)
                        .fillMaxSize()
                        .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
                ) {
                    stickyHeader {
                        SearchBar(
                            onClick = onSearchBarClick,
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .fillMaxWidth()
                                .height(40.dp)
                        )
                    }

                    items(
                        items = it.filter { categoryWithCount -> categoryWithCount.productCount > 0 },
                        key = { categoryWithCount -> categoryWithCount.category?.id ?: 0 }
                    ) { categoryWithCount ->
                        CategoryWithCountListItem(
                            categoryWithCount = categoryWithCount,
                            onClick = onCategoryClick,
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

@Composable
private fun EmptyListContent(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(scrollState)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_price_tag),
            contentDescription = stringResource(id = R.string.category_list_empty_list_image_content_description),
            modifier = Modifier
                .size(200.dp, 200.dp)
                .padding(bottom = 36.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )

        Text(
            text = stringResource(id = R.string.category_list_empty_list_text),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .padding(bottom = 130.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = { onClick() },
        shape = RoundedCornerShape(percent = 100),
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_search_24),
                contentDescription = stringResource(id = R.string.category_list_search_bar_icon_content_description),
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(24.dp)
            )

            Text(
                text = stringResource(id = R.string.category_list_search_bar_hint),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.alpha(0.6f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun CategoryWithCountListItem(
    categoryWithCount: CategoryWithProductCount,
    onClick: (Category?) -> Unit,
    modifier: Modifier = Modifier
) {
    val category = categoryWithCount.category ?: NoCategory
    val productCount = categoryWithCount.productCount

    Card(
        onClick = { onClick(category) },
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = category.icon.iconRes),
                contentDescription = stringResource(id = R.string.category_list_category_item_icon_content_description),
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(35.dp)
            )

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = pluralStringResource(
                        id = R.plurals.category_list_category_item_count_text,
                        count = productCount,
                        productCount
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.alpha(0.6f)
                )
            }
        }
    }
}

@Composable
private fun SettingsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_settings_24),
            contentDescription = stringResource(R.string.settings_button_content_description)
        )
    }
}

@Preview
@Composable
private fun EmptyPreview() {
    val viewModelState = object : CategoryListScreenViewModelState {
        override val categoryWithProductCountFlow =
            MutableStateFlow(LoadingState.Success(emptyList<CategoryWithProductCount>()))
    }

    MainContent(
        viewModelState = viewModelState,
        state = CategoryListScreenStateHolder(),
        onSettingsClick = {},
        onSearchBarClick = {},
        onCategoryClick = {},
        onNavBarShoppingListClick = {}
    )
}

@Preview
@Composable
private fun DefaultPreview() {
    val categoryWithCountList = listOf(
        CategoryWithProductCount(
            category = NoCategory,
            productCount = 25
        ),
        CategoryWithProductCount(
            category = Category(id = 1, icon = CategoryIcon.Apple, name = "Fruits"),
            productCount = 10
        ),
        CategoryWithProductCount(
            category = Category(id = 2, icon = CategoryIcon.Carrot, name = "Vegetables"),
            productCount = 500
        ),
        CategoryWithProductCount(
            category = Category(id = 3, icon = CategoryIcon.Beer, name = "Beverages"),
            productCount = 7
        ),
        CategoryWithProductCount(
            category = Category(id = 4, icon = CategoryIcon.Cheese, name = "Dairy"),
            productCount = 23
        ),
    )
    val viewModelState = object : CategoryListScreenViewModelState {
        override val categoryWithProductCountFlow =
            MutableStateFlow(LoadingState.Success(categoryWithCountList))
    }

    MainContent(
        viewModelState = viewModelState,
        state = CategoryListScreenStateHolder(),
        onSettingsClick = {},
        onSearchBarClick = {},
        onCategoryClick = {},
        onNavBarShoppingListClick = {}
    )
}
