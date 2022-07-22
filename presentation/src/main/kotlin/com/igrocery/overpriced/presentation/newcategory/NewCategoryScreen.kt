package com.igrocery.overpriced.presentation.newcategory

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon
import com.igrocery.overpriced.presentation.newprice.NewPriceScreenStateHolder
import com.igrocery.overpriced.presentation.newprice.rememberNewPriceScreenState
import com.igrocery.overpriced.presentation.newstore.NewStoreScreenStateHolder
import com.igrocery.overpriced.presentation.shared.BackButton
import com.igrocery.overpriced.presentation.shared.CloseButton
import com.igrocery.overpriced.presentation.shared.SaveButton
import com.igrocery.overpriced.shared.Logger
import com.ireceipt.receiptscanner.presentation.R

@Suppress("unused")
private val log = Logger { }

@Composable
fun NewCategoryScreen(
    viewModel: NewCategoryScreenViewModel,
    navigateUp: () -> Unit,
    navigateDone: (Category) -> Unit,
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

        }
    )

    BackHandler {
        navigateUp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainLayout(
    createCategoryResult: NewCategoryScreenViewModel.CreateCategoryResult?,
    state: NewCategoryScreenStateHolder,
    onBackButtonClick: () -> Unit,
    onSaveButtonClick: () -> Unit,
) {
    val topBarScrollState = rememberTopAppBarScrollState()
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
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(scaffoldPadding)
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 12.dp)
                .fillMaxSize()
                .nestedScroll(topBarScrollBehavior.nestedScrollConnection),
        ) {
            CategoryNameTextField(
                createCategoryResult = createCategoryResult,
                state = state,
                modifier = Modifier
                    .padding(bottom = 12.dp)
            )

            Text(
                text = stringResource(id = R.string.new_category_icon_header),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(bottom = 10.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Adaptive(60.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items (
                    items = CategoryIcon.values(),
                    key = { it.ordinal }
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
                        Image(
                            painter = painterResource(id = it.iconRes),
                            contentDescription = stringResource(id = R.string.new_category_icon_content_description),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryNameTextField(
    createCategoryResult: NewCategoryScreenViewModel.CreateCategoryResult?,
    state: NewCategoryScreenStateHolder,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    OutlinedTextField(
        value = state.categoryName,
        onValueChange = {
            state.categoryName = it.take(100)
        },
        modifier = modifier
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
        isError = createCategoryResult is NewCategoryScreenViewModel.CreateCategoryResult.Error
    )
    if (state.isRequestingFirstFocus) {
        LaunchedEffect(key1 = Unit) {
            focusRequester.requestFocus()
        }
        state.isRequestingFirstFocus = false
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
