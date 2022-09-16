package com.igrocery.overpriced.presentation.selectcurrency

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.shared.BackButton
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.shared.Logger
import java.util.*

@Suppress("unused")
private val log = Logger { }

@Composable
fun SelectCurrencyScreen(
    viewModel: SelectCurrencyScreenViewModel,
    navigateUp: () -> Unit,
) {
    log.debug("Composing SelectCurrencyScreen")

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

    val state by rememberSelectCurrencyScreenState()
    MainContent(
        viewModelState = viewModel.uiState,
        state = state,
        onBackButtonClick = navigateUp,
        onCurrencyRowClick = {
            viewModel.selectCurrency(it)
            navigateUp()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    viewModelState: SelectCurrencyScreenViewModel.ViewModelState,
    state: SelectCurrencyScreenStateHolder,
    onBackButtonClick: () -> Unit,
    onCurrencyRowClick: (Currency) -> Unit
) {
    val topBarScrollState = rememberTopAppBarState()
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topBarScrollState)
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
                    Text(text = stringResource(id = R.string.select_currency_title))
                },
                scrollBehavior = topBarScrollBehavior,
                modifier = Modifier.statusBarsPadding()
            )
        },
    ) { scaffoldPaddings ->
        Column(
            modifier = Modifier
                .padding(scaffoldPaddings)
                .navigationBarsPadding()
                .fillMaxSize()
        ) {
            Surface(
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_info_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Text(
                        text = stringResource(id = R.string.select_currency_display_info),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            val preferredCurrency by viewModelState.preferredCurrencyFlow.collectAsState()
            preferredCurrency.let {
                if (it is LoadingState.Success) {
                    CurrencyLazyColumn(
                        allCurrencies = state.availableCurrencies,
                        preferredCurrency = it.data,
                        topBarScrollBehavior = topBarScrollBehavior,
                        onCurrencyRowClick = onCurrencyRowClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyLazyColumn(
    allCurrencies: List<Currency>,
    preferredCurrency: Currency?,
    topBarScrollBehavior: TopAppBarScrollBehavior,
    onCurrencyRowClick: (Currency) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberLazyListState(
        initialFirstVisibleItemIndex = (allCurrencies.indexOf(preferredCurrency) - 4)
            .coerceAtLeast(0)
    )

    var itemHeight = remember { 0 }
    LazyColumn(
        state = scrollState,
        contentPadding = PaddingValues(vertical = 8.dp),
        modifier = modifier
            .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
    ) {
        items(
            items = allCurrencies,
            key = { it.currencyCode }
        ) { currency ->
            CurrencyItem(
                isSelected = preferredCurrency == currency,
                currency = currency,
                onRowClick = onCurrencyRowClick,
                modifier = Modifier.onGloballyPositioned {
                    itemHeight = it.size.height
                }
            )
        }
    }

    LaunchedEffect(key1 = itemHeight) {
        val scrollOffsetY = (-itemHeight * scrollState.firstVisibleItemIndex).toFloat()
        topBarScrollBehavior.nestedScrollConnection.onPostScroll(
            consumed = Offset(0f, scrollOffsetY),
            available = Offset(0f, scrollOffsetY),
            source = NestedScrollSource.Drag
        )
    }
}

@Composable
fun CurrencyItem(
    isSelected: Boolean,
    currency: Currency,
    onRowClick: (Currency) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onRowClick(currency) }
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_baseline_check_24),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            contentDescription = stringResource(id = R.string.select_currency_selected_currency_content_description),
            modifier = Modifier
                .padding(12.dp)
                .size(24.dp),
            alpha = if (isSelected) 1f else 0f
        )

        Text(
            text = "${currency.currencyCode} - ${currency.displayName}",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    MainContent(
        viewModelState = SelectCurrencyScreenViewModel.ViewModelState(),
        state = SelectCurrencyScreenStateHolder(),
        onBackButtonClick = {},
        onCurrencyRowClick = {}
    )
}
