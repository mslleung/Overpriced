package com.igrocery.overpriced.presentation.mainnavigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.mainnavigation.BottomNavDestinations.CategoryList
import com.igrocery.overpriced.presentation.mainnavigation.BottomNavDestinations.GroceryList
import com.igrocery.overpriced.presentation.mainnavigation.categorylist.CategoryListScreen
import com.igrocery.overpriced.presentation.mainnavigation.categorylist.CategoryListScreenViewModel

private object BottomNavDestinations {

    const val GroceryList = "groceryList"

    const val CategoryList = "categoryList"

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainBottomNavigationScreen(
    bottomNavController: NavHostController,

    // forwarded navigation from ShoppingList

    // forwarded navigation from CategoryList
    navigateToSettings: () -> Unit,
    navigateToSearchProduct: () -> Unit,
    navigateToProductList: (Category?) -> Unit,
    navigateToNewPrice: () -> Unit,
) {
    Scaffold(
        bottomBar = {
            val currentBackStackEntry by bottomNavController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry?.destination?.route

            NavigationBar {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_shopping_cart_24),
                            contentDescription = stringResource(id = R.string.grocery_lists_bottom_nav_content_description),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(text = stringResource(id = R.string.grocery_lists_bottom_nav_label)) },
                    selected = currentRoute == GroceryList,
                    onClick = {
                        if (currentRoute != GroceryList) {
                            bottomNavController.navigate(GroceryList)
                        }
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_attach_money_24),
                            contentDescription = stringResource(id = R.string.category_list_bottom_nav_content_description),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(text = stringResource(id = R.string.category_list_bottom_nav_label)) },
                    selected = currentRoute == CategoryList,
                    onClick = {
                        if (currentRoute != CategoryList) {
                            bottomNavController.navigate(CategoryList)
                        }
                    }
                )
            }
        },
        contentWindowInsets = WindowInsets.navigationBars
    ) {
        val animationSpec: FiniteAnimationSpec<Float> =
            spring(stiffness = Spring.StiffnessMediumLow)
        AnimatedNavHost(
            navController = bottomNavController,
            startDestination = CategoryList,
            enterTransition = {
                fadeIn(animationSpec) + scaleIn(
                    animationSpec,
                    initialScale = 0.8f
                )
            },
            exitTransition = {
                fadeOut(animationSpec) + scaleOut(
                    animationSpec,
                    targetScale = 0.8f
                )
            },
            popEnterTransition = {
                fadeIn(animationSpec) + scaleIn(
                    animationSpec,
                    initialScale = 0.8f
                )
            },
            popExitTransition = {
                fadeOut(animationSpec) + scaleOut(
                    animationSpec,
                    targetScale = 0.8f
                )
            },
            modifier = Modifier
                .padding(it)
        ) {
            composable(GroceryList) {

            }
            composable(CategoryList) {
                val categoryListScreenViewModel = hiltViewModel<CategoryListScreenViewModel>()

                CategoryListScreen(
                    categoryListScreenViewModel = categoryListScreenViewModel,
                    navigateToSettings = navigateToSettings,
                    navigateToSearchProduct = navigateToSearchProduct,
                    navigateToProductList = navigateToProductList,
                    navigateToNewPrice = navigateToNewPrice,
                )
            }
        }
    }
}
