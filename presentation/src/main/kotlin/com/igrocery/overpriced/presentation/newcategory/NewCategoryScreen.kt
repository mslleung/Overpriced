package com.igrocery.overpriced.presentation.newcategory

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.shared.*
import com.igrocery.overpriced.shared.Logger

@Suppress("unused")
private val log = Logger { }

@Composable
fun NewCategoryScreen(
    viewModel: NewCategoryScreenViewModel,
    navigateUp: () -> Unit,
    navigateDone: (categoryId: Long) -> Unit,
) {
    log.debug("Composing NewCategoryScreen")

    val state by rememberNewCategoryScreenState()
    MainLayout(
        viewModelState = viewModel,
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
        if (result is LoadingState.Success) {
            navigateDone(result.data)
        }
    }

    BackHandler {
        log.debug("Composing NewCategoryScreen: BackHandler")
        navigateUp()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun MainLayout(
    viewModelState: NewCategoryScreenViewModelState,
    state: NewCategoryScreenStateHolder,
    onBackButtonClick: () -> Unit,
    onSaveButtonClick: () -> Unit,
) {
    val topBarScrollState = rememberTopAppBarState()
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topBarScrollState)

    UseAnimatedFadeTopBarColorForStatusBarColor(topBarScrollState)
    UseDefaultSystemNavBarColor()

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
                    Text(text = stringResource(id = R.string.new_category_title))
                },
                actions = {
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
            val keyboardController = LocalSoftwareKeyboardController.current
            CategoryNameTextField(
                categoryName = state.categoryName,
                onCategoryNameChange = {
                    state.categoryName = it.take(100)
                },
                isError = viewModelState.createCategoryResult is LoadingState.Error,
                focusRequester = focusRequester,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .fillMaxWidth()
            )
            if (state.isRequestingFirstFocus) {
                LaunchedEffect(key1 = Unit) {
                    focusRequester.requestFocus()
                    keyboardController?.show()  // somehow the keyboard doesn't show up despite getting focus
                }
                state.isRequestingFirstFocus = false
            }

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

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CategoryNameTextField(
    categoryName: String,
    onCategoryNameChange: (String) -> Unit,
    isError: Boolean,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        OutlinedTextField(
            value = categoryName,
            onValueChange = onCategoryNameChange,
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
fun CategoryIconHeader(
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
fun CategoryIconGrid(
    selectedCategoryIcon: () -> CategoryIcon,   // delayed read
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
                border = if (selectedCategoryIcon() == it) {
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
                Image(
                    painter = painterResource(id = it.iconRes),
                    modifier = Modifier.size(40.dp),
                    contentDescription = stringResource(id = R.string.new_category_icon_content_description)
                )
            }
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    val viewModelState = object : NewCategoryScreenViewModelState {
        override val createCategoryResult: LoadingState<Long> = LoadingState.NotLoading()
    }

    MainLayout(
        viewModelState = viewModelState,
        state = NewCategoryScreenStateHolder(),
        onBackButtonClick = {},
        onSaveButtonClick = {}
    )
}
