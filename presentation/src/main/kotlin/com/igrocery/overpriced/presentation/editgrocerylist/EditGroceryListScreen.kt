package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.newstore.*
import com.igrocery.overpriced.presentation.shared.*
import com.igrocery.overpriced.shared.Logger

@Suppress("unused")
private val log = Logger {}

@Composable
fun EditGroceryListScreen(
    editGroceryListViewModel: EditGroceryListScreenViewModel,
    navigateUp: () -> Unit,
) {
    log.debug("Composing EditGroceryListScreen")

    val snackbarHostState = remember { SnackbarHostState() }
    val state by rememberEditGroceryListScreenState()
    MainContent(
        viewModelState = editGroceryListViewModel,
        state = state,
        onBackButtonClick = navigateUp,
        onNewGroceryListItemClick = {}
    )

    BackHandler {
        log.debug("EditGroceryListScreen: BackHandler")
        navigateUp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    viewModelState: EditGroceryListScreenViewModelState,
    state: EditGroceryListScreenStateHolder,
    onBackButtonClick: () -> Unit,
    onNewGroceryListItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topBarScrollState = rememberTopAppBarState()
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topBarScrollState)

    UseAnimatedFadeTopBarColorForStatusBarColor(topAppBarState = topBarScrollState)
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
                    Text(text = stringResource(id = R.string.new_price_title))
                },
                actions = {
//                    SaveButton(
//                        onClick = onSaveButtonClick,
//                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, end = 10.dp)
//                    )
                },
                scrollBehavior = topBarScrollBehavior,
            )
        },
    ) { scaffoldPaddings ->
        val groceryListItems = viewModelState.groceryListItemFlow.collectAsLazyPagingItems()
        if (groceryListItems.isInitialLoadCompleted()) {
            if (groceryListItems.itemCount == 0) {
                EmptyContent(
                    onNewGroceryListItemClick = onNewGroceryListItemClick,
                    modifier = Modifier
                        .padding(scaffoldPaddings)
                        .fillMaxSize()
                )
            } else {

            }
        }
    }
}

@Composable
private fun EmptyContent(
    onNewGroceryListItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .verticalScroll(scrollState)
    ) {
        Image(
            painter = painterResource(id = R.drawable.shopping_cart_supermarket_svgrepo_com),
            contentDescription = stringResource(id = R.string.grocery_lists_empty_image_content_description),
            modifier = Modifier
                .size(200.dp, 200.dp)
                .padding(bottom = 16.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )

        Text(
            text = stringResource(id = R.string.grocery_lists_empty_text),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .padding(bottom = 12.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        Button(
            onClick = onNewGroceryListItemClick,
        ) {
            Text(
                text = stringResource(id = R.string.grocery_lists_empty_add_button_text),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun DefaultPreview() {
//    val topBarState = rememberTopAppBarState()
//    val topBarScrollBehavior =
//        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state = topBarState)
//    val viewModelState = object : GroceryListScreenViewModelState {
//        override val groceryListsWithItemCountFlow = flowOf(
//            PagingData.from(
//                listOf(
//                    GroceryListWithItemCount(
//                        groceryList = GroceryList(name = "Grocery list 1"),
//                        itemCount = 5
//                    )
//                )
//            )
//        )
//    }
//
//    MainContent(
//        topBarScrollBehavior = topBarScrollBehavior,
//        viewModelState = viewModelState,
//        state = GroceryListScreenStateHolder(),
//        onNewGroceryListClick = {},
//        onItemCountChanged = {}
//    )
}
