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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.himanshoe.charty.line.LineChart
import com.himanshoe.charty.line.config.LineConfig
import com.himanshoe.charty.line.model.LineData
import com.igrocery.overpriced.domain.productpricehistory.models.*
import com.igrocery.overpriced.presentation.shared.*
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.*

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
        val priceRecords =
            viewModelState.priceRecordsPagingDataFlow.collectAsLazyPagingItems()
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
                        .padding(bottom = 8.dp)
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

                    if (priceRecords.isInitialLoadCompleted()) {
                        // we just show 10 points for now, note that the pager loads 100 * 3 items initially
                        val lineDataList = mutableListOf<LineData>()
                        val numOfDataPoints = priceRecords.itemCount.coerceAtMost(10)
                        repeat(numOfDataPoints) { index ->
                            val priceRecord = priceRecords[index]
                                ?: throw IllegalStateException("No price record at index $index")
                            val date = Instant.fromEpochMilliseconds(priceRecord.creationTimestamp)
                                .toLocalDateTime(TimeZone.currentSystemDefault()).date
                            lineDataList.add(
                                0,
                                LineData(
                                    xValue = "${date.dayOfMonth}/${date.monthNumber}",
                                    yValue = priceRecord.price.amount.toFloat()
                                )
                            )
                        }
                        LineChart(
                            lineData = lineDataList,
                            color = MaterialTheme.colorScheme.primary,
                            lineConfig = LineConfig(hasSmoothCurve = false, hasDotMarker = true),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                        )
                    }
                }
            }

            items(
                items = priceRecords,
                key = { priceRecord -> priceRecord.id }
            ) { priceRecord ->
                if (priceRecord != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
//                            .clickable {  }
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        val dateTime = Instant.fromEpochMilliseconds(priceRecord.creationTimestamp)
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                        val date = dateTime.date
                        val dateString = "${date.dayOfMonth}/${date.monthNumber}/${date.year}"
                        val time = dateTime.time
                        val timeString = if (time.hour < 12) {
                            "${time.hour}:${time.minute} AM"
                        } else if (time.hour == 12) {
                            "${time.hour}:${time.minute} PM"
                        } else {
                            "${time.hour - 12}:${time.minute} PM"
                        }
                        Text(
                            text = "$dateString $timeString",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .alpha(0.6f)
                                .fillMaxWidth(0.6f)
                        )

                        Text(
                            text = "${priceRecord.price.currency.symbol} ${priceRecord.price.amount}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    val viewModelState = object : StorePriceDetailScreenViewModelState {
        override val currencyFlow: StateFlow<LoadingState<Currency>> =
            MutableStateFlow(LoadingState.Success(Currency.getInstance(Locale.US)))
        override val productFlow: StateFlow<LoadingState<Product>> = MutableStateFlow(
            LoadingState.Success(Product(name = "Apple", description = "", categoryId = null))
        )
        override val storeFlow: StateFlow<LoadingState<Store>> = MutableStateFlow(
            LoadingState.Success(
                Store(
                    name = "Apple",
                    address = Address("Example street", GeoCoordinates(0.0, 0.0))
                )
            )
        )
        override val priceRecordsPagingDataFlow = MutableStateFlow(
            PagingData.from(
                listOf(
                    PriceRecord(
                        productId = 0,
                        price = Money(5.0, Currency.getInstance(Locale.US)),
                        storeId = 0
                    )
                )
            )
        )
    }

    MainContent(
        viewModelState = viewModelState,
        state = StorePriceDetailScreenStateHolder(),
        onBackButtonClick = {},
    )
}
