package com.igrocery.overpriced.presentation.categorylist

import androidx.activity.compose.BackHandler
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon
import com.igrocery.overpriced.presentation.categorylist.CategoryListScreenViewModel.CategoryWithProductCount
import com.igrocery.overpriced.shared.Logger
import com.ireceipt.receiptscanner.presentation.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@Suppress("unused")
private val log = Logger { }

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun CategoryListScreen(
    categoryListScreenViewModel: CategoryListScreenViewModel,
    navigateUp: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToSearchProduct: () -> Unit,
    navigateToCategoryDetail: (Category) -> Unit,
) {
    log.debug("Composing ProductPriceListScreen")

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

    val noCategoryString = stringResource(id = R.string.no_category)
    val categoryWithCountList by categoryListScreenViewModel.categoryWithProductCount.collectAsState()
    val state by rememberCategoryListScreenState()
    MainContent(
        categoryWithCountList = emptyList(),
        state = state,
        onSettingsClick = navigateToSettings,
        onSearchBarClick = navigateToSearchProduct,
        onCategoryClick = navigateToCategoryDetail,
    )

    BackHandler(enabled = false) {
        navigateUp()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun MainContent(
    categoryWithCountList: List<CategoryWithProductCount>?,
    state: CategoryListScreenStateHolder,
    onSettingsClick: () -> Unit,
    onSearchBarClick: () -> Unit,
    onCategoryClick: (Category) -> Unit,
) {
    val topBarState = rememberTopAppBarState()
    val topBarScrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            decayAnimationSpec = rememberSplineBasedDecay(),
            state = topBarState,
        )
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
                modifier = Modifier.statusBarsPadding()
            )
        },
    ) {
        if (categoryWithCountList == null) {
            // Loading state - show nothing
        } else if (categoryWithCountList.isEmpty()) {
            EmptyListContent(
                modifier = Modifier
                    .padding(it)
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
                    .padding(it)
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
                    items = categoryWithCountList
                        .filter { categoryWithCount -> categoryWithCount.productCount > 0 },
                    key = { categoryWithCount -> categoryWithCount.category.id }
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
    onClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val (category, productCount) = categoryWithCount

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
    MainContent(
        categoryWithCountList = emptyList(),
        state = CategoryListScreenStateHolder(),
        onSettingsClick = {},
        onSearchBarClick = {},
        onCategoryClick = {},
    )
}

@Preview
@Composable
private fun DefaultPreview() {
    val categoryWithCountList = listOf(
        CategoryWithProductCount(
            category = Category(
                id = 0,
                icon = CategoryIcon.NoCategory,
                name = stringResource(id = R.string.no_category)
            ),
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

    MainContent(
        categoryWithCountList = categoryWithCountList,
        state = CategoryListScreenStateHolder(),
        onSettingsClick = {},
        onSearchBarClick = {},
        onCategoryClick = {},
    )
}
