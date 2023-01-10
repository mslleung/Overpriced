package com.igrocery.overpriced.presentation.mainnavigation.grocerylist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.igrocery.overpriced.domain.grocerylist.dtos.GroceryListWithItemCount
import com.igrocery.overpriced.domain.grocerylist.models.GroceryList
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.shared.UseDefaultBottomNavBarColourForSystemNavBarColor
import com.igrocery.overpriced.presentation.shared.UseDefaultStatusBarColor
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
    groceryListScreenViewModel: GroceryListScreenViewModel,
    modifier: Modifier = Modifier,
) {
    log.debug("Composing GroceryListScreen")

    val state by rememberGroceryListScreenState()
    MainContent(
        topBarScrollBehavior = topBarScrollBehavior,
        viewModelState = groceryListScreenViewModel,
        state = state,
        onNewGroceryListClick = { /*TODO*/ })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    topBarScrollBehavior: TopAppBarScrollBehavior,
    viewModelState: GroceryListScreenViewModelState,
    state: GroceryListScreenStateHolder,
    onNewGroceryListClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    UseDefaultStatusBarColor()
    UseDefaultBottomNavBarColourForSystemNavBarColor()

    Scaffold(
        contentWindowInsets = WindowInsets.ime,
        modifier = modifier,
    ) { scaffoldPaddings ->
        val groceryListsWithItemCount =
            viewModelState.groceryListsWithItemCountFlow.collectAsLazyPagingItems()
        LazyColumn(
            modifier = Modifier
                .padding(scaffoldPaddings)
                .fillMaxSize()
        ) {
            items(
                items = groceryListsWithItemCount,
                key = { it.groceryList.id }
            ) { item ->
                if (item != null) {
                    GroceryListContent(
                        groceryListWithItemCount = item,
                        modifier = Modifier
                            .height(48.dp)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun GroceryListContent(
    groceryListWithItemCount: GroceryListWithItemCount,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text(
                text = groceryListWithItemCount.groceryList.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium
            )

            val updateInstant =
                Instant.fromEpochMilliseconds(groceryListWithItemCount.groceryList.updateTimestamp)
            val updateDate = updateInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date
            Text(
                text = "${updateDate.dayOfMonth}/${updateDate.monthNumber}/${updateDate.year}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.alpha(0.6f)
            )
        }

        Text(
            text = pluralStringResource(
                id = R.plurals.grocery_lists_item_count,
                count = groceryListWithItemCount.itemCount
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(0.6f)
        )
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
    )
}
