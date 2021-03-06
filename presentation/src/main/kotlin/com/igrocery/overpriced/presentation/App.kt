package com.igrocery.overpriced.presentation

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.animation.*
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navigation
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
import com.igrocery.overpriced.presentation.NavDestinations.ProductPriceList
import com.igrocery.overpriced.presentation.NavDestinations.ScanBarcode
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
import com.igrocery.overpriced.presentation.categorylist.CategoryListScreen
import com.igrocery.overpriced.presentation.categorylist.CategoryListScreenViewModel
import com.igrocery.overpriced.presentation.scanbarcode.ScanBarcodeScreen
import com.igrocery.overpriced.presentation.scanbarcode.ScanBarcodeScreenViewModel
import com.igrocery.overpriced.presentation.selectcategory.SelectCategoryDialogViewModel
import com.igrocery.overpriced.presentation.selectcurrency.SelectCurrencyScreen
import com.igrocery.overpriced.presentation.selectcurrency.SelectCurrencyScreenViewModel
import com.igrocery.overpriced.presentation.selectstore.SelectStoreDialogViewModel
import com.igrocery.overpriced.presentation.settings.SettingsScreen
import com.igrocery.overpriced.presentation.settings.SettingsScreenViewModel
import com.igrocery.overpriced.presentation.ui.theme.AppTheme
import com.igrocery.overpriced.shared.Logger

@Suppress("unused")
private val log = Logger { }

private object NavDestinations {

    const val EditCategory = "editCategory"
    const val EditCategory_Arg_CategoryId = "categoryId"
    const val EditCategory_With_Args = "editCategory/{$EditCategory_Arg_CategoryId}"

    const val EditStore = "editStore"
    const val EditStore_Arg_StoreId = "storeId"
    const val EditStore_With_Args = "editStore/{$EditStore_Arg_StoreId}"

    const val NewCategory = "newCategory"
    const val NewPrice = "newPrice"
    const val NewStore = "newStore"
    const val ProductPriceList = "productPriceList"
    const val ScanBarcode = "scanBarcode"
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

        val animationSpec: FiniteAnimationSpec<Float> = spring(stiffness = Spring.StiffnessMediumLow)
        AnimatedNavHost(
            navController = navController,
            startDestination = ProductPriceList,
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
            }
        ) {
            composable(ProductPriceList) {
                val categoryListScreenViewModel =
                    hiltViewModel<CategoryListScreenViewModel>()

                CategoryListScreen(
                    categoryListScreenViewModel = categoryListScreenViewModel,
                    navigateUp = { navController.navigateUp() },
                    navigateToSettings = { navController.navigate(SettingsRoute) },
                    navigateToAddPrice = { navController.navigate(NewPriceRecordRoute) }
                ) {
//                navController.navigate(screen.route) {
//                    // Pop up to the start destination of the graph to
//                    // avoid building up a large stack of destinations
//                    // on the back stack as users select items
//                    popUpTo(navController.graph.findStartDestination().id) {
//                        saveState = true
//                    }
//                    // Avoid multiple copies of the same destination when
//                    // reselecting the same item
//                    launchSingleTop = true
//                    // Restore state when reselecting a previously selected item
//                    restoreState = true
//                }
                }
            }

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

            navigation(route = NewPriceRecordRoute, startDestination = NewPrice) {
                composable(NewPrice) { backStackEntry ->
                    val navGraphEntry =
                        remember(backStackEntry) {
                            navController.getBackStackEntry(NewPriceRecordRoute)
                        }
                    val newPriceViewModel = hiltViewModel<NewPriceScreenViewModel>(navGraphEntry)
                    val selectCategoryDialogViewModel = hiltViewModel<SelectCategoryDialogViewModel>(navGraphEntry)
                    val selectStoreDialogViewModel = hiltViewModel<SelectStoreDialogViewModel>(navGraphEntry)

                    NewPriceScreen(
                        newPriceScreenViewModel = newPriceViewModel,
                        selectCategoryDialogViewModel = selectCategoryDialogViewModel,
                        selectStoreDialogViewModel = selectStoreDialogViewModel,
                        navigateUp = { navController.navigateUp() },
                        navigateToScanBarcode = { navController.navigate(ScanBarcode) },
                        navigateToNewCategory = { navController.navigate(NewCategory) },
                        navigateToEditCategory = { navController.navigate("$EditCategory/${it.id}") },
                        navigateToNewStore = { navController.navigate(NewStore) },
                        navigateToEditStore = { navController.navigate("$EditStore/${it.id}") },
                    )
                }
                composable(ScanBarcode) { backStackEntry ->
                    val navGraphEntry =
                        remember(backStackEntry) {
                            navController.getBackStackEntry(
                                NewPriceRecordRoute
                            )
                        }
                    val newPriceViewModel = hiltViewModel<NewPriceScreenViewModel>(navGraphEntry)
                    val scanBarcodeScreenViewModel = hiltViewModel<ScanBarcodeScreenViewModel>()

                    ScanBarcodeScreen(
                        viewModel = scanBarcodeScreenViewModel,
                        navigateUp = { navController.navigateUp() },
                        navigateDone = { barcode ->
                            newPriceViewModel.setBarcode(barcode)
                            navController.navigateUp()
                        })
                }
                composable(NewCategory) { backStackEntry ->
                    val navGraphEntry =
                        remember(backStackEntry) {
                            navController.getBackStackEntry(
                                NewPriceRecordRoute
                            )
                        }
                    val newPriceViewModel = hiltViewModel<NewPriceScreenViewModel>(navGraphEntry)
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
                    arguments = listOf(navArgument(EditCategory_Arg_CategoryId) { type = NavType.LongType })
                ) { backStackEntry ->
                    val navGraphEntry =
                        remember(backStackEntry) {
                            navController.getBackStackEntry(
                                NewPriceRecordRoute
                            )
                        }
                    val newPriceViewModel = hiltViewModel<NewPriceScreenViewModel>(navGraphEntry)
                    val editCategoryViewModel = hiltViewModel<EditCategoryScreenViewModel>()

                    val categoryId = backStackEntry.arguments?.getLong(EditCategory_Arg_CategoryId) ?: 0L

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
                    val newPriceViewModel = hiltViewModel<NewPriceScreenViewModel>(navGraphEntry)
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
                    arguments = listOf(navArgument(EditStore_Arg_StoreId) { type = NavType.LongType })
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
    }
}

