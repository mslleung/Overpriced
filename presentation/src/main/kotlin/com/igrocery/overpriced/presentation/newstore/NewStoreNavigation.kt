package com.igrocery.overpriced.presentation.newstore

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.composable

private const val NewStore = "newStore"

fun NavController.navigateToNewStoreScreen(
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(NewStore, builder)
}

fun NavGraphBuilder.newStoreScreen(
    navigateUp: () -> Unit,
) {
    composable(NewStore) {
        val newStoreViewModel = hiltViewModel<NewStoreScreenViewModel>()

        NewStoreScreen(
            newStoreViewModel = newStoreViewModel,
            navigateUp = navigateUp,
            navigateDone = {
                // TODO so far we don't have a need for returning the category id
                navigateUp()
            }
        )
    }
}
