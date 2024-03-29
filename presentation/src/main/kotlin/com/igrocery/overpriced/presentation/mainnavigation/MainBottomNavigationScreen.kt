package com.igrocery.overpriced.presentation.mainnavigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.GroceryListId
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.editgrocerylist.NewGroceryListNameDialog
import com.igrocery.overpriced.presentation.editgrocerylist.rememberGroceryListNameDialogState
import com.igrocery.overpriced.presentation.mainnavigation.categorylist.CategoryList
import com.igrocery.overpriced.presentation.mainnavigation.categorylist.categoryListScreen
import com.igrocery.overpriced.presentation.mainnavigation.categorylist.navigateToCategoryListScreen
import com.igrocery.overpriced.presentation.mainnavigation.grocerylist.*
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.presentation.shared.ifLoaded
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Suppress("unused")
private val log = Logger { }

private const val BottomNavRoute = "bottomNavRoute"

@Composable
fun MainBottomNavigationScreen(
    mainBottomNavigationScreenViewModel: MainBottomNavigationScreenViewModel,
    navigateToSettings: () -> Unit,

    // forwarded navigation from ShoppingList
    navigateToEditGroceryList: (GroceryListId) -> Unit,

    // forwarded navigation from CategoryList
    navigateToSearchProduct: () -> Unit,
    navigateToProductList: (CategoryId?) -> Unit,
    navigateToNewPrice: () -> Unit,
) {
    log.debug("Composing MainBottomNavigationScreen")

    val state by rememberMainBottomNavigationScreenState()
    val groceryListLazyListState = rememberLazyListState()
    MainContent(
        viewModelState = mainBottomNavigationScreenViewModel,
        state = state,
        groceryListLazyListState = groceryListLazyListState,
        navigateToSettings = navigateToSettings,
        navigateToEditGroceryList = navigateToEditGroceryList,
        navigateToNewPrice = navigateToNewPrice,
        navigateToSearchProduct = navigateToSearchProduct,
        navigateToProductList = navigateToProductList
    )

    if (state.isGroceryListNameDialogShown) {
        val groceryListNameDialogState by rememberGroceryListNameDialogState()
        NewGroceryListNameDialog(
            state = groceryListNameDialogState,
            onConfirm = {
                state.isGroceryListNameDialogShown = false
                mainBottomNavigationScreenViewModel.createNewGroceryList(
                    groceryListNameDialogState.groceryListName.text
                )
            },
            onDismiss = { state.isGroceryListNameDialogShown = false }
        )
    }

    val createNewGroceryListResult =
        mainBottomNavigationScreenViewModel.createNewGroceryListResultState
    LaunchedEffect(createNewGroceryListResult) {
        if (createNewGroceryListResult is LoadingState.Success) {
            // assume the newly created list is placed at the top
            groceryListLazyListState.scrollToItem(0)
        }
    }

    BackHandler(enabled = state.isGroceryListNameDialogShown) {
        log.debug("MainBottomNavigationScreen: BackHandler")
        if (state.isGroceryListNameDialogShown) {
            state.isGroceryListNameDialogShown = false
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MainContent(
    viewModelState: MainBottomNavigationScreenViewModelState,
    state: MainBottomNavigationScreenStateHolder,
    groceryListLazyListState: LazyListState,
    navigateToSettings: () -> Unit,
    navigateToEditGroceryList: (GroceryListId) -> Unit,
    navigateToNewPrice: () -> Unit,
    navigateToSearchProduct: () -> Unit,
    navigateToProductList: (CategoryId?) -> Unit
) {
    val topBarState = rememberTopAppBarState()
    val topBarScrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state = topBarState)

    val bottomNavController = rememberNavController()

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
                windowInsets = WindowInsets.statusBars,
                modifier = Modifier.padding(
                    WindowInsets.navigationBars.only(WindowInsetsSides.End)
                        .asPaddingValues()
                )
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
                            bottomNavController.navigateToGroceryListScreen {
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
                            bottomNavController.navigateToCategoryListScreen {
                                launchSingleTop = true
                                popUpTo(BottomNavRoute)
                            }
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            val groceryListCount by viewModelState.groceryListCountFlow.collectAsState()
            val currentBackStackEntry by bottomNavController.currentBackStackEntryAsState()
            when (currentBackStackEntry?.destination?.route) {
                GroceryList -> {
                    groceryListCount.ifLoaded {
                        if (it > 0) {
                            ExtendedFloatingActionButton(
                                text = { Text(text = stringResource(id = R.string.grocery_lists_new_grocery_list_fab_text)) },
                                icon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_baseline_add_24),
                                        contentDescription = stringResource(
                                            id = R.string.grocery_lists_new_grocery_list_fab_content_description
                                        ),
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                onClick = {
                                    state.isGroceryListNameDialogShown = true
                                },
                                modifier = Modifier.padding(
                                    WindowInsets.navigationBars.only(WindowInsetsSides.End)
                                        .asPaddingValues()
                                )
                            )
                        }
                    }
                }
                CategoryList -> {
                    if (state.shouldShowFabForCategoryListScreen) {
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
                            modifier = Modifier.padding(
                                WindowInsets.navigationBars.only(WindowInsetsSides.End)
                                    .asPaddingValues()
                            )
                        )
                    }
                }
            }
        },
    ) {
        // (workaround) we use another nav host instead of nested navigation so we can separate the
        // transition animation.
        val animationSpec: FiniteAnimationSpec<Float> =
            spring(stiffness = Spring.StiffnessMediumLow)
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavRoute,
            enterTransition = {
                fadeIn(animationSpec) + scaleIn(
                    animationSpec,
                    initialScale = 0.9f
                )
            },
            exitTransition = {
                ExitTransition.None
            },
            modifier = Modifier
                .padding(it)
        ) {
            navigation(route = BottomNavRoute, startDestination = GroceryList) {
                // view models are scoped to the route to prevent excessive recreation when changing
                // tabs (also helps to maintain fab states)
                groceryListScreen(
                    previousBackStackEntry = { bottomNavController.getBackStackEntry(BottomNavRoute) },
                    mainBottomNavigationState = { state },
                    topBarScrollBehavior = topBarScrollBehavior,
                    lazyListState = groceryListLazyListState,
                    navigateToEditGroceryList = navigateToEditGroceryList
                )
                categoryListScreen(
                    previousBackStackEntry = { bottomNavController.getBackStackEntry(BottomNavRoute) },
                    topBarScrollBehavior = topBarScrollBehavior,
                    navigateToSearchProduct = navigateToSearchProduct,
                    navigateToProductList = navigateToProductList,
                )
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

@Preview
@Composable
private fun DefaultPreview() {
    val viewModelState = object : MainBottomNavigationScreenViewModelState {
        override val groceryListCountFlow: StateFlow<LoadingState<Int>> =
            MutableStateFlow(LoadingState.Success(0))
        override var createNewGroceryListResultState: LoadingState<GroceryListId> =
            LoadingState.NotLoading()
    }

    val state by rememberMainBottomNavigationScreenState()
    MainContent(
        viewModelState = viewModelState,
        state = state,
        rememberLazyListState(),
        navigateToSettings = {},
        navigateToEditGroceryList = {},
        navigateToNewPrice = {},
        navigateToSearchProduct = {}
    ) {}
}
