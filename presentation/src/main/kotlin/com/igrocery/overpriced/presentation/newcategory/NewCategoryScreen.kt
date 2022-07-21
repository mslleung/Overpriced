package com.igrocery.overpriced.presentation.newcategory

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
