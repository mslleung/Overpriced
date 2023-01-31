package com.igrocery.overpriced.presentation.editcategory

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.igrocery.overpriced.domain.CategoryId

private const val EditCategory = "editCategory"
private const val EditCategory_Arg_CategoryId = "categoryId"
private const val EditCategory_With_Args = "editCategory/{$EditCategory_Arg_CategoryId}"
const val EditCategory_Result_CategoryId = "editCategoryResultCategoryId"

fun NavController.navigateToEditCategoryScreen(
    categoryId: CategoryId,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    require(categoryId.value > 0)
    navigate("$EditCategory/$categoryId", builder)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.editCategoryScreen(
    navigateUp: () -> Unit,
    navigateDone: (categoryId: CategoryId) -> Unit
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
            navigateDone = { navigateDone(args.categoryId) }
        )
    }
}

internal class EditCategoryScreenArgs(
    val categoryId: CategoryId
) {
    constructor(backStackEntry: NavBackStackEntry) :
            this(
                categoryId = CategoryId(
                    backStackEntry.arguments?.getLong(EditCategory_Arg_CategoryId)
                        ?: throw IllegalArgumentException("categoryId should not be null")
                )
            )

    constructor(savedStateHandle: SavedStateHandle) :
            this(
                categoryId = savedStateHandle.get<CategoryId>(EditCategory_Arg_CategoryId)
                    .takeIf { it?.value != 0L }
                    ?: throw IllegalArgumentException("categoryId should not be null")
            )
}
