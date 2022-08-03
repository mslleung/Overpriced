package com.igrocery.overpriced.presentation.editcategory

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon
import com.igrocery.overpriced.presentation.editcategory.EditCategoryScreenViewModel.UpdateCategoryResult
import com.igrocery.overpriced.presentation.shared.BackButton
import com.igrocery.overpriced.presentation.shared.ConfirmDeleteDialog
import com.igrocery.overpriced.presentation.shared.DeleteButton
import com.igrocery.overpriced.presentation.shared.SaveButton
import com.igrocery.overpriced.shared.Logger
import com.ireceipt.receiptscanner.presentation.R
import com.skydoves.landscapist.glide.GlideImage

@Suppress("unused")
private val log = Logger { }

@Composable
fun EditCategoryScreen(
    categoryId: Long,
    viewModel: EditCategoryScreenViewModel,
    navigateUp: () -> Unit,
    navigateDone: () -> Unit,
) {
    log.debug("Composing EditCategoryScreen")

    val systemUiController = rememberSystemUiController()
    val statusBarColor = MaterialTheme.colorScheme.surface
    val navBarColor = MaterialTheme.colorScheme.surface
    SideEffect {
        systemUiController.setStatusBarColor(
            statusBarColor,
            transformColorForLightContent = { color -> color })
        systemUiController.setNavigationBarColor(
            navBarColor,
            navigationBarContrastEnforced = false,
            transformColorForLightContent = { color -> color })
    }

    val state by rememberEditCategoryScreenState()
    MainLayout(
        updateCategoryResult = viewModel.updateCategoryResult,
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
        LaunchedEffect(key1 = Unit) {
            viewModel.setCategoryId(categoryId)
            viewModel.categoryFlow
                .collect {
                    if (it != null) {
                        state.categoryName = it.name
                        state.categoryIcon = it.icon
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
        if (result is UpdateCategoryResult.Success) {
            navigateDone()
        }
    }

    BackHandler {
        navigateUp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainLayout(
    updateCategoryResult: UpdateCategoryResult?,
    state: EditCategoryScreenStateHolder,
    onBackButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onSaveButtonClick: () -> Unit,
) {
    val topBarScrollState = rememberTopAppBarState()
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topBarScrollState)
    Scaffold(
        topBar = {
            SmallTopAppBar(
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
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { scaffoldPadding ->
        // do not place the text field as an item in the lazy grid, when it is scrolled off-screen,
        // focus will be lost and keyboard will be hidden, which is weird to the user
        Column(
            modifier = Modifier
                .padding(scaffoldPadding)
                .padding(horizontal = 12.dp)
                .fillMaxWidth() // no nested scroll
        ) {
            CategoryNameTextField(
                categoryName = state.categoryName,
                onCategoryNameChanged = { state.categoryName = it },
                isError = updateCategoryResult is UpdateCategoryResult.Error,
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
                selectedCategoryIcon = state.categoryIcon,
                onCategoryIconSelected = { state.categoryIcon = it },
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
                    .fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun CategoryNameTextField(
    categoryName: String,
    onCategoryNameChanged: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        OutlinedTextField(
            value = categoryName,
            onValueChange = {
                onCategoryNameChanged(it.take(100))
            },
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true,
            label = {
                Text(text = stringResource(id = R.string.new_category_name_label))
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
            }),
            isError = isError
        )

        AnimatedVisibility(visible = isError) {
            Text(
                text = stringResource(id = R.string.new_category_name_empty_error_text),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun CategoryIconHeader(
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(id = R.string.new_category_icon_header),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Composable
private fun CategoryIconGrid(
    selectedCategoryIcon: CategoryIcon,
    onCategoryIconSelected: (CategoryIcon) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(60.dp),
        modifier = modifier,
    ) {
        items(
            items = CategoryIcon.values(),
            key = { it.ordinal },
        ) {
            Button(
                onClick = {
                    onCategoryIconSelected(it)
                },
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                border = if (selectedCategoryIcon == it) {
                    BorderStroke(2.dp, SolidColor(MaterialTheme.colorScheme.primary))
                } else {
                    null
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                GlideImage(
                    imageModel = it.iconRes,
                    modifier = Modifier.size(40.dp),
                    contentDescription = stringResource(id = R.string.new_category_icon_content_description),
                    contentScale = ContentScale.Fit,
                    previewPlaceholder = it.iconRes
                )
            }
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    MainLayout(
        updateCategoryResult = null,
        state = EditCategoryScreenStateHolder(),
        onBackButtonClick = {},
        onDeleteButtonClick = {},
        onSaveButtonClick = {},
    )
}
