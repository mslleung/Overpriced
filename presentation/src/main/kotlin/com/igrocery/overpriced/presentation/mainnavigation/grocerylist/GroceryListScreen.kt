package com.igrocery.overpriced.presentation.mainnavigation.grocerylist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.igrocery.overpriced.domain.GroceryListId
import com.igrocery.overpriced.domain.grocerylist.dtos.GroceryListWithItemCount
import com.igrocery.overpriced.domain.grocerylist.models.GroceryList
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.mainnavigation.MainBottomNavigationScreenStateHolder
import com.igrocery.overpriced.presentation.shared.UseDefaultBottomNavBarColourForSystemNavBarColor
import com.igrocery.overpriced.presentation.shared.UseDefaultStatusBarColor
import com.igrocery.overpriced.presentation.shared.isInitialLoadCompleted
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Suppress("unused")
private val log = Logger { }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryListScreen(
    topBarScrollBehavior: TopAppBarScrollBehavior,
    mainBottomNavigationState: MainBottomNavigationScreenStateHolder,
    groceryListScreenViewModel: GroceryListScreenViewModel,
    navigateToEditGroceryList: (GroceryListId) -> Unit,
    modifier: Modifier = Modifier,
) {
    log.debug("Composing GroceryListScreen")

    val state by rememberGroceryListScreenState()
    MainContent(
        topBarScrollBehavior = topBarScrollBehavior,
        viewModelState = groceryListScreenViewModel,
        state = state,
        onNewGroceryListClick = {
            mainBottomNavigationState.isGroceryListNameDialogShown = true
        },
        onGroceryListClick = navigateToEditGroceryList,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun MainContent(
    topBarScrollBehavior: TopAppBarScrollBehavior,
    viewModelState: GroceryListScreenViewModelState,
    state: GroceryListScreenStateHolder,
    onNewGroceryListClick: () -> Unit,
    onGroceryListClick: (GroceryListId) -> Unit,
    modifier: Modifier = Modifier
) {
    UseDefaultStatusBarColor()
    UseDefaultBottomNavBarColourForSystemNavBarColor()

    Scaffold(
        modifier = modifier,
    ) { scaffoldPaddings ->
        val groceryListsWithItemCount =
            viewModelState.groceryListsWithItemCountFlow.collectAsLazyPagingItems()
        if (groceryListsWithItemCount.isInitialLoadCompleted()) {
            if (groceryListsWithItemCount.itemCount == 0) {
                EmptyContent(
                    onNewGroceryListClick = onNewGroceryListClick,
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
                        items = groceryListsWithItemCount,
                        key = { it.groceryList.id }
                    ) { item ->
                        if (item != null) {
                            GroceryListContent(
                                groceryListWithItemCount = item,
                                onClick = onGroceryListClick,
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
    onNewGroceryListClick: () -> Unit,
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
            onClick = onNewGroceryListClick,
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
@Composable
private fun GroceryListContent(
    groceryListWithItemCount: GroceryListWithItemCount,
    onClick: (GroceryListId) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onClick(groceryListWithItemCount.groceryList.id) },
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .padding(bottom = 4.dp)
            ) {
                Text(
                    text = groceryListWithItemCount.groceryList.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                val updateInstant =
                    Instant.fromEpochMilliseconds(groceryListWithItemCount.groceryList.updateTimestamp)
                val updateDate = updateInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                Text(
                    text = "${updateDate.dayOfMonth}/${updateDate.monthNumber}/${updateDate.year}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.alpha(0.6f)
                )
            }

            Text(
                text = pluralStringResource(
                    id = R.plurals.grocery_lists_item_count,
                    count = groceryListWithItemCount.itemCount,
                    groceryListWithItemCount.itemCount
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0.6f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun DefaultPreview() {
    val topBarState = rememberTopAppBarState()
    val topBarScrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state = topBarState)
    val viewModelState = object : GroceryListScreenViewModelState {
        override val groceryListsWithItemCountFlow = flowOf(
            PagingData.from(
                listOf(
                    GroceryListWithItemCount(
                        groceryList = GroceryList(name = "Grocery list 1"),
                        itemCount = 5
                    )
                )
            )
        )
    }

    MainContent(
        topBarScrollBehavior = topBarScrollBehavior,
        viewModelState = viewModelState,
        state = GroceryListScreenStateHolder(),
        onNewGroceryListClick = {},
        onGroceryListClick = {}
    )
}
