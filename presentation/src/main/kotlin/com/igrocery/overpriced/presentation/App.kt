package com.igrocery.overpriced.presentation

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.animation.*
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.igrocery.overpriced.presentation.NavDestinations.CategoryDetail
import com.igrocery.overpriced.presentation.NavDestinations.CategoryDetail_Arg_CategoryId
import com.igrocery.overpriced.presentation.NavDestinations.CategoryDetail_With_Args
import com.igrocery.overpriced.presentation.NavDestinations.EditCategory
import com.igrocery.overpriced.presentation.NavDestinations.EditCategory_Arg_CategoryId
import com.igrocery.overpriced.presentation.NavDestinations.EditCategory_With_Args
import com.igrocery.overpriced.presentation.NavDestinations.EditStore
import com.igrocery.overpriced.presentation.NavDestinations.EditStore_Arg_StoreId
import com.igrocery.overpriced.presentation.NavDestinations.EditStore_With_Args
import com.igrocery.overpriced.presentation.NavDestinations.NewCategory
import com.igrocery.overpriced.presentation.NavDestinations.NewPrice
import com.igrocery.overpriced.presentation.NavDestinations.NewStore
import com.igrocery.overpriced.presentation.NavDestinations.CategoryList
import com.igrocery.overpriced.presentation.NavDestinations.ScanBarcode
import com.igrocery.overpriced.presentation.NavDestinations.SearchProduct
import com.igrocery.overpriced.presentation.NavDestinations.SelectCurrency
import com.igrocery.overpriced.presentation.NavDestinations.Settings
import com.igrocery.overpriced.presentation.NavRoutes.NewPriceRecordRoute
import com.igrocery.overpriced.presentation.NavRoutes.SettingsRoute
import com.igrocery.overpriced.presentation.categorydetail.CategoryDetailScreen
import com.igrocery.overpriced.presentation.categorydetail.CategoryDetailScreenViewModel
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
import com.igrocery.overpriced.presentation.searchproduct.SearchProductScreen
import com.igrocery.overpriced.presentation.searchproduct.SearchProductScreenViewModel
import com.igrocery.overpriced.presentation.selectcategory.SelectCategoryDialogViewModel
import com.igrocery.overpriced.presentation.selectcurrency.SelectCurrencyScreen
import com.igrocery.overpriced.presentation.selectcurrency.SelectCurrencyScreenViewModel
import com.igrocery.overpriced.presentation.selectstore.SelectStoreDialogViewModel
import com.igrocery.overpriced.presentation.settings.SettingsScreen
import com.igrocery.overpriced.presentation.settings.SettingsScreenViewModel
import com.igrocery.overpriced.presentation.ui.theme.AppTheme
import com.igrocery.overpriced.shared.Logger
import com.ireceipt.receiptscanner.presentation.R

@Suppress("unused")
private val log = Logger { }

private object NavDestinations {

    const val CategoryDetail = "categoryDetail"
    const val CategoryDetail_Arg_CategoryId = "categoryId"
    const val CategoryDetail_With_Args = "categoryDetail/{$CategoryDetail_Arg_CategoryId}"

    const val EditCategory = "editCategory"
    const val EditCategory_Arg_CategoryId = "categoryId"
    const val EditCategory_With_Args = "editCategory/{$EditCategory_Arg_CategoryId}"

    const val EditStore = "editStore"
    const val EditStore_Arg_StoreId = "storeId"
    const val EditStore_With_Args = "editStore/{$EditStore_Arg_StoreId}"

    const val NewCategory = "newCategory"
    const val NewPrice = "newPrice"
    const val NewStore = "newStore"
    const val CategoryList = "categoryList"
    const val ScanBarcode = "scanBarcode"
    const val SearchProduct = "searchProduct"
    const val SelectCurrency = "selectCurrency"
    const val Settings = "settings"

}

