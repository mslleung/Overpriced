package com.igrocery.overpriced.presentation.newcategory

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.igrocery.overpriced.presentation.editcategory.*

private const val NewCategory = "newCategory"

fun NavController.navigateToNewCategoryScreen(
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(NewCategory, builder)
}

@OptIn(ExperimentalAnimationApi::class)
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
