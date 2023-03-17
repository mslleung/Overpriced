package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.igrocery.overpriced.domain.GroceryListItemId
import com.igrocery.overpriced.domain.grocerylist.models.GroceryListItem
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

    val state by rememberEditGroceryListScreenState()
    MainContent(
        viewModelState = editGroceryListViewModel,
        state = state,
        onBackButtonClick = navigateUp,
        onGroceryListSelectChange = {

        },
        onGroceryListItemClick = {
            // TODO edit item?
        }
    )

    BackHandler {
        log.debug("EditGroceryListScreen: BackHandler")
        navigateUp()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun MainContent(
    viewModelState: EditGroceryListScreenViewModelState,
    state: EditGroceryListScreenStateHolder,
    onBackButtonClick: () -> Unit,
    onGroceryListSelectChange: (Boolean) -> Unit,
    onGroceryListItemClick: (GroceryListItemId) -> Unit,
    modifier: Modifier = Modifier
) {
    val topBarScrollState = rememberTopAppBarState()
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topBarScrollState)

    UseAnimatedFadeTopBarColorForStatusBarColor(topAppBarState = topBarScrollState)
    UseDefaultSystemNavBarColor()

    val groceryList by viewModelState.groceryListFlow.collectAsState()
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
                    groceryList.ifLoaded {
                        Text(text = it.name)
                    }
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
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(id = R.string.edit_grocery_list_fab_text)) },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_add_24),
                        contentDescription = stringResource(
                            id = R.string.edit_grocery_list_fab_text
                        ),
                        modifier = Modifier.size(24.dp)
                    )
                },
                onClick = {
                    // TODO
                },
                modifier = Modifier.padding(
                    WindowInsets.navigationBars.only(WindowInsetsSides.End)
                        .asPaddingValues()
                )
            )
        },
        modifier = modifier
    ) { scaffoldPaddings ->
        val groceryListItems = viewModelState.groceryListItemFlow.collectAsLazyPagingItems()
        if (groceryListItems.isInitialLoadCompleted()) {
            if (groceryListItems.itemCount == 0) {
                EmptyContent(
                    modifier = Modifier
                        .padding(scaffoldPaddings)
                        .fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(scaffoldPaddings)
                        .fillMaxSize()
                        .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
                ) {
                    items(
                        items = groceryListItems,
                        key = { it.id }
                    ) { item ->
                        if (item != null) {
                            MainContent(
                                groceryListItem = item,
                                onSelectChange = onGroceryListSelectChange,
                                onItemClick = onGroceryListItemClick,
                                modifier = Modifier
                                    .animateItemPlacement()
                                    .wrapContentHeight()
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyContent(
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
            painter = painterResource(id = R.drawable.empty_cart_svgrepo_com),
            contentDescription = stringResource(id = R.string.edit_grocery_list_empty_text),
            modifier = Modifier
                .size(200.dp, 200.dp)
                .padding(bottom = 16.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )

        Text(
            text = stringResource(id = R.string.edit_grocery_list_empty_text),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .padding(bottom = 12.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun MainContent(
    groceryListItem: GroceryListItem,
    onItemCheckChange: (Boolean) -> Unit,
    onItemClick: (GroceryListItemId) -> Unit,

    modifier: Modifier = Modifier
) {
    Row(

    )
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
