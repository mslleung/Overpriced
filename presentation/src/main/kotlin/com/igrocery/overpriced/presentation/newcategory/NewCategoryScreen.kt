package com.igrocery.overpriced.presentation.newcategory

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.igrocery.overpriced.domain.productpricehistory.models.Category
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
        state = state
    )

    BackHandler {
        navigateUp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainLayout(
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
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(topBarScrollBehavior.nestedScrollConnection),
        ) {
            val focusRequester = remember { FocusRequester() }
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
                    Text(text = stringResource(id = R.string.new_price_product_name_label))
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                isError =
            )
            if (state.isRequestingFirstFocus) {
                LaunchedEffect(key1 = Unit) {
                    focusRequester.requestFocus()
                }
                state.isRequestingFirstFocus = false
            }
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    MainLayout(
        state = NewCategoryScreenStateHolder(),
        onBackButtonClick = {},
        onSaveButtonClick = {}
    )
}
