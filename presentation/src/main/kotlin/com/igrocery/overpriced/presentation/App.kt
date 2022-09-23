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
import com.igrocery.overpriced.presentation.NavDestinations.EditCategory
import com.igrocery.overpriced.presentation.NavDestinations.EditCategory_Arg_CategoryId
import com.igrocery.overpriced.presentation.NavDestinations.EditCategory_With_Args
import com.igrocery.overpriced.presentation.NavDestinations.EditStore
import com.igrocery.overpriced.presentation.NavDestinations.EditStore_Arg_StoreId
import com.igrocery.overpriced.presentation.NavDestinations.EditStore_With_Args
import com.igrocery.overpriced.presentation.NavDestinations.NewCategory
import com.igrocery.overpriced.presentation.NavDestinations.NewPrice
import com.igrocery.overpriced.presentation.NavDestinations.NewStore
import com.igrocery.overpriced.presentation.NavDestinations.CategoryProduct
import com.igrocery.overpriced.presentation.NavDestinations.SearchProduct
import com.igrocery.overpriced.presentation.NavDestinations.SelectCurrency
import com.igrocery.overpriced.presentation.NavDestinations.Settings
import com.igrocery.overpriced.presentation.NavRoutes.NewPriceRecordRoute
import com.igrocery.overpriced.presentation.NavRoutes.SettingsRoute
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
import com.igrocery.overpriced.presentation.categoryproduct.CategoryProductScreen
import com.igrocery.overpriced.presentation.searchproduct.SearchProductScreen
import com.igrocery.overpriced.presentation.searchproduct.SearchProductScreenViewModel
import com.igrocery.overpriced.presentation.selectcategory.SelectCategoryDialogViewModel
import com.igrocery.overpriced.presentation.selectcurrency.SelectCurrencyScreen
import com.igrocery.overpriced.presentation.selectcurrency.SelectCurrencyScreenViewModel
import com.igrocery.overpriced.presentation.settings.SettingsScreen
import com.igrocery.overpriced.presentation.settings.SettingsScreenViewModel
import com.igrocery.overpriced.presentation.ui.theme.AppTheme
import com.igrocery.overpriced.shared.Logger

@Suppress("unused")
private val log = Logger { }

object NavDestinations {

    const val CategoryProduct = "categoryProduct"

    const val EditCategory = "editCategory"
    const val EditCategory_Arg_CategoryId = "categoryId"
    const val EditCategory_With_Args = "editCategory/{$EditCategory_Arg_CategoryId}"

    const val EditStore = "editStore"
    const val EditStore_Arg_StoreId = "storeId"
    const val EditStore_With_Args = "editStore/{$EditStore_Arg_StoreId}"

    const val NewCategory = "newCategory"
    const val NewPrice = "newPrice"
    const val NewStore = "newStore"
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
            startDestination = CategoryProduct,
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
            composable(CategoryProduct) {
                CategoryProductScreen(
                    navigateToSettings = { navController.navigate(SettingsRoute) },
                    navigateToSearchProduct = { navController.navigate(SearchProduct) },
                    navigateToEditCategory = { categoryId ->
                        navController.navigate("$EditCategory/$categoryId")
                    },
                    navigateToNewPrice = { navController.navigate(NewPriceRecordRoute) },
                    navigateToShoppingList = { /*TODO*/ })
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
            ) { backStackEntry ->
                val editCategoryViewModel = hiltViewModel<EditCategoryScreenViewModel>()

                val categoryId = backStackEntry.arguments?.getLong(EditCategory_Arg_CategoryId)
                    ?: throw IllegalArgumentException("categoryId cannot be null")

                EditCategoryScreen(
                    categoryId = categoryId,
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
            // TODO these should be created inside the screen, passing navGraphEntry as an argument
            val newPriceViewModel =
                hiltViewModel<NewPriceScreenViewModel>(navGraphEntry)
            val selectCategoryDialogViewModel =
                hiltViewModel<SelectCategoryDialogViewModel>(navGraphEntry)

            NewPriceScreen(
                newPriceScreenViewModel = newPriceViewModel,
                selectCategoryDialogViewModel = selectCategoryDialogViewModel,
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
                    newPriceViewModel.setProductCategoryId(it)
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

            val categoryId =
                backStackEntry.arguments?.getLong(EditCategory_Arg_CategoryId) ?: 0L

            EditCategoryScreen(
                categoryId = categoryId,
                viewModel = editCategoryViewModel,
                navigateUp = { navController.navigateUp() },
                navigateDone = {
                    newPriceViewModel.setProductCategoryId(categoryId)
                    navController.navigateUp()
                }
            )
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
                    newPriceViewModel.selectStore(it)
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

            val storeId = backStackEntry.arguments?.getLong(EditStore_Arg_StoreId) ?: 0L

            EditStoreScreen(
                storeId = storeId,
                viewModel = editStoreViewModel,
                navigateUp = { navController.navigateUp() },
                navigateDone = {
                    newPriceViewModel.selectStore(storeId)
                    navController.navigateUp()
                }
            )
        }
    }
}
