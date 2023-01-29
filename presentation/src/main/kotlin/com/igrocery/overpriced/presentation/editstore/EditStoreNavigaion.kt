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
const val EditStore_Result_StoreId = "editStoreResultStoreId"

fun NavController.navigateToEditStoreScreen(
    storeId: StoreId,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    require(storeId.value > 0)
    navigate("$EditStore/$storeId", builder)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.editStoreScreen(
    navigateUp: () -> Unit,
    navigateDone: (StoreId) -> Unit
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
            navigateDone = { navigateDone(args.storeId) },
        )
    }
}

internal class EditStoreScreenArgs(
    val storeId: StoreId,
) {
    constructor(backStackEntry: NavBackStackEntry) :
            this(
                storeId = StoreId(
                    backStackEntry.arguments?.getLong(EditStore_Arg_StoreId)
                        ?: throw IllegalArgumentException("storeId should not be null")
                )
            )

    constructor(savedStateHandle: SavedStateHandle) :
            this(
                storeId = savedStateHandle.get<StoreId>(EditStore_Arg_StoreId)
                    .takeIf { it?.value != 0L }
                    ?: throw IllegalArgumentException("storeId should not be null")
            )
}

