package com.igrocery.overpriced.presentation

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.animation.*
import androidx.compose.animation.core.FiniteAnimationSpec
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
import com.igrocery.overpriced.presentation.NavDestinations.EditStore
import com.igrocery.overpriced.presentation.NavDestinations.EditStore_Arg_StoreId
import com.igrocery.overpriced.presentation.NavDestinations.EditStore_With_Args
import com.igrocery.overpriced.presentation.NavDestinations.NewPrice
import com.igrocery.overpriced.presentation.NavDestinations.NewStore
import com.igrocery.overpriced.presentation.NavDestinations.ProductPriceList
import com.igrocery.overpriced.presentation.NavDestinations.ScanBarcode
import com.igrocery.overpriced.presentation.NavDestinations.SelectCurrency
import com.igrocery.overpriced.presentation.NavDestinations.Settings
import com.igrocery.overpriced.presentation.NavRoutes.NewPriceRecordRoute
import com.igrocery.overpriced.presentation.NavRoutes.SettingsRoute
import com.igrocery.overpriced.presentation.editstore.EditStoreScreen
import com.igrocery.overpriced.presentation.editstore.EditStoreScreenViewModel
import com.igrocery.overpriced.presentation.newprice.NewPriceScreen
import com.igrocery.overpriced.presentation.newprice.NewPriceScreenViewModel
import com.igrocery.overpriced.presentation.newstore.NewStoreScreen
import com.igrocery.overpriced.presentation.newstore.NewStoreScreenViewModel
import com.igrocery.overpriced.presentation.productpricelist.ProductPriceListScreen
import com.igrocery.overpriced.presentation.productpricelist.ProductPriceListScreenViewModel
import com.igrocery.overpriced.presentation.scanbarcode.ScanBarcodeScreen
import com.igrocery.overpriced.presentation.scanbarcode.ScanBarcodeScreenViewModel
import com.igrocery.overpriced.presentation.selectcategory.SelectCategoryDialogViewModel
import com.igrocery.overpriced.presentation.selectcurrency.SelectCurrencyScreen
import com.igrocery.overpriced.presentation.selectcurrency.SelectCurrencyScreenViewModel
import com.igrocery.overpriced.presentation.settings.SettingsScreen
import com.igrocery.overpriced.presentation.settings.SettingsScreenViewModel
import com.igrocery.overpriced.presentation.ui.theme.AppTheme
import com.igrocery.overpriced.shared.Logger

@Suppress("unused")
private val log = Logger { }

private object NavDestinations {

    const val EditStore = "editStore"
    const val EditStore_Arg_StoreId = "storeId"
    const val EditStore_With_Args = "editStore/{$EditStore_Arg_StoreId}"

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

        // The default is Spring.StiffnessMediumLow (400f). It is a bit too slow for my taste.
        val animationSpec: FiniteAnimationSpec<Float> = spring(stiffness = 800f)
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
                val productPriceListScreenViewModel =
                    hiltViewModel<ProductPriceListScreenViewModel>()

                ProductPriceListScreen(
                    productPriceListScreenViewModel = productPriceListScreenViewModel,
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
                    val selectCategoryDialogViewModel = hiltViewModel<SelectCategoryDialogViewModel>()

                    NewPriceScreen(
                        newPriceScreenViewModel = newPriceViewModel,
                        selectCategoryDialogViewModel = selectCategoryDialogViewModel,
                        navigateUp = { navController.navigateUp() },
                        navigateToScanBarcode = { navController.navigate(ScanBarcode) },
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
                    val editStoreViewModel = hiltViewModel<EditStoreScreenViewModel>()
                    
                    val storeId = backStackEntry.arguments?.getLong(EditStore_Arg_StoreId) ?: 0L
                    
                    EditStoreScreen(
                        storeId = storeId,
                        viewModel = editStoreViewModel,
                        navigateUp = { navController.navigateUp() },
                        navigateDone = { navController.navigateUp() }
                    )
                }
            }
        }
    }
}

