package com.igrocery.overpriced.presentation.editstore

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.presentation.editcategory.*

private const val EditStore = "editStore"
private const val EditStore_Arg_StoreId = "storeId"
private const val EditStore_With_Args = "editStore/{$EditStore_Arg_StoreId}"

fun NavController.navigateToEditStoreScreen(
    storeId: StoreId,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    require(storeId.value > 0)
    navigate("$EditStore/${storeId.value}", builder)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.editStoreScreen(
    navigateUp: () -> Unit,
) {
    composable(
        EditStore_With_Args,
        arguments = listOf(navArgument(EditStore_Arg_StoreId) {
            type = NavType.LongType
        })
    ) { backStackEntry ->
        val editStoreScreenViewModel = hiltViewModel<EditStoreScreenViewModel>()

        val args = EditStoreScreenArgs(backStackEntry)

        EditStoreScreen(
            viewModel = editStoreScreenViewModel,
            navigateUp = navigateUp,
        )
    }
}

internal data class EditStoreScreenArgs(
    val storeId: StoreId,
) {
    constructor(backStackEntry: NavBackStackEntry) :
            this(
                storeId = backStackEntry.arguments?.getLong(EditStore_Arg_StoreId)
                    .takeIf { it != 0L }
                    ?.let { StoreId(it) }
                    ?: throw IllegalArgumentException("storeId should not be null")
            )

    constructor(savedStateHandle: SavedStateHandle) :
            this(
                storeId = savedStateHandle.get<Long>(EditStore_Arg_StoreId)
                    .takeIf { it != 0L }
                    ?.let { StoreId(it) }
                    ?: throw IllegalArgumentException("storeId should not be null")
            )
}

