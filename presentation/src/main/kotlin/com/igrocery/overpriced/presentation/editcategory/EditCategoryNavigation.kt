package com.igrocery.overpriced.presentation.editcategory

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.igrocery.overpriced.domain.CategoryId

private const val EditCategory = "editCategory"
private const val EditCategory_Arg_CategoryId = "categoryId"
private const val EditCategory_With_Args = "editCategory/{$EditCategory_Arg_CategoryId}"

fun NavController.navigateToEditCategoryScreen(
    categoryId: CategoryId,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    require(categoryId.value > 0)
    navigate("$EditCategory/${categoryId.value}", builder)
}

fun NavGraphBuilder.editCategoryScreen(
    navigateUp: () -> Unit,
) {
    composable(
        EditCategory_With_Args,
        arguments = listOf(navArgument(EditCategory_Arg_CategoryId) {
            type = NavType.LongType
        })
    ) { backStackEntry ->
        val editCategoryViewModel = hiltViewModel<EditCategoryScreenViewModel>()

        val args = EditCategoryScreenArgs(backStackEntry)

        EditCategoryScreen(
            viewModel = editCategoryViewModel,
            navigateUp = navigateUp,
        )
    }
}

internal data class EditCategoryScreenArgs(
    val categoryId: CategoryId
) {
    constructor(backStackEntry: NavBackStackEntry) :
            this(
                categoryId = backStackEntry.arguments?.getLong(EditCategory_Arg_CategoryId)
                    .takeIf { it != 0L }
                    ?.let { CategoryId(it) }
                    ?: throw IllegalArgumentException("categoryId should not be null")
            )

    constructor(savedStateHandle: SavedStateHandle) :
            this(
                categoryId = savedStateHandle.get<Long>(EditCategory_Arg_CategoryId)
                    .takeIf { it != 0L }
                    ?.let { CategoryId(it) }
                    ?: throw IllegalArgumentException("categoryId should not be null")
            )
}
