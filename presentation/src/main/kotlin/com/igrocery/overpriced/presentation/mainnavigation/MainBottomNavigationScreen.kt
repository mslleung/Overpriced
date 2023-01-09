package com.igrocery.overpriced.presentation.mainnavigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.mainnavigation.BottomNavDestinations.BottomNavRoute
import com.igrocery.overpriced.presentation.mainnavigation.BottomNavDestinations.CategoryList
import com.igrocery.overpriced.presentation.mainnavigation.BottomNavDestinations.GroceryList
import com.igrocery.overpriced.presentation.mainnavigation.categorylist.CategoryListScreen
import com.igrocery.overpriced.presentation.mainnavigation.categorylist.CategoryListScreenViewModel
import com.igrocery.overpriced.presentation.mainnavigation.grocerylist.GroceryListScreen
import com.igrocery.overpriced.presentation.mainnavigation.grocerylist.GroceryListScreenViewModel

private object BottomNavDestinations {

    const val BottomNavRoute = "bottomNavRoute"

    const val GroceryList = "groceryList"

    const val CategoryList = "categoryList"

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainBottomNavigationScreen(
    bottomNavController: NavHostController,
    navigateToSettings: () -> Unit,

    // forwarded navigation from ShoppingList

    // forwarded navigation from CategoryList
    navigateToSearchProduct: () -> Unit,
    navigateToProductList: (Category?) -> Unit,
    navigateToNewPrice: () -> Unit,
) {
    val topBarState = rememberTopAppBarState()
    val topBarScrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state = topBarState)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    // TODO replace with brand icon
                    Text(text = stringResource(id = R.string.app_name))
                },
                actions = {
                    SettingsButton(
                        onClick = navigateToSettings,
                        modifier = Modifier
                            .padding(14.dp)
                            .size(24.dp, 24.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
                scrollBehavior = topBarScrollBehavior,
                windowInsets = WindowInsets.statusBars
            )
        },
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
                            bottomNavController.navigate(GroceryList) {
                                launchSingleTop = true
                                popUpTo(BottomNavRoute)
                            }
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
                            bottomNavController.navigate(CategoryList) {
                                launchSingleTop = true
                                popUpTo(BottomNavRoute)
                            }
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            val currentBackStackEntry by bottomNavController.currentBackStackEntryAsState()
            when (currentBackStackEntry?.destination?.route) {
                GroceryList -> {
                    ExtendedFloatingActionButton(
                        text = {
                            Text(text = stringResource(id = R.string.grocery_lists_new_grocery_list_fab_text))
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_add_24),
                                contentDescription = stringResource(
                                    id = R.string.grocery_lists_new_grocery_list_fab_content_description
                                ),
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        onClick = {/*TODO*/ },
                    )
                }
                CategoryList -> {
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
                        onClick = navigateToNewPrice,
                    )
                }
            }
        },
    ) {
        val animationSpec: FiniteAnimationSpec<Float> =
            spring(stiffness = Spring.StiffnessMediumLow)
        AnimatedNavHost(
            navController = bottomNavController,
            startDestination = BottomNavRoute,
            enterTransition = {
                fadeIn(animationSpec) + scaleIn(
                    animationSpec,
                    initialScale = 0.9f
                )
            },
            exitTransition = {
                fadeOut(animationSpec)
            },
            modifier = Modifier
                .padding(it)
        ) {
            navigation(route = BottomNavRoute, startDestination = GroceryList) {
                composable(GroceryList) {
                    val groceryListScreenViewModel = hiltViewModel<GroceryListScreenViewModel>()

                    GroceryListScreen(
                        topBarScrollBehavior = topBarScrollBehavior,
                        groceryListScreenViewModel = groceryListScreenViewModel,

                        )
                }
                composable(CategoryList) {
                    val categoryListScreenViewModel = hiltViewModel<CategoryListScreenViewModel>()

                    CategoryListScreen(
                        topBarScrollBehavior = topBarScrollBehavior,
                        categoryListScreenViewModel = categoryListScreenViewModel,
                        navigateToSearchProduct = navigateToSearchProduct,
                        navigateToProductList = navigateToProductList,
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_settings_24),
            contentDescription = stringResource(R.string.settings_button_content_description)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
private fun DefaultPreview() {
    val bottomNavController = rememberAnimatedNavController()

    MainBottomNavigationScreen(
        bottomNavController = bottomNavController,
        navigateToSettings = {},
        navigateToSearchProduct = {},
        navigateToProductList = {},
        navigateToNewPrice = {}
    )
}
