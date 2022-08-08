package com.igrocery.overpriced.presentation.newcategory

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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import com.igrocery.overpriced.presentation.newcategory.NewCategoryScreenViewModel.CreateCategoryResult
import com.igrocery.overpriced.presentation.shared.BackButton
import com.igrocery.overpriced.presentation.shared.SaveButton
import com.igrocery.overpriced.shared.Logger
import com.igrocery.overpriced.presentation.R
import com.skydoves.landscapist.glide.GlideImage

@Suppress("unused")
private val log = Logger { }

@Composable
fun NewCategoryScreen(
    viewModel: NewCategoryScreenViewModel,
    navigateUp: () -> Unit,
    navigateDone: (categoryId: Long) -> Unit,
) {
    log.debug("Composing NewCategoryScreen")

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

    val state by rememberNewCategoryScreenState()
    MainLayout(
        createCategoryResult = viewModel.createCategoryResult,
        state = state,
        onBackButtonClick = navigateUp,
        onSaveButtonClick = {
            viewModel.createCategory(
                categoryName = state.categoryName,
                categoryIcon = state.categoryIcon
            )
        }
    )

    LaunchedEffect(key1 = viewModel.createCategoryResult) {
        val result = viewModel.createCategoryResult
        if (result is CreateCategoryResult.Success) {
            navigateDone(result.categoryId)
        }
    }

    BackHandler {
        navigateUp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainLayout(
    createCategoryResult: CreateCategoryResult?,
    state: NewCategoryScreenStateHolder,
    onBackButtonClick: () -> Unit,
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
                    Text(text = stringResource(id = R.string.new_category_title))
                },
                actions = {
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
                createCategoryResult = createCategoryResult,
                state = state,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .fillMaxWidth()
            )

            Text(
                text = stringResource(id = R.string.new_category_icon_header),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
            )

            LazyVerticalGrid(
                columns = GridCells.Adaptive(60.dp),
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
                    .fillMaxSize(),
            ) {
                items(
                    items = CategoryIcon.values(),
                    key = { it.ordinal },
                ) {
                    Button(
                        onClick = {
                            state.categoryIcon = it
                        },
                        modifier = Modifier.size(60.dp),
                        shape = CircleShape,
                        border = if (state.categoryIcon == it) {
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
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun CategoryNameTextField(
    createCategoryResult: CreateCategoryResult?,
    state: NewCategoryScreenStateHolder,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current
        OutlinedTextField(
            value = state.categoryName,
            onValueChange = {
                state.categoryName = it.take(100)
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
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
            isError = createCategoryResult is CreateCategoryResult.Error
        )
        if (state.isRequestingFirstFocus) {
            LaunchedEffect(key1 = Unit) {
                focusRequester.requestFocus()
                keyboardController?.show()  // somehow the keyboard doesn't show up despite getting focus
            }
            state.isRequestingFirstFocus = false
        }

        AnimatedVisibility(visible = createCategoryResult is CreateCategoryResult.Error) {
            Text(
                text = stringResource(id = R.string.new_category_name_empty_error_text),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    MainLayout(
        createCategoryResult = null,
        state = NewCategoryScreenStateHolder(),
        onBackButtonClick = {},
        onSaveButtonClick = {}
    )
}
