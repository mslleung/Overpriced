package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.igrocery.overpriced.presentation.mainnavigation.SettingsButton
import com.igrocery.overpriced.presentation.newstore.*
import com.igrocery.overpriced.presentation.shared.*
import com.igrocery.overpriced.shared.Logger

@Suppress("unused")
private val log = Logger {}

@Composable
fun EditGroceryListScreen(
    editGroceryListViewModel: EditGroceryListScreenViewModel,
    navigateUp: () -> Unit,
) {
    log.debug("Composing EditGroceryListScreen")

    val snackbarHostState = remember { SnackbarHostState() }
    val state by rememberEditGroceryListScreenState()
    MainContent(
        viewModelState = editGroceryListViewModel,
        state = state,
        onBackButtonClick = navigateUp,
    )

    BackHandler {
        log.debug("EditGroceryListScreen: BackHandler")
        navigateUp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    viewModelState: EditGroceryListScreenViewModelState,
    state: EditGroceryListScreenStateHolder,
    onBackButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topBarScrollState = rememberTopAppBarState()
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topBarScrollState)

    UseAnimatedFadeTopBarColorForStatusBarColor(topAppBarState = topBarScrollState)
    UseDefaultBottomNavBarColourForSystemNavBarColor()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    BackButton(
                        onClick = onBackButtonClick,
                        modifier = Modifier
                            .padding(14.dp)
                            .size(24.dp, 24.dp)
                    )
                },
                title = {
                    Text(text = stringResource(id = com.igrocery.overpriced.presentation.R.string.new_price_title))
                },
                actions = {
//                    SaveButton(
//                        onClick = onSaveButtonClick,
//                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, end = 10.dp)
//                    )
                },
                scrollBehavior = topBarScrollBehavior,
            )
        },
    ) {

    }
}

