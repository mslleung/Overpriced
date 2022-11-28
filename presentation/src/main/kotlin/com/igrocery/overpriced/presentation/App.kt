package com.igrocery.overpriced.presentation

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.animation.*
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.igrocery.overpriced.presentation.NavDestinations.CategoryList
import com.igrocery.overpriced.presentation.NavDestinations.EditCategory
import com.igrocery.overpriced.presentation.NavDestinations.EditCategory_Arg_CategoryId
import com.igrocery.overpriced.presentation.NavDestinations.EditCategory_Result_CategoryId
import com.igrocery.overpriced.presentation.NavDestinations.EditCategory_With_Args
import com.igrocery.overpriced.presentation.NavDestinations.EditStore
import com.igrocery.overpriced.presentation.NavDestinations.EditStore_Arg_StoreId
import com.igrocery.overpriced.presentation.NavDestinations.EditStore_Result_StoreId
import com.igrocery.overpriced.presentation.NavDestinations.EditStore_With_Args
import com.igrocery.overpriced.presentation.NavDestinations.NewCategory
import com.igrocery.overpriced.presentation.NavDestinations.NewCategory_Result_CategoryId
import com.igrocery.overpriced.presentation.NavDestinations.NewPrice
import com.igrocery.overpriced.presentation.NavDestinations.NewPrice_Arg_CategoryId
import com.igrocery.overpriced.presentation.NavDestinations.NewPrice_Arg_ProductId
import com.igrocery.overpriced.presentation.NavDestinations.NewPrice_With_Args
import com.igrocery.overpriced.presentation.NavDestinations.NewStore
import com.igrocery.overpriced.presentation.NavDestinations.NewStore_Result_StoreId
import com.igrocery.overpriced.presentation.NavDestinations.ProductDetail
import com.igrocery.overpriced.presentation.NavDestinations.ProductDetail_Arg_ProductId
import com.igrocery.overpriced.presentation.NavDestinations.ProductDetail_With_Args
import com.igrocery.overpriced.presentation.NavDestinations.ProductList
import com.igrocery.overpriced.presentation.NavDestinations.ProductList_Arg_CategoryId
import com.igrocery.overpriced.presentation.NavDestinations.ProductList_With_Args
import com.igrocery.overpriced.presentation.NavDestinations.SearchProduct
import com.igrocery.overpriced.presentation.NavDestinations.SelectCurrency
import com.igrocery.overpriced.presentation.NavDestinations.Settings
import com.igrocery.overpriced.presentation.NavRoutes.CategoryRoute
import com.igrocery.overpriced.presentation.NavRoutes.SettingsRoute
import com.igrocery.overpriced.presentation.categorylist.CategoryListScreen
import com.igrocery.overpriced.presentation.categorylist.CategoryListScreenViewModel
import com.igrocery.overpriced.presentation.editcategory.EditCategoryScreen
import com.igrocery.overpriced.presentation.editcategory.EditCategoryScreenViewModel
import com.igrocery.overpriced.presentation.editstore.EditStoreScreen
import com.igrocery.overpriced.presentation.editstore.EditStoreScreenViewModel
import com.igrocery.overpriced.presentation.newcategory.NewCategoryScreen
import com.igrocery.overpriced.presentation.newcategory.NewCategoryScreenViewModel
import com.igrocery.overpriced.presentation.newprice.NewPriceScreen
import com.igrocery.overpriced.presentation.newprice.NewPriceScreenViewModel
import com.igrocery.overpriced.presentation.newstore.NewStoreScreen
import com.igrocery.overpriced.presentation.newstore.NewStoreScreenViewModel
import com.igrocery.overpriced.presentation.productlist.ProductListScreen
import com.igrocery.overpriced.presentation.productlist.ProductListScreenViewModel
import com.igrocery.overpriced.presentation.searchproduct.SearchProductScreen
import com.igrocery.overpriced.presentation.searchproduct.SearchProductScreenViewModel
import com.igrocery.overpriced.presentation.selectcurrency.SelectCurrencyScreen
import com.igrocery.overpriced.presentation.selectcurrency.SelectCurrencyScreenViewModel
import com.igrocery.overpriced.presentation.settings.SettingsScreen
import com.igrocery.overpriced.presentation.settings.SettingsScreenViewModel
import com.igrocery.overpriced.presentation.ui.theme.AppTheme
import com.igrocery.overpriced.shared.Logger

