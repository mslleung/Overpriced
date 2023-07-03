package com.igrocery.overpriced.presentation.newcategory

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.composable

private const val NewCategory = "newCategory"

fun NavController.navigateToNewCategoryScreen(
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(NewCategory, builder)
}

fun NavGraphBuilder.newCategoryScreen(
    navigateUp: () -> Unit,
) {
    composable(NewCategory) {
        val newCategoryScreenViewModel = hiltViewModel<NewCategoryScreenViewModel>()

        NewCategoryScreen(
            viewModel = newCategoryScreenViewModel,
            navigateUp = navigateUp,
            navigateDone = {
                // TODO so far we don't have a need for returning the category id
                navigateUp()
            }
        )
    }
}
