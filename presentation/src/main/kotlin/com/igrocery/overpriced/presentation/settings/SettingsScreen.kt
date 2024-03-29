package com.igrocery.overpriced.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.shared.BackButton
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.presentation.shared.UseAnimatedFadeTopBarColorForStatusBarColor
import com.igrocery.overpriced.presentation.shared.UseDefaultSystemNavBarColor
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

@Suppress("unused")
private val log = Logger { }

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenViewModel,
    navigateUp: () -> Unit,
    navigateToSelectCurrencyScreen: () -> Unit,
) {
    log.debug("Composing SettingsScreen")

    MainContent(
        viewModelState = viewModel,
        onBackButtonClick = navigateUp,
        onPreferredCurrencyRowClick = navigateToSelectCurrencyScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    viewModelState: SettingsScreenViewModelState,
    onBackButtonClick: () -> Unit,
    onPreferredCurrencyRowClick: () -> Unit
) {
    val topBarScrollState = rememberTopAppBarState()
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topBarScrollState)

    UseAnimatedFadeTopBarColorForStatusBarColor(topBarScrollState)
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
                    Text(text = stringResource(id = R.string.settings_title))
                },
                scrollBehavior = topBarScrollBehavior,
            )
        },
    ) { scaffoldPaddings ->
        val scrollState = rememberScrollState()
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(scaffoldPaddings)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPreferredCurrencyRowClick() }
                    .padding(vertical = 6.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.settings_preferred_currency_label),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                val preferredCurrency by viewModelState.preferredCurrencyFlow.collectAsState()
                preferredCurrency.let {
                    Text(
                        text = if (it is LoadingState.Success) it.data.displayName else "",
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.alpha(0.6f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    MainContent(
        viewModelState = object : SettingsScreenViewModelState {
            override val preferredCurrencyFlow: StateFlow<LoadingState<Currency>>
                get() = MutableStateFlow(LoadingState.Loading())
        },
        onBackButtonClick = {},
        onPreferredCurrencyRowClick = {}
    )
}
