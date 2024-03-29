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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.igrocery.overpriced.presentation.NavRoutes.SettingsRoute
import com.igrocery.overpriced.presentation.editcategory.*
import com.igrocery.overpriced.presentation.editgrocerylist.editGroceryListScreen
import com.igrocery.overpriced.presentation.editgrocerylist.navigateToEditGroceryListScreen
import com.igrocery.overpriced.presentation.editstore.editStoreScreen
import com.igrocery.overpriced.presentation.editstore.navigateToEditStoreScreen
import com.igrocery.overpriced.presentation.mainnavigation.MainBottomNavigation
import com.igrocery.overpriced.presentation.mainnavigation.mainBottomNavigationScreen
import com.igrocery.overpriced.presentation.newcategory.*
import com.igrocery.overpriced.presentation.newprice.navigateToNewPriceScreen
import com.igrocery.overpriced.presentation.newprice.newPriceScreen
import com.igrocery.overpriced.presentation.newstore.navigateToNewStoreScreen
import com.igrocery.overpriced.presentation.newstore.newStoreScreen
import com.igrocery.overpriced.presentation.productdetail.navigateToProductDetailScreen
import com.igrocery.overpriced.presentation.productdetail.productDetailScreen
import com.igrocery.overpriced.presentation.productlist.navigateToProductListScreen
import com.igrocery.overpriced.presentation.productlist.productListScreen
import com.igrocery.overpriced.presentation.searchproduct.navigateToSearchProductScreen
import com.igrocery.overpriced.presentation.searchproduct.searchProductScreen
import com.igrocery.overpriced.presentation.selectcategory.navigateToSelectCategoryScreen
import com.igrocery.overpriced.presentation.selectcategory.selectCategoryScreen
import com.igrocery.overpriced.presentation.selectcurrency.navigateToSelectCurrencyScreen
import com.igrocery.overpriced.presentation.selectcurrency.selectCurrencyScreen
import com.igrocery.overpriced.presentation.selectstore.navigateToSelectStoreScreen
import com.igrocery.overpriced.presentation.selectstore.selectStoreScreen
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

@RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
@Composable
fun App() {
    AppTheme {
        // main app nav controller
        val navController = rememberNavController()

        val animationSpec: FiniteAnimationSpec<Float> =
            spring(stiffness = Spring.StiffnessMediumLow)
        NavHost(
            navController = navController,
            startDestination = MainBottomNavigation,
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
            )
        }
    }
}

private fun NavGraphBuilder.navGraph(
    navController: NavHostController,
) {
    mainBottomNavigationScreen(
        navigateToSettings = { navController.navigateToSettingsScreen() },
        navigateToEditGroceryList = { navController.navigateToEditGroceryListScreen(it) },
        navigateToSearchProduct = { navController.navigateToSearchProductScreen() },
        navigateToProductList = { navController.navigateToProductListScreen(it) },
        navigateToNewPrice = { navController.navigateToNewPriceScreen() },
    )
    editGroceryListScreen(
        navigateUp = { navController.navigateUp() },
        navigateToSearchProduct = { navController.navigateToSearchProductScreen(it) }
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
        navigateToSelectCategory = { navController.navigateToSelectCategoryScreen(it) },
        navigateToSelectStore = { navController.navigateToSelectStoreScreen(it) },
    )
    newCategoryScreen(
        navigateUp = { navController.navigateUp() },
    )
    editCategoryScreen(
        navigateUp = { navController.navigateUp() },
    )
    selectCategoryScreen(
        navController = navController,
        navigateUp = { navController.navigateUp() },
        navigateToNewCategory = { navController.navigateToNewCategoryScreen() },
        navigateToEditCategory = { navController.navigateToEditCategoryScreen(it) }
    )
    newStoreScreen(
        navigateUp = { navController.navigateUp() },
    )
    editStoreScreen(
        navigateUp = { navController.navigateUp() },
    )
    selectStoreScreen(
        navController = navController,
        navigateUp = { navController.navigateUp() },
        navigateToNewStore = { navController.navigateToNewStoreScreen() },
        navigateToEditStore = { navController.navigateToEditStoreScreen(it) }
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
