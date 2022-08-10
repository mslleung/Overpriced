package com.igrocery.overpriced.presentation.categoryproduct

import androidx.compose.animation.*
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.categorylist.CategoryListScreen
import com.igrocery.overpriced.presentation.categorylist.CategoryListScreenViewModel
import com.igrocery.overpriced.presentation.categoryproduct.NavDestinations.CategoryList
import com.igrocery.overpriced.presentation.categoryproduct.NavDestinations.ProductList_Arg_CategoryId
import com.igrocery.overpriced.presentation.categoryproduct.NavDestinations.ProductList_With_Args
import com.igrocery.overpriced.presentation.productlist.ProductListScreen
import com.igrocery.overpriced.presentation.productlist.ProductListScreenViewModel
import com.igrocery.overpriced.shared.Logger

@Suppress("unused")
private val log = Logger { }

@Composable
fun CategoryProductScreen(
    navigateToSettings: () -> Unit,
    navigateToSearchProduct: () -> Unit,
    navigateToEditCategory: (Category) -> Unit,
    navigateToNewPrice: () -> Unit,
    navigateToShoppingList: () -> Unit,
    modifier: Modifier = Modifier,
) {
    log.debug("Composing CategoryProductScreen")

    MainContent(
        navigateToSettings = navigateToSettings,
        navigateToSearchProduct = navigateToSearchProduct,
        navigateToEditCategory = navigateToEditCategory,
        onFabClick = navigateToNewPrice,
        onBottomBarShoppingListClick = navigateToShoppingList,
        modifier = modifier,
    )
}

private object NavDestinations {

    const val CategoryList = "categoryList"

    const val ProductList = "productList"
    const val ProductList_Arg_CategoryId = "categoryId"
    const val ProductList_With_Args =
        "$ProductList?$ProductList_Arg_CategoryId={$ProductList_Arg_CategoryId}"

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
private fun MainContent(
    navigateToSettings: () -> Unit,
    navigateToSearchProduct: () -> Unit,
    navigateToEditCategory: (Category) -> Unit,
    onFabClick: () -> Unit,
    onBottomBarShoppingListClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberAnimatedNavController()

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
                    onClick = {
                        // TODO check the backstack
                        navController.navigate(CategoryList) {
                            launchSingleTop = true
                        }
                    }
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
                    onClick = onBottomBarShoppingListClick
                )
            }
        },
        modifier = modifier,
    ) {
        NestedNavGraph(
            navController = navController,
            navigateToSettings = navigateToSettings,
            navigateToSearchProduct = navigateToSearchProduct,
            navigateToEditCategory = navigateToEditCategory,
            modifier = Modifier.padding(it)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun NestedNavGraph(
    navController: NavHostController,
    navigateToSettings: () -> Unit,
    navigateToSearchProduct: () -> Unit,
    navigateToEditCategory: (Category) -> Unit,
    modifier: Modifier
) {
    val animationSpec: FiniteAnimationSpec<Float> =
        spring(stiffness = Spring.StiffnessMediumLow)
    AnimatedNavHost(
        navController = navController,
        startDestination = CategoryList,
        enterTransition = {
            fadeIn(animationSpec) + scaleIn(
                animationSpec,
                initialScale = 0.9f
            )
        },
        exitTransition = {
            fadeOut(animationSpec) + scaleOut(
                animationSpec,
                targetScale = 1.1f
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec) + scaleIn(
                animationSpec,
                initialScale = 1.1f
            )
        },
        popExitTransition = {
            fadeOut(animationSpec) + scaleOut(
                animationSpec,
                targetScale = 0.9f
            )
        },
        modifier = modifier
    ) {
        composable(CategoryList) {
            val categoryListScreenViewModel = hiltViewModel<CategoryListScreenViewModel>()

            CategoryListScreen(
                categoryListScreenViewModel = categoryListScreenViewModel,
//                navigateUp = { navController.navigateUp() },
                navigateToSettings = navigateToSettings,
                navigateToSearchProduct = navigateToSearchProduct,
                navigateToProductList = {
                    if (it != null) {
                        navController.navigate("${NavDestinations.ProductList}?$ProductList_Arg_CategoryId=${it.id}")
                    } else {
                        navController.navigate(NavDestinations.ProductList)
                    }
                },
//                navigateToNewPrice = { navController.navigate(NavRoutes.NewPriceRecordRoute) }
            )
        }
        composable(
            route = ProductList_With_Args,
            arguments = listOf(navArgument(ProductList_Arg_CategoryId) {
                type = NavType.LongType
                defaultValue = 0L
            })
        ) { backStackEntry ->
            val productListScreenViewModel =
                hiltViewModel<ProductListScreenViewModel>()

            val categoryId = backStackEntry.arguments?.getLong(ProductList_Arg_CategoryId) ?: 0L
            ProductListScreen(
                categoryId = if (categoryId == 0L) null else categoryId,
                viewModel = productListScreenViewModel,
                navigateUp = { navController.navigateUp() },
                navigateToSearchProduct = navigateToSearchProduct,
                navigateToEditCategory = {},
            )
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
