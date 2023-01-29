package com.igrocery.overpriced.presentation.newstore

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.presentation.editcategory.*

private const val NewStore = "newStore"
const val NewStore_Result_StoreId = "newStoreResultStoreId"

fun NavController.navigateToNewStoreScreen(
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(NewStore, builder)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.newStoreScreen(
    navigateUp: () -> Unit,
    navigateDone: (StoreId) -> Unit
) {
    composable(NewStore) {
        val newStoreViewModel = hiltViewModel<NewStoreScreenViewModel>()

        NewStoreScreen(
            newStoreViewModel = newStoreViewModel,
            navigateUp = navigateUp,
            navigateDone = navigateDone
        )
    }
}
