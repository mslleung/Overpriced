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
import com.igrocery.overpriced.presentation.NavDestinations.CategoryProduct
import com.igrocery.overpriced.presentation.NavDestinations.EditCategory
import com.igrocery.overpriced.presentation.NavDestinations.EditCategory_Arg_CategoryId
import com.igrocery.overpriced.presentation.NavDestinations.EditCategory_With_Args
import com.igrocery.overpriced.presentation.NavDestinations.EditStore
import com.igrocery.overpriced.presentation.NavDestinations.EditStore_Arg_StoreId
import com.igrocery.overpriced.presentation.NavDestinations.EditStore_With_Args
import com.igrocery.overpriced.presentation.NavDestinations.NewCategory
import com.igrocery.overpriced.presentation.NavDestinations.NewPrice
import com.igrocery.overpriced.presentation.NavDestinations.NewStore
import com.igrocery.overpriced.presentation.NavDestinations.ProductList
import com.igrocery.overpriced.presentation.NavDestinations.ProductList_Arg_CategoryId
import com.igrocery.overpriced.presentation.NavDestinations.ProductList_With_Args
import com.igrocery.overpriced.presentation.NavDestinations.SearchProduct
import com.igrocery.overpriced.presentation.NavDestinations.SelectCurrency
import com.igrocery.overpriced.presentation.NavDestinations.Settings
import com.igrocery.overpriced.presentation.NavRoutes.NewPriceRecordRoute
import com.igrocery.overpriced.presentation.NavRoutes.SettingsRoute
import com.igrocery.overpriced.presentation.categorybase.CategoryBaseScreen
import com.igrocery.overpriced.presentation.categorybase.NavDestinations
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

    const val EditStore = "editStore"
    const val EditStore_Arg_StoreId = "storeId"
    const val EditStore_With_Args = "editStore/{$EditStore_Arg_StoreId}"

    const val NewCategory = "newCategory"
    const val NewPrice = "newPrice"
    const val NewStore = "newStore"

    const val ProductList = "productList"
    const val ProductList_Arg_CategoryId = "categoryId"
    const val ProductList_With_Args =
        "$ProductList?$ProductList_Arg_CategoryId={$ProductList_Arg_CategoryId}"

    const val SearchProduct = "searchProduct"
    const val SelectCurrency = "selectCurrency"
    const val Settings = "settings"

}

private object NavRoutes {
    const val SettingsRoute = "settingsRoute"
    const val NewPriceRecordRoute = "newPriceRecordRoute"
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
        ) {
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
                EditCategory_With_Args,
                arguments = listOf(navArgument(EditCategory_Arg_CategoryId) {
                    type = NavType.LongType
                })
            ) {
                val editCategoryViewModel = hiltViewModel<EditCategoryScreenViewModel>()

                EditCategoryScreen(
                    viewModel = editCategoryViewModel,
                    navigateUp = { navController.navigateUp() },
                    navigateDone = {
                        navController.navigateUp()
                    }
                )
            }

            settingsGraph(navController)
            newPriceRecordGraph(navController)
        }
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

@OptIn(ExperimentalAnimationApi::class)
@RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
private fun NavGraphBuilder.newPriceRecordGraph(navController: NavHostController) {
    navigation(route = NewPriceRecordRoute, startDestination = NewPrice) {
        composable(NewPrice) { backStackEntry ->
            val navGraphEntry =
                remember(backStackEntry) {
                    navController.getBackStackEntry(NewPriceRecordRoute)
                }
            val newPriceViewModel =
                hiltViewModel<NewPriceScreenViewModel>(navGraphEntry)

            NewPriceScreen(
                newPriceScreenViewModel = newPriceViewModel,
                navigateUp = { navController.navigateUp() },
                navigateToNewCategory = { navController.navigate(NewCategory) },
                navigateToEditCategory = { navController.navigate("$EditCategory/${it.id}") },
                navigateToNewStore = { navController.navigate(NewStore) },
                navigateToEditStore = { navController.navigate("$EditStore/${it.id}") },
            )
        }
        composable(NewCategory) { backStackEntry ->
            val navGraphEntry =
                remember(backStackEntry) {
                    navController.getBackStackEntry(
                        NewPriceRecordRoute
                    )
                }
            val newPriceViewModel =
                hiltViewModel<NewPriceScreenViewModel>(navGraphEntry)
            val newCategoryViewModel = hiltViewModel<NewCategoryScreenViewModel>()

            NewCategoryScreen(
                viewModel = newCategoryViewModel,
                navigateUp = { navController.navigateUp() },
                navigateDone = {
                    newPriceViewModel.updateCategoryId(it)
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
            val navGraphEntry =
                remember(backStackEntry) {
                    navController.getBackStackEntry(
                        NewPriceRecordRoute
                    )
                }
            val newPriceViewModel =
                hiltViewModel<NewPriceScreenViewModel>(navGraphEntry)
            val editCategoryViewModel = hiltViewModel<EditCategoryScreenViewModel>()

            backStackEntry.arguments?.let { arg ->
                val categoryId = arg.getLong(EditCategory_Arg_CategoryId).takeIf { it != 0L }

                EditCategoryScreen(
                    viewModel = editCategoryViewModel,
                    navigateUp = { navController.navigateUp() },
                    navigateDone = {
                        newPriceViewModel.updateCategoryId(categoryId)
                        navController.navigateUp()
                    }
                )
            } ?: throw IllegalArgumentException("argument should not be null")
        }
        composable(NewStore) { backStackEntry ->
            val navGraphEntry =
                remember(backStackEntry) {
                    navController.getBackStackEntry(NewPriceRecordRoute)
                }
            val newPriceViewModel =
                hiltViewModel<NewPriceScreenViewModel>(navGraphEntry)
            val newStoreViewModel = hiltViewModel<NewStoreScreenViewModel>()

            NewStoreScreen(
                newStoreViewModel = newStoreViewModel,
                navigateUp = { navController.navigateUp() },
                navigateDone = {
                    newPriceViewModel.updateStoreId(it)
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
            val navGraphEntry =
                remember(backStackEntry) {
                    navController.getBackStackEntry(
                        NewPriceRecordRoute
                    )
                }
            val newPriceViewModel = hiltViewModel<NewPriceScreenViewModel>(navGraphEntry)
            val editStoreViewModel = hiltViewModel<EditStoreScreenViewModel>()

            backStackEntry.arguments?.let { arg ->
                val storeId = arg.getLong(EditStore_Arg_StoreId).takeIf { it != 0L }

                EditStoreScreen(
                    viewModel = editStoreViewModel,
                    navigateUp = { navController.navigateUp() },
                    navigateDone = {
                        newPriceViewModel.updateStoreId(storeId)
                        navController.navigateUp()
                    }
                )
            } ?: throw IllegalArgumentException("argument should not be null")
        }
    }
}
