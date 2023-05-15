package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.draw.alpha
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
    navigateToSearchProduct: (query: String) -> Unit
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
        onDeleteButtonClick = {
            editGroceryListViewModel.deleteGroceryList()
            navigateUp()
        },
        onGroceryListItemCheckChange = { groceryListItem, isChecked ->
            editGroceryListViewModel.updateItem(
                item = groceryListItem.copy(
                    isChecked = isChecked,
                )
            )
        },
        onGroceryListItemClick = {
            state.editingGroceryListItem = it
        },
        onGroceryListItemLongClick = {
            state.longClickGroceryListItem = it
        },
        onGroceryListItemSearchPricesClick = {
            navigateToSearchProduct(it.name)
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
        val groceryListItemDialogState by rememberGroceryListItemDialogState()
        GroceryListItemDialog(
            state = groceryListItemDialogState,
            onConfirm = {
                state.isAddGroceryListItemDialogShown = false
                editGroceryListViewModel.addItem(
                    itemName = groceryListItemDialogState.itemName.text,
                    itemDescription = groceryListItemDialogState.itemDescription
                )
            },
            onDismiss = { state.isAddGroceryListItemDialogShown = false }
        )
    }

    state.editingGroceryListItem?.let {
        val groceryListItemDialogState by rememberGroceryListItemDialogState(
            initialName = it.name,
            initialDescription = it.description
        )
        GroceryListItemDialog(
            state = groceryListItemDialogState,
            onConfirm = {
                state.editingGroceryListItem = null
                editGroceryListViewModel.updateItem(
                    item = it.copy(
                        name = groceryListItemDialogState.itemName.text,
                        description = groceryListItemDialogState.itemDescription
                    )
                )
            },
            onDismiss = { state.editingGroceryListItem = null }
        )
    }

    state.longClickGroceryListItem?.let { item ->
        ListSelectionDialog(
            selections = listOf(
                stringResource(id = R.string.edit_grocery_list_item_more_edit),
                stringResource(id = R.string.edit_grocery_list_item_more_delete)
            ),
            onDismiss = {
                state.longClickGroceryListItem = null
            },
            onSelected = {
                when (it) {
                    0 -> {
                        state.longClickGroceryListItem = null
                        state.editingGroceryListItem = item
                    }

                    1 -> {
                        state.longClickGroceryListItem = null
                        editGroceryListViewModel.deleteItem(item)
                    }

                    else -> {
                        throw NotImplementedError("selection $it not handled")
                    }
                }
            }
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
    onDeleteButtonClick: () -> Unit,
    onGroceryListItemCheckChange: (GroceryListItem, Boolean) -> Unit,
    onGroceryListItemClick: (GroceryListItem) -> Unit,
    onGroceryListItemLongClick: (GroceryListItem) -> Unit,
    onGroceryListItemSearchPricesClick: (GroceryListItem) -> Unit,
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
                    var isOverflowShown by remember { mutableStateOf(false) }

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

                    IconButton(
                        onClick = { isOverflowShown = !isOverflowShown },
                        modifier = Modifier
                            .size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_more_vert_24),
                            contentDescription = stringResource(id = R.string.edit_grocery_list_overflow_button_content_description)
                        )
                    }
                    DropdownMenu(
                        expanded = isOverflowShown,
                        onDismissRequest = { isOverflowShown = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(id = R.string.edit_grocery_list_delete_action),
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                            },
                            onClick = { onDeleteButtonClick() }
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
                                onItemCheckChange = { onGroceryListItemCheckChange(item, it) },
                                onItemClick = { onGroceryListItemClick(item) },
                                onItemLongClick = { onGroceryListItemLongClick(item) },
                                onItemSearchPricesClick = { onGroceryListItemSearchPricesClick(item) },
                                modifier = Modifier
                                    .animateItemPlacement()
                                    .wrapContentHeight()
                                    .fillMaxWidth()
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GroceryListItemContent(
    groceryListItem: GroceryListItem,
    onItemCheckChange: (Boolean) -> Unit,
    onItemClick: () -> Unit,
    onItemLongClick: () -> Unit,
    onItemSearchPricesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .combinedClickable(
                onClick = onItemClick,
                onLongClick = onItemLongClick,
            )
            .padding(horizontal = 4.dp)
            .alpha(if (groceryListItem.isChecked) 0.6f else 1f) // fade out the whole row if checked
    ) {
        Checkbox(
            checked = groceryListItem.isChecked,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.secondary,
                uncheckedColor = MaterialTheme.colorScheme.primary,
                checkmarkColor = MaterialTheme.colorScheme.onSecondary
            ),
            onCheckedChange = onItemCheckChange,
        )

        Column(
            modifier = Modifier
                .padding(start = 4.dp)
                .weight(1f)
        ) {
            Text(
                text = groceryListItem.name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            if (groceryListItem.description.isNotBlank()) {
                Text(
                    text = groceryListItem.description,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .alpha(0.6f)
                )
            }
        }

        IconButton(
            onClick = onItemSearchPricesClick,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.search_money_svgrepo_com),
                contentDescription = stringResource(R.string.edit_grocery_list_item_search_prices),
                tint = if (groceryListItem.isChecked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
            )
        }
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
            mutableStateOf(LoadingState.NotLoading())
        }
        override var deleteGroceryListResultState: LoadingState<Unit> by remember {
            mutableStateOf(LoadingState.NotLoading())
        }
    }

    val state by rememberEditGroceryListScreenState()

    MainContent(
        viewModelState = viewModelState,
        state = state,
        onBackButtonClick = {},
        onEditButtonClick = {},
        onDeleteButtonClick = {},
        onGroceryListItemCheckChange = { _, _ -> },
        onGroceryListItemClick = {},
        onGroceryListItemLongClick = {},
        onGroceryListItemSearchPricesClick = {},
        onAddItemClick = {}
    )
}
