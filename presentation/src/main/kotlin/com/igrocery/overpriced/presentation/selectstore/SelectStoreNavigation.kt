package com.igrocery.overpriced.presentation.selectstore

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.igrocery.overpriced.domain.StoreId

private const val SelectStore = "selectStore"
private const val SelectStore_Arg_StoreId = "storeId"
private const val SelectStore_With_Args =
    "$SelectStore?$SelectStore_Arg_StoreId={$SelectStore_Arg_StoreId}"

fun NavController.navigateToSelectStoreScreen(
    selectedStoreId: StoreId? = null,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    var navString = SelectStore
    if (selectedStoreId != null) {
        require(selectedStoreId.value > 0)
        navString += "?$SelectStore_Arg_StoreId=${selectedStoreId.value}"
    }
    navigate(navString, builder)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.selectStoreScreen(
    navController: NavController,
    navigateUp: () -> Unit,
    navigateToNewStore: () -> Unit,
    navigateToEditStore: (StoreId) -> Unit,
) {
    composable(
        SelectStore_With_Args,
        arguments = listOf(
            navArgument(SelectStore_Arg_StoreId) {
                type = NavType.LongType
                defaultValue = 0L
            },
        )
    ) { backStackEntry ->
        val selectStoreViewModel = hiltViewModel<SelectStoreScreenViewModel>()

        val prevBackStackEntry = navController.previousBackStackEntry
            ?: throw IllegalArgumentException("This destination expects a parent.")
        val selectStoreResultViewModel =
            hiltViewModel<SelectStoreScreenResultViewModel>(prevBackStackEntry)

        val args = SelectStoreScreenArgs(backStackEntry)

        SelectStoreScreen(
            args = args,
            viewModel = selectStoreViewModel,
            navigateUp = navigateUp,
            navigateUpWithResults = {
                selectStoreResultViewModel.setResult(Result(it))
                navigateUp()
            },
            navigateToNewStore = navigateToNewStore,
            navigateToEditStore = navigateToEditStore,
        )
    }
}

internal data class SelectStoreScreenArgs(
    val selectedStoreId: StoreId? = null,
) {
    constructor(backStackEntry: NavBackStackEntry) :
            this(
                selectedStoreId = backStackEntry.arguments?.getLong(SelectStore_Arg_StoreId)
                    .takeIf { it != 0L }
                    ?.let { StoreId(it) },
            )

    constructor(savedStateHandle: SavedStateHandle) :
            this(
                selectedStoreId = savedStateHandle.get<Long>(SelectStore_Arg_StoreId)
                    .takeIf { it != 0L }
                    ?.let { StoreId(it) },
            )
}
