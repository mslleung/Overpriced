package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.igrocery.overpriced.domain.GroceryListId
import com.igrocery.overpriced.domain.GroceryListItemId
import com.igrocery.overpriced.domain.grocerylist.models.GroceryList
import com.igrocery.overpriced.domain.grocerylist.models.GroceryListItem
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.newstore.*
import com.igrocery.overpriced.presentation.shared.*
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

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
        onEditButtonClick = {
            state.isGroceryListNameDialogShown = true
        },
        onGroceryListItemCheckChange = { groceryListItemId, checked ->

        },
        onGroceryListItemClick = {
            state.isAddGroceryListItemDialogShown = true
            
        },
        onAddItemClick = {
            state.isAddGroceryListItemDialogShown = true
        }
    )

    if (state.isGroceryListNameDialogShown) {
        val groceryList by editGroceryListViewModel.groceryListFlow.collectAsState()
        groceryList.ifLoaded {
            val groceryListNameDialogState by rememberGroceryListNameDialogState(initialName = it.name)
            EditGroceryListNameDialog(
                state = groceryListNameDialogState,
                onConfirm = {
                    state.isGroceryListNameDialogShown = false
                    editGroceryListViewModel.editGroceryList(
                        it.copy(name = groceryListNameDialogState.groceryListName.text)
                    )
                },
                onDismiss = { state.isGroceryListNameDialogShown = false }
            )
        }
    }

    if (state.isAddGroceryListItemDialogShown) {
        val addGroceryListItemDialogState by rememberAddGroceryListItemDialogState()
        AddGroceryListItemDialog(
            state = addGroceryListItemDialogState,
            onConfirm = {
                state.isAddGroceryListItemDialogShown = false
                editGroceryListViewModel.addItem(
                    itemName = addGroceryListItemDialogState.itemName.text,
                    itemDescription = addGroceryListItemDialogState.itemDescription
                )
            },
            onDismiss = { state.isAddGroceryListItemDialogShown = false }
        )
    }

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
    onEditButtonClick: () -> Unit,
    onGroceryListItemCheckChange: (GroceryListItemId, Boolean) -> Unit,
    onGroceryListItemClick: (GroceryListItem) -> Unit,
    onAddItemClick: () -> Unit,
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
                    IconButton(
                        onClick = onEditButtonClick,
                        modifier = Modifier
                            .size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_edit_24),
                            contentDescription = stringResource(id = R.string.edit_grocery_list_edit_button_content_description)
                        )
                    }
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
                onClick = onAddItemClick,
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
                            GroceryListItemContent(
                                groceryListItem = item,
                                onItemCheckChange = { onGroceryListItemCheckChange(item.id, it) },
                                onItemClick = { onGroceryListItemClick(item) },
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
private fun GroceryListItemContent(
    groceryListItem: GroceryListItem,
    onItemCheckChange: (Boolean) -> Unit,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable { onItemClick() }
    ) {
        Checkbox(
            checked = groceryListItem.isChecked,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                uncheckedColor = MaterialTheme.colorScheme.primary,
                checkmarkColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            onCheckedChange = onItemCheckChange,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = groceryListItem.name,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
        )
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    val viewModelState = object : EditGroceryListScreenViewModelState {
        override val groceryListFlow: StateFlow<LoadingState<GroceryList>> =
            MutableStateFlow(
                LoadingState.Success(
                    GroceryList(name = "Grocery list 1")
                )
            )
        override val groceryListItemFlow: Flow<PagingData<GroceryListItem>> = flowOf(
            PagingData.from(
                listOf(
                    GroceryListItem(
                        groceryListId = GroceryListId(1L),
                        name = "Apples",
                        description = "",
                        isChecked = false
                    ),
                    GroceryListItem(
                        groceryListId = GroceryListId(1L),
                        name = "Oranges",
                        description = "5 pieces",
                        isChecked = true
                    ),
                )
            )
        )
        override var editGroceryListResultState: LoadingState<Unit> by remember {
            mutableStateOf(
                LoadingState.NotLoading()
            )
        }
    }

    val state by rememberEditGroceryListScreenState()

    MainContent(
        viewModelState = viewModelState,
        state = state,
        onBackButtonClick = {},
        onEditButtonClick = {},
        onGroceryListItemCheckChange = { _, _ -> },
        onGroceryListItemClick = {},
        onAddItemClick = {}
    )
}
