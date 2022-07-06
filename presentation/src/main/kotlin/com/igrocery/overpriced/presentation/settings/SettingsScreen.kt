package com.igrocery.overpriced.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.igrocery.overpriced.presentation.shared.BackButton
import com.igrocery.overpriced.shared.Logger
import com.ireceipt.receiptscanner.presentation.R
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

    val preferredCurrency by viewModel.preferredCurrencyFlow.collectAsState()
    val state by rememberSettingsScreenState()
    MainContent(
        preferredCurrency = preferredCurrency,
        state = state,
        onBackButtonClick = navigateUp,
        onPreferredCurrencyRowClick = navigateToSelectCurrencyScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    preferredCurrency: Currency?,
    state: SettingsScreenStateHolder,
    onBackButtonClick: () -> Unit,
    onPreferredCurrencyRowClick: () -> Unit
) {
    val topBarScrollState = rememberTopAppBarScrollState()
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topBarScrollState)
    Scaffold(
        topBar = {
            SmallTopAppBar(
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
                modifier = Modifier.statusBarsPadding()
            )
        },
    ) { scaffoldPaddings ->
        val scrollState = rememberScrollState()
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(scaffoldPaddings)
                .navigationBarsPadding()
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPreferredCurrencyRowClick() }
                    .padding(vertical = 6.dp, horizontal = 12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.settings_preferred_currency_label),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = preferredCurrency?.displayName ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.alpha(0.6f)
                )
            }
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    MainContent(
        preferredCurrency = Currency.getInstance(Locale.getDefault()),
        state = SettingsScreenStateHolder(),
        onBackButtonClick = {},
        onPreferredCurrencyRowClick = {}
    )
}
