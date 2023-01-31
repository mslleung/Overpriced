package com.igrocery.overpriced.presentation

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.animation.*
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.igrocery.overpriced.presentation.NavRoutes.SettingsRoute
import com.igrocery.overpriced.presentation.editcategory.*
import com.igrocery.overpriced.presentation.editgrocerylist.editGroceryListScreen
import com.igrocery.overpriced.presentation.editgrocerylist.navigateToEditGroceryListScreen
import com.igrocery.overpriced.presentation.editstore.EditStore_Result_StoreId
import com.igrocery.overpriced.presentation.editstore.editStoreScreen
import com.igrocery.overpriced.presentation.editstore.navigateToEditStoreScreen
import com.igrocery.overpriced.presentation.mainnavigation.MainBottomNavigation
import com.igrocery.overpriced.presentation.mainnavigation.mainBottomNavigationScreen
import com.igrocery.overpriced.presentation.newcategory.*
import com.igrocery.overpriced.presentation.newprice.navigateToNewPriceScreen
import com.igrocery.overpriced.presentation.newprice.newPriceScreen
import com.igrocery.overpriced.presentation.newstore.NewStore_Result_StoreId
import com.igrocery.overpriced.presentation.newstore.navigateToNewStoreScreen
import com.igrocery.overpriced.presentation.newstore.newStoreScreen
import com.igrocery.overpriced.presentation.productdetail.navigateToProductDetailScreen
import com.igrocery.overpriced.presentation.productdetail.productDetailScreen
import com.igrocery.overpriced.presentation.productlist.navigateToProductListScreen
import com.igrocery.overpriced.presentation.productlist.productListScreen
import com.igrocery.overpriced.presentation.searchproduct.navigateToSearchProductScreen
import com.igrocery.overpriced.presentation.searchproduct.searchProductScreen
import com.igrocery.overpriced.presentation.selectcurrency.navigateToSelectCurrencyScreen
import com.igrocery.overpriced.presentation.selectcurrency.selectCurrencyScreen
import com.igrocery.overpriced.presentation.settings.*
import com.igrocery.overpriced.presentation.storepricedetail.navigateToStorePriceDetailScreen
import com.igrocery.overpriced.presentation.storepricedetail.storePriceDetailScreen
import com.igrocery.overpriced.presentation.ui.theme.AppTheme
import com.igrocery.overpriced.shared.Logger

@Suppress("unused")
private val log = Logger { }

private object NavRoutes {
    const val SettingsRoute = "SettingsRoute"
}

@OptIn(ExperimentalAnimationApi::class)
@RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
@Composable
fun App() {
    AppTheme {
        // main app nav controller
        val navController = rememberAnimatedNavController()

        // nav controller for the bottom nav bar
        val bottomNavController = rememberAnimatedNavController()

        val animationSpec: FiniteAnimationSpec<Float> =
            spring(stiffness = Spring.StiffnessMediumLow)
        AnimatedNavHost(
            navController = navController,
            startDestination = "mainBottomNavigation",
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
            navGraph(
                navController = navController,
                bottomNavController = bottomNavController,
            )
        }
    }
}

private fun NavGraphBuilder.navGraph(
    navController: NavHostController,
    bottomNavController: NavHostController,
) {
    mainBottomNavigationScreen(
        bottomNavController = bottomNavController,
        navigateToSettings = { navController.navigateToSettingsScreen() },
        navigateToEditGroceryList = { navController.navigateToEditGroceryListScreen(it) },
        navigateToSearchProduct = { navController.navigateToSearchProductScreen() },
        navigateToProductList = { navController.navigateToProductListScreen(it) },
        navigateToNewPrice = { navController.navigateToNewPriceScreen() },
    )
    editGroceryListScreen(
        navigateUp = { navController.navigateUp() }
    )
    productListScreen(
        navigateUp = { navController.navigateUp() },
        navigateToSearchProduct = { navController.navigateToSearchProductScreen() },
        navigateToEditCategory = { navController.navigateToEditCategoryScreen(it) },
        navigateToProductDetail = { navController.navigateToProductDetailScreen(it) }
    )
    searchProductScreen(
        navigateUp = { navController.navigateUp() },
        navigateToProductDetails = { navController.navigateToProductDetailScreen(it) }
    )
    newPriceScreen(
        navigateUp = { navController.navigateUp() },
        navigateToNewCategory = { navController.navigateToNewCategoryScreen() },
        navigateToEditCategory = { navController.navigateToEditCategoryScreen(it.id) },
        navigateToNewStore = { navController.navigateToNewStoreScreen() },
        navigateToEditStore = { navController.navigateToEditStoreScreen(it) },
    )
    newCategoryScreen(
        navigateUp = { navController.navigateUp() },
        navigateDone = {
            navController.previousBackStackEntry?.savedStateHandle?.set(
                NewCategory_Result_CategoryId,
                it.value
            ) ?: throw IllegalStateException("NewCategory result is not received.")
            navController.navigateUp()
        }
    )
    editCategoryScreen(
        navigateUp = { navController.navigateUp() },
        navigateDone = {
            navController.previousBackStackEntry?.savedStateHandle?.set(
                EditCategory_Result_CategoryId,
                it.value
            ) ?: throw IllegalStateException("EditCategory result is not received.")
            navController.navigateUp()
        }
    )
    newStoreScreen(
        navigateUp = { navController.navigateUp() },
        navigateDone = {
            navController.previousBackStackEntry?.savedStateHandle?.set(
                NewStore_Result_StoreId,
                it.value
            ) ?: throw IllegalStateException("NewStore result is not received.")
            navController.navigateUp()
        }
    )
    editStoreScreen(
        navigateUp = { navController.navigateUp() },
        navigateDone = {
            navController.previousBackStackEntry?.savedStateHandle?.set(
                EditStore_Result_StoreId,
                it.value
            ) ?: throw IllegalStateException("EditStore result is not received.")
            navController.navigateUp()
        }
    )
    productDetailScreen(
        navigateUp = { navController.navigateUp() },
        navigateToStorePriceDetail = { productId, storeId ->
            navController.navigateToStorePriceDetailScreen(productId, storeId)
        }
    )
    storePriceDetailScreen(
        navigateUp = { navController.navigateUp() },
    )

    settingsGraph(navController)
}

private fun NavGraphBuilder.settingsGraph(navController: NavHostController) {
    navigation(route = SettingsRoute, startDestination = Settings) {
        settingsScreen(
            navigateUp = { navController.navigateUp() },
            navigateToSelectCurrencyScreen = { navController.navigateToSelectCurrencyScreen() }
        )
        selectCurrencyScreen(
            navigateUp = { navController.navigateUp() }
        )
    }
}
