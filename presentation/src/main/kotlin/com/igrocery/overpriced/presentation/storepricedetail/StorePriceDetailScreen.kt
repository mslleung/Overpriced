package com.igrocery.overpriced.presentation.storepricedetail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.himanshoe.charty.line.LineChart
import com.igrocery.overpriced.presentation.shared.*
import com.igrocery.overpriced.shared.Logger

@Suppress("unused")
private val log = Logger { }

@Composable
fun StorePriceDetailScreen(
    viewModel: StorePriceDetailScreenViewModel,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    log.debug("Composing StorePriceDetailScreen")

    val state by rememberStorePriceDetailScreenState()
    MainContent(
        viewModelState = viewModel,
        state = state,
        onBackButtonClick = navigateUp,
        modifier = modifier
    )

    BackHandler {
        log.debug("StorePriceDetailScreen: BackHandler")
        navigateUp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    viewModelState: StorePriceDetailScreenViewModelState,
    state: StorePriceDetailScreenStateHolder,
    onBackButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val topBarState = rememberTopAppBarState()
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topBarState)

    UseAnimatedFadeTopBarColorForStatusBarColor(topBarState)
    UseDefaultSystemNavBarColor()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val store by viewModelState.storeFlow.collectAsState()
                    store.ifLoaded {
                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = it.name,
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(end = 8.dp),
                            )

                            Text(
                                text = it.name,
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .alpha(0.6f),
                            )
                        }
                    }
                },
                navigationIcon = {
                    BackButton(
                        onClick = onBackButtonClick,
                        modifier = Modifier
                            .size(48.dp)
                    )
                },
                scrollBehavior = topBarScrollBehavior,
            )
        },
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
        ) {
            item(
                contentType = "chart"
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth()
                ) {
                    val productState by viewModelState.productFlow.collectAsState()
                    productState.ifLoaded { product ->
                        Text(
                            text = product.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        if (product.description.isNotBlank()) {
                            Text(
                                text = product.description,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .alpha(0.6f)
                            )
                        }
                    }


                    val priceRecords =
                        viewModelState.priceRecordsPagingDataFlow.collectAsLazyPagingItems()
                    if (priceRecords.isInitialLoadCompleted()) {
                        priceRecords.get()
                        LineChart(lineData = , color = MaterialTheme.colorScheme.primary)
                    }
                }


            }
        }
    }
}


