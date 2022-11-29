package com.igrocery.overpriced.presentation.editcategory

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.newcategory.CategoryIconGrid
import com.igrocery.overpriced.presentation.newcategory.CategoryIconHeader
import com.igrocery.overpriced.presentation.newcategory.CategoryNameTextField
import com.igrocery.overpriced.presentation.shared.*
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Suppress("unused")
private val log = Logger { }

@Composable
fun EditCategoryScreen(
    viewModel: EditCategoryScreenViewModel,
    navigateUp: () -> Unit,
    navigateDone: () -> Unit,
) {
    log.debug("Composing EditCategoryScreen")

    val state by rememberEditCategoryScreenState()
    MainLayout(
        viewModelState = viewModel,
        state = state,
        onBackButtonClick = navigateUp,
        onDeleteButtonClick = {
            state.isConfirmDeleteDialogShown = true
        },
        onSaveButtonClick = {
            viewModel.updateCategory(
                categoryName = state.categoryName,
                categoryIcon = state.categoryIcon
            )
        }
    )

    if (!state.isInitialized) {
        val categoryLoadState by viewModel.categoryFlow.collectAsState()
        categoryLoadState.let {
            if (it is LoadingState.Success) {
                val category = it.data ?: NoCategory
                LaunchedEffect(key1 = Unit) {
                    state.categoryName = category.name
                    state.categoryIcon = category.icon
                    state.isInitialized = true
                }
            }
        }
    }

    if (state.isConfirmDeleteDialogShown) {
        ConfirmDeleteDialog(
            onDismiss = {
                state.isConfirmDeleteDialogShown = false
            },
            onConfirm = {
                state.isConfirmDeleteDialogShown = false
                navigateUp()

                viewModel.deleteCategory()
            },
            messageText = stringResource(id = R.string.edit_category_delete_dialog_message)
        )
    }

    LaunchedEffect(key1 = viewModel.updateCategoryResult) {
        val result = viewModel.updateCategoryResult
        if (result is LoadingState.Success) {
            navigateDone()
        }
    }

    BackHandler {
        log.debug("EditCategoryScreen: BackHandler")
        navigateUp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainLayout(
    viewModelState: EditCategoryScreenViewModelState,
    state: EditCategoryScreenStateHolder,
    onBackButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onSaveButtonClick: () -> Unit,
) {
    val topBarScrollState = rememberTopAppBarState()
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topBarScrollState)

    UseAnimatedFadeTopBarColorForStatusBarColor(topBarScrollState)
    UseDefaultBottomNavBarColourForSystemNavBarColor()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    BackButton(
                        onClick = onBackButtonClick,
                        modifier = Modifier
                            .padding(14.dp)
                            .size(24.dp, 24.dp)
                    )
                },
                title = {
                    Text(text = stringResource(id = R.string.edit_category_title))
                },
                actions = {
                    DeleteButton(
                        onClick = onDeleteButtonClick,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(24.dp),
                    )
                    SaveButton(
                        onClick = onSaveButtonClick,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, end = 10.dp),
                    )
                },
                scrollBehavior = topBarScrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { scaffoldPadding ->
        // do not place the text field as an item in the lazy grid, when it is scrolled off-screen,
        // focus will be lost and keyboard will be hidden, which is weird to the user
        Column(
            modifier = Modifier
                .padding(scaffoldPadding)
                .padding(horizontal = 12.dp)
                .fillMaxWidth() // no nested scroll
        ) {
            val focusRequester = remember { FocusRequester() }
            CategoryNameTextField(
                categoryName = state.categoryName,
                onCategoryNameChange = { state.categoryName = it.take(100) },
                isError = viewModelState.updateCategoryResult is LoadingState.Error,
                focusRequester = focusRequester,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .fillMaxWidth()
            )

            CategoryIconHeader(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
            )

            CategoryIconGrid(
                selectedCategoryIcon = { state.categoryIcon },
                onCategoryIconSelected = { state.categoryIcon = it },
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    val viewModelState = object : EditCategoryScreenViewModelState {
        override val categoryFlow: StateFlow<LoadingState<Category?>> =
            MutableStateFlow(LoadingState.Success(null))
        override val updateCategoryResult: LoadingState<Unit> = LoadingState.NotLoading()
    }

    MainLayout(
        viewModelState = viewModelState,
        state = EditCategoryScreenStateHolder(),
        onBackButtonClick = {},
        onDeleteButtonClick = {},
        onSaveButtonClick = {},
    )
}
