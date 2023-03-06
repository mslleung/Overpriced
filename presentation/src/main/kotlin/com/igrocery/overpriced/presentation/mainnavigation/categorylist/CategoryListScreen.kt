package com.igrocery.overpriced.presentation.mainnavigation.categorylist

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.productpricehistory.dtos.CategoryWithProductCount
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.shared.*
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.MutableStateFlow

@Suppress("unused")
private val log = Logger { }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    topBarScrollBehavior: TopAppBarScrollBehavior,
    categoryListScreenViewModel: CategoryListScreenViewModel,
    navigateToSearchProduct: () -> Unit,
    navigateToProductList: (CategoryId?) -> Unit,
    modifier: Modifier = Modifier,
) {
    log.debug("Composing CategoryListScreen")

    val state by rememberCategoryListScreenState()
    MainContent(
        topBarScrollBehavior = topBarScrollBehavior,
        viewModelState = categoryListScreenViewModel,
        state = state,
        onSearchBarClick = navigateToSearchProduct,
        onCategoryClick = navigateToProductList,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun MainContent(
    topBarScrollBehavior: TopAppBarScrollBehavior,
    viewModelState: CategoryListScreenViewModelState,
    state: CategoryListScreenStateHolder,
    onSearchBarClick: () -> Unit,
    onCategoryClick: (CategoryId?) -> Unit,
    modifier: Modifier = Modifier,
) {
    UseDefaultStatusBarColor()
    UseDefaultBottomNavBarColourForSystemNavBarColor()

    Scaffold(
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
                        categoryWithCount.category?.let { category ->
                            CategoryWithCountListItem(
                                category = category,
                                productCount = categoryWithCount.productCount,
                                onClick = { onCategoryClick(category.id) },
                                modifier = Modifier
                                    .animateItemPlacement()
                                    .fillMaxWidth()
                            )
                        } ?: CategoryWithCountListItem(
                            category = NoCategory,
                            productCount = categoryWithCount.productCount,
                            onClick = { onCategoryClick(null) },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryWithCountListItem(
    category: Category,
    productCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun EmptyPreview() {
    val topBarState = rememberTopAppBarState()
    val topBarScrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state = topBarState)
    val viewModelState = object : CategoryListScreenViewModelState {
        override val categoryWithProductCountFlow =
            MutableStateFlow(LoadingState.Success(emptyList<CategoryWithProductCount>()))
    }

    MainContent(
        topBarScrollBehavior = topBarScrollBehavior,
        viewModelState = viewModelState,
        state = CategoryListScreenStateHolder(),
        onSearchBarClick = {},
        onCategoryClick = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun DefaultPreview() {
    val topBarState = rememberTopAppBarState()
    val topBarScrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state = topBarState)
    val categoryWithCountList = listOf(
        CategoryWithProductCount(
            category = NoCategory,
            productCount = 25
        ),
        CategoryWithProductCount(
            category = Category(id = CategoryId(1), icon = CategoryIcon.Apple, name = "Fruits"),
            productCount = 10
        ),
        CategoryWithProductCount(
            category = Category(
                id = CategoryId(2),
                icon = CategoryIcon.Carrot,
                name = "Vegetables"
            ),
            productCount = 500
        ),
        CategoryWithProductCount(
            category = Category(id = CategoryId(3), icon = CategoryIcon.Beer, name = "Beverages"),
            productCount = 7
        ),
        CategoryWithProductCount(
            category = Category(id = CategoryId(4), icon = CategoryIcon.Cheese, name = "Dairy"),
            productCount = 23
        ),
    )
    val viewModelState = object : CategoryListScreenViewModelState {
        override val categoryWithProductCountFlow =
            MutableStateFlow(LoadingState.Success(categoryWithCountList))
    }

    MainContent(
        topBarScrollBehavior = topBarScrollBehavior,
        viewModelState = viewModelState,
        state = CategoryListScreenStateHolder(),
        onSearchBarClick = {},
        onCategoryClick = {},
    )
}
