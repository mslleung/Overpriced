package com.igrocery.overpriced.presentation.selectcategory

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.domain.productpricehistory.models.Category

private const val SelectCategory = "selectCategory"
const val SelectCategory_Arg_CategoryId = "categoryId"
private const val SelectCategory_With_Args =
    "$SelectCategory?$SelectCategory_Arg_CategoryId={$SelectCategory_Arg_CategoryId}"

fun NavController.navigateToSelectCategoryScreen(
    selectedCategoryId: CategoryId? = null,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    var navString = SelectCategory
    if (selectedCategoryId != null) {
        require(selectedCategoryId.value > 0)
        navString += "?$SelectCategory_Arg_CategoryId={${selectedCategoryId.value}}"
    }
    navigate(navString, builder)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.selectCategoryScreen(
    navigateUp: () -> Unit,
    navigateUpWithResults: (CategoryId) -> Unit,
    navigateToNewCategory: () -> Unit,
    navigateToEditCategory: (Category) -> Unit,
    navigateToNewStore: () -> Unit,
    navigateToEditStore: (StoreId) -> Unit,
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
        val newPriceViewModel = hiltViewModel<SelectCategoryScreenViewModel>()

        val args = SelectCategoryScreenArgs(backStackEntry)

        NewPriceScreen(
            savedStateHandle = backStackEntry.savedStateHandle,
            newPriceScreenViewModel = newPriceViewModel,
            navigateUp = navigateUp,
            navigateToNewCategory = navigateToNewCategory,
            navigateToEditCategory = navigateToEditCategory,
            navigateToNewStore = navigateToNewStore,
            navigateToEditStore = navigateToEditStore,
        )
    }
}

internal class SelectCategoryScreenArgs(
    selectedCategoryId: CategoryId? = null,
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
