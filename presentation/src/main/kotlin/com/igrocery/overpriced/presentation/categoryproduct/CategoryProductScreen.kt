package com.igrocery.overpriced.presentation.categoryproduct

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.igrocery.overpriced.domain.productpricehistory.dtos.CategoryWithProductCount
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.shared.Logger

@Suppress("unused")
private val log = Logger { }

@Composable
fun CategoryProductScreen(
    navigateUp: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToSearchProduct: () -> Unit,
    navigateToProductList: (Category?) -> Unit,
    navigateToNewPrice: () -> Unit,
    modifier: Modifier = Modifier,
) {
    log.debug("Composing CategoryProductScreen")

    MainContent(
        categoryWithCountList = categoryWithCountList,
        state = state,
        onSettingsClick = navigateToSettings,
        onSearchBarClick = navigateToSearchProduct,
        onCategoryClick = navigateToProductList,
        onFabClick = navigateToNewPrice,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun MainContent(
    categoryWithCountList: List<CategoryWithProductCount>?,
    state: CategoryListScreenStateHolder,
    onSettingsClick: () -> Unit,
    onSearchBarClick: () -> Unit,
    onCategoryClick: (Category?) -> Unit,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
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
                onClick = onFabClick,
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding(),
            ) {
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
                    onClick = { }
                )
            }
        },
        modifier = modifier,
    ) {
        Scaffold(

        ) {

        }
    }
}

//@Preview
//@Composable
//private fun EmptyPreview() {
//    MainContent(
//        categoryWithCountList = emptyList(),
//        state = CategoryListScreenStateHolder(),
//        onSettingsClick = {},
//        onSearchBarClick = {},
//        onCategoryClick = {},
//        onFabClick = {},
//    )
//}