@Suppress("unused")
private val log = Logger { }

object NavDestinations {

    const val CategoryList = "categoryList"

    const val EditCategory = "editCategory"
    const val EditCategory_Arg_CategoryId = "categoryId"
    const val EditCategory_With_Args = "editCategory/{$EditCategory_Arg_CategoryId}"
    const val EditCategory_Result_CategoryId = "editCategoryResultCategoryId"

    const val EditStore = "editStore"
    const val EditStore_Arg_StoreId = "storeId"
    const val EditStore_With_Args = "editStore/{$EditStore_Arg_StoreId}"
    const val EditStore_Result_StoreId = "editStoreResultStoreId"

    const val NewCategory = "newCategory"
    const val NewCategory_Result_CategoryId = "newCategoryResultCategoryId"

    const val NewPrice = "newPrice"
    const val NewPrice_Arg_ProductId = "productId"
    const val NewPrice_Arg_CategoryId = "categoryId"
    const val NewPrice_With_Args =
        "$NewPrice?$NewPrice_Arg_ProductId={$NewPrice_Arg_ProductId}?$NewPrice_Arg_CategoryId={$NewPrice_Arg_CategoryId}"

    const val NewStore = "newStore"
    const val NewStore_Result_StoreId = "newStoreResultStoreId"

    const val ProductList = "productList"
    const val ProductList_Arg_CategoryId = "categoryId"
    const val ProductList_With_Args =
        "$ProductList?$ProductList_Arg_CategoryId={$ProductList_Arg_CategoryId}"

    const val ProductDetail = "ProductDetail"
    const val ProductDetail_Arg_ProductId = "productId"
    const val ProductDetail_With_Args =
        "$ProductDetail/{$ProductDetail_Arg_ProductId}"

    const val SearchProduct = "searchProduct"
    const val SelectCurrency = "selectCurrency"
    const val Settings = "settings"

}

private object NavRoutes {
    const val CategoryRoute = "CategoryRoute"
    const val SettingsRoute = "settingsRoute"
}

