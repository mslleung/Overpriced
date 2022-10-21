package com.igrocery.overpriced.presentation.categorybase

import androidx.compose.animation.*
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.categorylist.CategoryListScreen
import com.igrocery.overpriced.presentation.categorylist.CategoryListScreenViewModel
import com.igrocery.overpriced.presentation.categorybase.NavDestinations.CategoryList
import com.igrocery.overpriced.presentation.categorybase.NavDestinations.ProductList_Arg_CategoryId
import com.igrocery.overpriced.presentation.categorybase.NavDestinations.ProductList_With_Args
import com.igrocery.overpriced.presentation.productlist.ProductListScreen
import com.igrocery.overpriced.presentation.productlist.ProductListScreenViewModel
import com.igrocery.overpriced.shared.Logger

@Suppress("unused")
private val log = Logger { }

@Composable
fun CategoryBaseScreen(
    navigateToSettings: () -> Unit,
    navigateToSearchProduct: () -> Unit,
    navigateToEditCategory: (categoryId: Long) -> Unit,
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

object NavDestinations {

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
    navigateToEditCategory: (categoryId: Long) -> Unit,
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
                    onClick = {
                        val currentBackStackEntry = navController.currentBackStackEntry
                        currentBackStackEntry?.destination?.route.let {
                            if (it == ProductList_With_Args) {
                                navController.navigateUp()
                            }
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
        contentWindowInsets = WindowInsets.navigationBars,
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
    navigateToEditCategory: (categoryId: Long) -> Unit,
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
                navigateToSettings = navigateToSettings,
                navigateToSearchProduct = navigateToSearchProduct,
                navigateToProductList = {
                    if (it != null) {
                        navController.navigate("${NavDestinations.ProductList}?$ProductList_Arg_CategoryId=${it.id}")
                    } else {
                        navController.navigate(NavDestinations.ProductList)
                    }
                },
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
                viewModel = productListScreenViewModel,
                navigateUp = { navController.navigateUp() },
                navigateToSearchProduct = navigateToSearchProduct,
                navigateToEditCategory = {
                    navigateToEditCategory(categoryId)
                },
            )
        }
    }
}

@Preview
@Composable
private fun EmptyPreview() {
    MainContent(
        navigateToSettings = {},
        navigateToSearchProduct = {},
        navigateToEditCategory = {},
        onFabClick = {},
        onBottomBarShoppingListClick = {}
    )
}
