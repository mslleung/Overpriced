package com.igrocery.overpriced.presentation.mainnavigation.grocerylist

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.shared.UseDefaultBottomNavBarColourForSystemNavBarColor
import com.igrocery.overpriced.presentation.shared.UseDefaultStatusBarColor
import com.igrocery.overpriced.shared.Logger

@Suppress("unused")
private val log = Logger { }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryListScreen(
    topBarScrollBehavior: TopAppBarScrollBehavior,
    groceryListScreenViewModel: GroceryListScreenViewModel,
    modifier: Modifier = Modifier,
) {
    log.debug("Composing GroceryListScreen")

    val state by rememberGroceryListScreenState()
    MainContent(
        topBarScrollBehavior = topBarScrollBehavior,
        viewModelState = groceryListScreenViewModel,
        state = state,
        onNewGroceryListClick = { /*TODO*/ })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    topBarScrollBehavior: TopAppBarScrollBehavior,
    viewModelState: GroceryListScreenViewModelState,
    state: GroceryListScreenStateHolder,
    onNewGroceryListClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    UseDefaultStatusBarColor()
    UseDefaultBottomNavBarColourForSystemNavBarColor()

    Scaffold(
        floatingActionButton = {
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
                onClick = onNewGroceryListClick,
            )
        },
        contentWindowInsets = WindowInsets.ime,
        modifier = modifier,
    ) {


    }
}