private object NavRoutes {
    const val SettingsRoute = "settingsRoute"
    const val NewPriceRecordRoute = "newPriceRecordRoute"
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
@Composable
fun App() {
    AppTheme {
        val navController = rememberAnimatedNavController()

        // states belonging to the global scaffold
        val coroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val isFabExpanded by remember { mutableStateOf(true) }

        // Global scaffold, this contains common elements that are not tied to a particular screen
        val currentRoute by navController.currentBackStackEntryAsState()
        Scaffold(
//            bottomBar = {
//                when (currentRoute?.destination?.route) {
//                    CategoryList -> {
//                        NavigationBar(
//                            modifier = Modifier
//                                .navigationBarsPadding()
//                                .imePadding(),
//                        ) {
//                            NavigationBarItem(
//                                icon = {
//                                    Icon(
//                                        painter = painterResource(id = R.drawable.ic_baseline_attach_money_24),
//                                        contentDescription = stringResource(id = R.string.category_list_bottom_nav_content_description),
//                                        modifier = Modifier.size(24.dp)
//                                    )
//                                },
//                                label = { Text(text = stringResource(id = R.string.category_list_bottom_nav_label)) },
//                                selected = true,
//                                onClick = { }
//                            )
//                            NavigationBarItem(
//                                icon = {
//                                    Icon(
//                                        painter = painterResource(id = R.drawable.ic_baseline_shopping_cart_24),
//                                        contentDescription = stringResource(id = R.string.shopping_lists_bottom_nav_content_description),
//                                        modifier = Modifier.size(24.dp)
//                                    )
//                                },
//                                label = { Text(text = stringResource(id = R.string.shopping_lists_bottom_nav_label)) },
//                                selected = false,
//                                onClick = { }
//                            )
//                        }
//                    }
//                    else -> {
//                        // No bottom bar
//                    }
//                }
//            },
//            snackbarHost = {
//                SnackbarHost(
//                    hostState = snackbarHostState,
//                    modifier = Modifier
//                        .navigationBarsPadding()
//                        .imePadding()
//                )
//            },
            floatingActionButton = {
                AnimatedContent(
                    targetState = currentRoute?.destination?.route,
                    transitionSpec = {
                        scaleIn(
                            initialScale = 0.4f,
                            transformOrigin = TransformOrigin(1f, 1f)
                        ) + fadeIn() with scaleOut(
                            targetScale = 0.4f,
                            transformOrigin = TransformOrigin(1f, 0.8f)
                        ) + fadeOut() using SizeTransform()
                    },
                    contentAlignment = Alignment.BottomEnd,
                ) {
                    when (it) {
                        CategoryList -> {
                            log.error("CategoryList FAB")
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
                                expanded = isFabExpanded,
                                onClick = { navController.navigate(NewPriceRecordRoute) },
                                modifier = Modifier
                                    .navigationBarsPadding()
                                    .imePadding()
                            )
                        }
                        CategoryDetail_With_Args -> {
                            FloatingActionButton(
                                onClick = { }, // TODO
                                modifier = Modifier
                                    .navigationBarsPadding()
                                    .imePadding(),
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_add_24),
                                    contentDescription = stringResource(
                                        id = R.string.category_list_new_price_fab_content_description
                                    ),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        else -> {
                            // No fab
                            log.error("No FAB")
                        }
                    }
                }
            }
        ) { scaffoldPadding ->

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
                }
            ) {
                composable(CategoryList) {
                    val categoryListScreenViewModel =
                        hiltViewModel<CategoryListScreenViewModel>()

                    CategoryListScreen(
                        categoryListScreenViewModel = categoryListScreenViewModel,
                        navigateUp = { navController.navigateUp() },
                        navigateToSearchProduct = { navController.navigate(SearchProduct) },
                        navigateToSettings = { navController.navigate(SettingsRoute) },
                        navigateToCategoryDetail = { navController.navigate("$CategoryDetail/${it.id}") },
                    )
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
                    route = CategoryDetail_With_Args,
                    arguments = listOf(navArgument(CategoryDetail_Arg_CategoryId) {
                        type = NavType.LongType
                    })
                ) { backStackEntry ->
                    val categoryDetailScreenViewModel =
                        hiltViewModel<CategoryDetailScreenViewModel>()

                    val categoryId =
                        backStackEntry.arguments?.getLong(CategoryDetail_Arg_CategoryId)
                            ?: throw IllegalArgumentException("Category id must not be null.")
                    CategoryDetailScreen(
                        categoryId = categoryId,
                        viewModel = categoryDetailScreenViewModel,
                        navigateUp = { navController.navigateUp() },
                        navigateToSearchProduct = {},
                        navigateToEditCategory = {},
                    )
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
                        val newPriceViewModel =
                            hiltViewModel<NewPriceScreenViewModel>(navGraphEntry)
                        val selectCategoryDialogViewModel =
                            hiltViewModel<SelectCategoryDialogViewModel>(navGraphEntry)
                        val selectStoreDialogViewModel =
                            hiltViewModel<SelectStoreDialogViewModel>(navGraphEntry)

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
                        val newPriceViewModel =
                            hiltViewModel<NewPriceScreenViewModel>(navGraphEntry)
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
                        val newPriceViewModel =
                            hiltViewModel<NewPriceScreenViewModel>(navGraphEntry)
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
}

