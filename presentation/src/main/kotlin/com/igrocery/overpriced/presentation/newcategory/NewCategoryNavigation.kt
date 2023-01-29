package com.igrocery.overpriced.presentation.newcategory

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.presentation.editcategory.*

private const val NewCategory = "newCategory"
const val NewCategory_Result_CategoryId = "newCategoryResultCategoryId"

fun NavController.navigateToNewCategoryScreen(
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(NewCategory, builder)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.newCategoryScreen(
    navigateUp: () -> Unit,
    navigateDone: (CategoryId) -> Unit
) {
    composable(NewCategory) {
        val newCategoryScreenViewModel = hiltViewModel<NewCategoryScreenViewModel>()

        NewCategoryScreen(
            viewModel = newCategoryScreenViewModel,
            navigateUp = navigateUp,
            navigateDone = { navigateDone(it) }
        )
    }
}
