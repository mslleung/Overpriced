package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.igrocery.overpriced.presentation.newstore.*
import com.igrocery.overpriced.presentation.shared.UseDefaultBottomNavBarColourForSystemNavBarColor
import com.igrocery.overpriced.presentation.shared.UseDefaultStatusBarColor
import com.igrocery.overpriced.shared.Logger


@Suppress("unused")
private val log = Logger {}

@Composable
fun EditGroceryListScreen(
    newGroceryListViewModel: EditGroceryListScreenViewModel,
    navigateUp: () -> Unit,
    navigateDone: (newStoreId: Long) -> Unit,
) {
    log.debug("Composing EditGroceryListScreen")

    val snackbarHostState = remember { SnackbarHostState() }
    val state by rememberNewStoreScreenState()
    val storeMapState by rememberStoreGoogleMapState(context = LocalContext.current)
    MainContent(
        state = state,
        storeMapState = storeMapState,
        onCameraPositionChanged = {
            state.cameraPosition = it
        },
        onBackButtonClick = navigateUp,
        onSaveButtonClick = {
            state.isSaveDialogShown = true
        }
    )

    BackHandler {
        log.debug("EditGroceryListScreen: BackHandler")
        navigateUp()
    }
}

@Composable
private fun MainContent(
    viewModelState: EditGroceryListScreenViewModelState,
    state: EditGroceryListScreenStateHolder,
    onBackButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    UseDefaultStatusBarColor()
    UseDefaultBottomNavBarColourForSystemNavBarColor()

    Scaffold(

    ) {

    }
}

