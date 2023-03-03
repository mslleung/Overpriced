package com.igrocery.overpriced.presentation.selectcategory

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.igrocery.overpriced.domain.CategoryId

private const val SelectCategory = "selectCategory"
private const val SelectCategory_Arg_CategoryId = "categoryId"
private const val SelectCategory_With_Args =
    "$SelectCategory?$SelectCategory_Arg_CategoryId={$SelectCategory_Arg_CategoryId}"

fun NavController.navigateToSelectCategoryScreen(
    initialCategoryId: CategoryId? = null,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    var navString = SelectCategory
    if (initialCategoryId != null) {
        require(initialCategoryId.value > 0)
        navString += "?$SelectCategory_Arg_CategoryId={${initialCategoryId.value}}"
    }
    navigate(navString, builder)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.selectCategoryScreen(
    navController: NavController,
    navigateUp: () -> Unit,
    navigateToNewCategory: () -> Unit,
    navigateToEditCategory: (CategoryId) -> Unit,
) {
    composable(
        SelectCategory_With_Args,
        arguments = listOf(
            navArgument(SelectCategory_Arg_CategoryId) {
                type = NavType.LongType
                defaultValue = 0L
            },
        )
    ) { backStackEntry ->
        val selectCategoryViewModel = hiltViewModel<SelectCategoryScreenViewModel>()

        val prevBackStackEntry = navController.previousBackStackEntry
            ?: throw IllegalArgumentException("This destination expects a parent.")
        val selectCategoryResultViewModel =
            hiltViewModel<SelectCategoryScreenResultViewModel>(prevBackStackEntry)

        val args = SelectCategoryScreenArgs(backStackEntry)

        SelectCategoryScreen(
            args = args,
            viewModel = selectCategoryViewModel,
            navigateUp = navigateUp,
            navigateUpWithResults = {
                selectCategoryResultViewModel.setResult(Result(it))
                navigateUp()
            },
            navigateToNewCategory = navigateToNewCategory,
            navigateToEditCategory = navigateToEditCategory,
        )
    }
}

internal data class SelectCategoryScreenArgs(
    val selectedCategoryId: CategoryId? = null,
) {
    constructor(backStackEntry: NavBackStackEntry) :
            this(
                selectedCategoryId = backStackEntry.arguments?.getLong(SelectCategory_Arg_CategoryId)
                    .takeIf { it != 0L }
                    ?.let { CategoryId(it) },
            )

    constructor(savedStateHandle: SavedStateHandle) :
            this(
                selectedCategoryId = savedStateHandle.get<Long>(SelectCategory_Arg_CategoryId)
                    .takeIf { it != 0L }
                    ?.let { CategoryId(it) },
            )
}
