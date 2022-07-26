package com.igrocery.overpriced.presentation.categorylist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.presentation.categorylist.CategoryListScreenViewModel.CategoryWithProductCount
import com.igrocery.overpriced.shared.Logger
import com.ireceipt.receiptscanner.presentation.R

@Suppress("unused")
private val log = Logger { }

@Composable
fun CategoryListScreen(
    categoryListScreenViewModel: CategoryListScreenViewModel,
    navigateUp: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToAddPrice: () -> Unit,
    navigateToPlanner: () -> Unit
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

    val categoryWithCountList by
        categoryListScreenViewModel.categoryListWithCountFlow.collectAsState()
    val state by rememberCategoryListScreenState()
    MainContent(
        categoryWithCountList = categoryWithCountList,
        state = state,
        onCategoryClick = { },
        onSettingsClick = navigateToSettings,
        onFabClick = navigateToAddPrice,
        onNavBarPlannerClick = navigateToPlanner,
    )

    BackHandler(enabled = false) {
        navigateUp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    categoryWithCountList: List<CategoryWithProductCount>,
    state: CategoryListScreenStateHolder,
    onCategoryClick: (Category) -> Unit,
    onSettingsClick: () -> Unit,
    onFabClick: () -> Unit,
    onNavBarPlannerClick: () -> Unit
) {
    val topBarScrollState = rememberTopAppBarScrollState()
    val topBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(state = topBarScrollState)
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
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(text = stringResource(id = R.string.category_list_new_price_fab_text))
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_add_24),
                        contentDescription = stringResource(
                            id = R.string.category_list_new_price_fab_content_description
                        ),
                        modifier = Modifier.size(24.dp)
                    )
                },
                onClick = onFabClick,
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding(),
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_attach_money_24),
                            contentDescription = stringResource(id = R.string.category_list_bottom_nav_content_description),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(text = stringResource(id = R.string.category_list_bottom_nav_label)) },
                    selected = true,
                    onClick = { }
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
                    onClick = onNavBarPlannerClick
                )
            }
        }
    ) {
        if (categoryWithCountList.isEmpty()) {
            EmptyListContent(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .padding(it)
                    .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
            ) {
//                stickyHeader {
//
//                }

                items(
                    items = categoryWithCountList,
                    key = { categoryWithCount -> categoryWithCount.category.id }
                ) { categoryWithCount ->
                    CategoryWithCountListItem(
                        categoryWithCount = categoryWithCount,
                        onClick = onCategoryClick,
                        modifier = Modifier
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
private fun CategoryWithCountListItem(
    categoryWithCount: CategoryWithProductCount,
    onClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val (category, productCount) = categoryWithCount

    ElevatedCard(
        onClick = { onClick(category) },
        modifier = modifier
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = category.icon.iconRes),
                contentDescription = stringResource(id = R.string.category_list_category_item_icon_content_description)
            )

            Text(
                text = category.name,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

//        Column(
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.End,
//            modifier = Modifier
//                .fillMaxHeight()
//                .fillMaxWidth()
//        ) {
//            // prices
//            Text(
//                text = product.name,
//                style = MaterialTheme.typography.titleLarge,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//            )
//        }
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
private fun DefaultPreview() {
    MainContent(
        categoryWithCountList = emptyList(),
        state = CategoryListScreenStateHolder(),
        onCategoryClick = {},
        onSettingsClick = {},
        onFabClick = {},
        onNavBarPlannerClick = {}
    )
}