@OptIn(ExperimentalAnimationApi::class)
@RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
@Composable
fun App() {
    AppTheme {
        val navController = rememberAnimatedNavController()

        val animationSpec: FiniteAnimationSpec<Float> =
            spring(stiffness = Spring.StiffnessMediumLow)
        AnimatedNavHost(
            navController = navController,
            startDestination = CategoryRoute,
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
        ) {
            categoryGraph(navController)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.categoryGraph(navController: NavHostController) {
    navigation(startDestination = CategoryList, route = CategoryRoute) {
        composable(CategoryList) {
            val categoryListScreenViewModel = hiltViewModel<CategoryListScreenViewModel>()

            CategoryListScreen(
                categoryListScreenViewModel = categoryListScreenViewModel,
                navigateToSettings = { navController.navigate(SettingsRoute) },
                navigateToSearchProduct = { navController.navigate(SearchProduct) },
                navigateToProductList = {
                    if (it != null) {
                        navController.navigate("${ProductList}?$ProductList_Arg_CategoryId=${it.id}")
                    } else {
                        navController.navigate(ProductList)
                    }
                },
                navigateToNewPrice = { navController.navigate(NewPrice) },
                navigateToShoppingList = { /* TODO */ }
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

            backStackEntry.arguments?.let { arg ->
                val categoryId = arg.getLong(ProductList_Arg_CategoryId).takeIf { it != 0L }

                ProductListScreen(
                    viewModel = productListScreenViewModel,
                    navigateUp = { navController.navigateUp() },
                    navigateToSearchProduct = { navController.navigate(SearchProduct) },
                    navigateToEditCategory = {
                        if (categoryId == null)
                            throw IllegalArgumentException("Cannot edit \"No Category\"")

                        navController.navigate("$EditCategory/$categoryId")
                    },
                    navigateToProductDetail = {
                        require(it != 0L) { "Product id cannot be 0" }

                        navController.navigate("$ProductDetail/$it")
                    }
                )
            } ?: throw IllegalArgumentException("argument should not be null")
        }
        composable(SearchProduct) {
            val searchProductScreenViewModel = hiltViewModel<SearchProductScreenViewModel>()

            SearchProductScreen(
                viewModel = searchProductScreenViewModel,
                navigateUp = { navController.navigateUp() },
                navigateToProductDetails = { }
            )
        }
        composable(
            route = NewPrice_With_Args,
            arguments = listOf(
                navArgument(NewPrice_Arg_ProductId) {
                    type = NavType.LongType
                    defaultValue = 0L
                },
                navArgument(NewPrice_Arg_CategoryId) {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val newPriceViewModel = hiltViewModel<NewPriceScreenViewModel>()

            NewPriceScreen(
                savedStateHandle = backStackEntry.savedStateHandle,
                newPriceScreenViewModel = newPriceViewModel,
                navigateUp = { navController.navigateUp() },
                navigateToNewCategory = {
                    navController.navigate(NewCategory)
                },
                navigateToEditCategory = {
                    navController.navigate("${EditCategory}/${it.id}")
                },
                navigateToNewStore = { navController.navigate(NewStore) },
                navigateToEditStore = { navController.navigate("${EditStore}/${it.id}") },
            )
        }
        composable(NewCategory) {
            val newCategoryViewModel = hiltViewModel<NewCategoryScreenViewModel>()

            NewCategoryScreen(
                viewModel = newCategoryViewModel,
                navigateUp = { navController.navigateUp() },
                navigateDone = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        NewCategory_Result_CategoryId,
                        it
                    ) ?: throw IllegalStateException("NewCategory result is not received.")
                    navController.navigateUp()
                }
            )
        }
        composable(
            EditCategory_With_Args,
            arguments = listOf(navArgument(EditCategory_Arg_CategoryId) {
                type = NavType.LongType
            })
        ) { backStackEntry ->
            val editCategoryViewModel = hiltViewModel<EditCategoryScreenViewModel>()

            backStackEntry.arguments?.let { arg ->
                val categoryId = arg.getLong(EditCategory_Arg_CategoryId).takeIf { it != 0L }

                EditCategoryScreen(
                    viewModel = editCategoryViewModel,
                    navigateUp = { navController.navigateUp() },
                    navigateDone = {
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            EditCategory_Result_CategoryId,
                            categoryId
                        ) ?: throw IllegalStateException("EditCategory result is not received.")
                        navController.navigateUp()
                    }
                )
            } ?: throw IllegalArgumentException("argument should not be null")
        }
        composable(NewStore) {
            val newStoreViewModel = hiltViewModel<NewStoreScreenViewModel>()

            NewStoreScreen(
                newStoreViewModel = newStoreViewModel,
                navigateUp = { navController.navigateUp() },
                navigateDone = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        NewStore_Result_StoreId,
                        it
                    ) ?: throw IllegalStateException("NewStore result is not received.")
                    navController.navigateUp()
                }
            )
        }
        composable(
            EditStore_With_Args,
            arguments = listOf(navArgument(EditStore_Arg_StoreId) {
                type = NavType.LongType
            })
        ) { backStackEntry ->
            val editStoreViewModel = hiltViewModel<EditStoreScreenViewModel>()

            backStackEntry.arguments?.let { arg ->
                val storeId = arg.getLong(EditStore_Arg_StoreId).takeIf { it != 0L }

                EditStoreScreen(
                    viewModel = editStoreViewModel,
                    navigateUp = { navController.navigateUp() },
                    navigateDone = {
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            EditStore_Result_StoreId,
                            storeId
                        ) ?: throw IllegalStateException("EditStore result is not received.")
                        navController.navigateUp()
                    }
                )
            } ?: throw IllegalArgumentException("argument should not be null")
        }
        composable(
            ProductDetail_With_Args,
            arguments = listOf(navArgument(ProductDetail_Arg_ProductId) {
                type = NavType.LongType
            })
        ) { backStackEntry ->

        }

        settingsGraph(navController)
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.settingsGraph(navController: NavHostController) {
    navigation(route = SettingsRoute, startDestination = Settings) {
        composable(Settings) {
            val settingsViewModel = hiltViewModel<SettingsScreenViewModel>()

            SettingsScreen(
                viewModel = settingsViewModel,
                navigateUp = { navController.navigateUp() },
                navigateToSelectCurrencyScreen = { navController.navigate(SelectCurrency) }
            )
        }
        composable(SelectCurrency) {
            val selectCurrencyViewModel = hiltViewModel<SelectCurrencyScreenViewModel>()

            SelectCurrencyScreen(
                viewModel = selectCurrencyViewModel,
                navigateUp = { navController.navigateUp() },
            )
        }
    }
}

