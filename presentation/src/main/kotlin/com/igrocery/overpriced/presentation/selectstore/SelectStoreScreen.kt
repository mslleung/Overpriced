package com.igrocery.overpriced.presentation.selectstore

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.domain.productpricehistory.models.*
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.editstore.ConfirmDeleteStoreDialog
import com.igrocery.overpriced.presentation.selectstore.SelectStoreScreenStateHolder.*
import com.igrocery.overpriced.presentation.shared.*
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Suppress("unused")
private val log = Logger { }

@Composable
internal fun SelectStoreScreen(
    args: SelectStoreScreenArgs,
    viewModel: SelectStoreScreenViewModel,
    navigateUp: () -> Unit,
    navigateUpWithResults: (StoreId) -> Unit,
    navigateToNewStore: () -> Unit,
    navigateToEditStore: (StoreId) -> Unit,
) {
    log.debug("Composing SelectStoreScreen")

    val state by rememberSelectStoreScreenState(args)
    MainLayout(
        viewModelState = viewModel,
        state = state,
        onBackButtonClick = navigateUp,
        onStoreClick = navigateUpWithResults,
        onNewStoreClick = navigateToNewStore,
        onStoreMoreClick = {
            state.storeMoreDialogData = StoreMoreDialogData(it)
        }
    )

    state.storeMoreDialogData?.let { dialogData ->
        ListSelectionDialog(
            selections = listOf(
                stringResource(id = R.string.select_store_more_edit),
                stringResource(id = R.string.select_store_more_delete)
            ),
            onDismiss = {
                state.storeMoreDialogData = null
            },
            onSelected = {
                when (it) {
                    0 -> {
                        navigateToEditStore(dialogData.store.id)
                        state.storeMoreDialogData = null
                    }
                    1 -> {
                        state.deleteStoreDialogData = DeleteStoreDialogData(dialogData.store)
                        state.storeMoreDialogData = null
                    }
                    else -> {
                        throw NotImplementedError("selection $it not handled")
                    }
                }
            }
        )
    }

    state.deleteStoreDialogData?.let { dialogData ->
        ConfirmDeleteStoreDialog(
            onDismiss = {
                state.deleteStoreDialogData = null
            },
            onConfirm = {
                viewModel.deleteStore(dialogData.store)
                state.deleteStoreDialogData = null
            }
        )
    }

    BackHandler {
        log.debug("SelectStoreScreen: BackHandler")
        if (state.deleteStoreDialogData != null) {
            state.deleteStoreDialogData = null
        } else if (state.storeMoreDialogData != null) {
            state.storeMoreDialogData = null
        } else {
            navigateUp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainLayout(
    viewModelState: SelectStoreScreenViewModelState,
    state: SelectStoreScreenStateHolder,
    onBackButtonClick: () -> Unit,
    onStoreClick: (StoreId) -> Unit,
    onNewStoreClick: () -> Unit,
    onStoreMoreClick: (Store) -> Unit
) {
    val topBarScrollState = rememberTopAppBarState()
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topBarScrollState)

    UseAnimatedFadeTopBarColorForStatusBarColor(topBarScrollState)
    UseDefaultSystemNavBarColor()

    val storesPagingItems = viewModelState.storesPagingDataFlow.collectAsLazyPagingItems()
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
                    Text(text = stringResource(id = R.string.select_store_title_text))
                },
                actions = {
                    if (storesPagingItems.itemCount != 0) {
                        IconButton(
                            onClick = onNewStoreClick,
                            modifier = Modifier
                                .size(48.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_add_24),
                                contentDescription = stringResource(id = R.string.select_store_new_store_icon_content_description)
                            )
                        }
                    }
                },
                scrollBehavior = topBarScrollBehavior,
            )
        },
    ) { scaffoldPadding ->
        if (storesPagingItems.isInitialLoadCompleted()) {
            if (storesPagingItems.itemCount == 0) {
                EmptyLayout(
                    onNewStoreClick = onNewStoreClick,
                    modifier = Modifier
                        .padding(scaffoldPadding)
                        .fillMaxSize()
                        .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    modifier = Modifier
                        .padding(scaffoldPadding)
                        .fillMaxSize()
                        .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
                ) {
                    items(
                        items = storesPagingItems,
                        key = { store -> store.id }
                    ) { store ->
                        if (store != null) {
                            StoreLocationOptionLayout(
                                store = store,
                                isSelected = state.selectedStoreId == store.id,
                                onStoreClick = onStoreClick,
                                onMoreClick = onStoreMoreClick,
                                modifier = Modifier
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
private fun EmptyLayout(
    onNewStoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(scrollState)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_market),
            contentDescription = stringResource(id = R.string.select_store_empty_icon_content_description),
            modifier = Modifier
                .size(200.dp, 200.dp)
                .padding(bottom = 32.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )

        Text(
            text = stringResource(id = R.string.select_store_empty_text),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .padding(bottom = 24.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        Button(
            onClick = onNewStoreClick,
        ) {
            Text(
                text = stringResource(id = R.string.select_store_empty_new_category_button_text),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun StoreLocationOptionLayout(
    store: Store,
    isSelected: Boolean,
    onStoreClick: (StoreId) -> Unit,
    onMoreClick: (Store) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier
            .clickable { onStoreClick(store.id) }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(24.dp),
        ) {
            if (isSelected) {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_check_24),
                    contentDescription = stringResource(id = R.string.select_store_selected_content_description),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .size(24.dp),
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            Text(
                text = store.name,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            val addressLines = store.address.lines
            if (addressLines.isNullOrBlank()) {
                Text(
                    text = stringResource(id = R.string.product_detail_store_no_address),
                    maxLines = 1,
                    fontStyle = FontStyle.Italic,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.alpha(0.6f)
                )
            } else {
                Text(
                    text = addressLines,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.alpha(0.6f)
                )
            }
        }

        // TODO view on map button

        IconButton(
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            onClick = { onMoreClick(store) },
            modifier = Modifier
                .size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_more_vert_24),
                contentDescription = stringResource(id = R.string.select_store_more_content_description)
            )
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    val viewModelState = object : SelectStoreScreenViewModelState {
        override var storesPagingDataFlow: Flow<PagingData<Store>> = flowOf(
            PagingData.from(
                listOf(
                    Store(
                        id = StoreId(0),
                        name = "Welcome",
                        address = Address(
                            "100 Happy Street, Mong Kok, HK",
                            geoCoordinates = GeoCoordinates(0.0, 0.0)
                        )
                    )
                )
            )
        )
    }

    MainLayout(
        viewModelState = viewModelState,
        state = SelectStoreScreenStateHolder(StoreId(0), null, null),
        onBackButtonClick = {},
        onNewStoreClick = {},
        onStoreClick = {},
        onStoreMoreClick = {},
    )
}
