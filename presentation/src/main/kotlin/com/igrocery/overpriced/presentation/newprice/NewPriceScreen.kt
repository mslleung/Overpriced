package com.igrocery.overpriced.presentation.newprice

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantity
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantityUnit
import com.igrocery.overpriced.domain.productpricehistory.models.SaleQuantity
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.newprice.NewPriceScreenStateHolder.SubmitError
import com.igrocery.overpriced.presentation.selectcategory.SelectCategoryScreenResultViewModel
import com.igrocery.overpriced.presentation.selectstore.SelectStoreScreenResultViewModel
import com.igrocery.overpriced.presentation.shared.*
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.math.roundToInt

@Suppress("unused")
private val log = Logger { }

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Composable
fun NewPriceScreen(
    args: NewPriceScreenArgs,
    newPriceScreenViewModel: NewPriceScreenViewModel,
    selectCategoryResultViewModel: SelectCategoryScreenResultViewModel,
    selectStoreResultViewModel: SelectStoreScreenResultViewModel,
    navigateUp: () -> Unit,
    navigateToSelectCategory: (CategoryId?) -> Unit,
    navigateToSelectStore: (StoreId?) -> Unit,
) {
    log.debug("Composing NewPriceScreen")

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val state by rememberNewPriceScreenState(
        args,
        newPriceScreenViewModel,
        selectCategoryResultViewModel,
        selectStoreResultViewModel
    )
    LaunchedEffect(Unit) {
        newPriceScreenViewModel.productFlow.collect {
            // TODO state should have an isEdit mode?
        }
    }
    val productSuggestionsPagingItems =
        newPriceScreenViewModel.suggestedProductsPagingDataFlow.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }
    MainLayout(
        viewModelState = newPriceScreenViewModel,
        state = state,
        snackbarHostState = snackbarHostState,
        productSuggestionsPagingItems = productSuggestionsPagingItems,
        onCloseButtonClick = {
            if (state.hasModifications()) {
                state.isDiscardDialogShown = true
            } else {
                keyboardController?.hide()
                navigateUp()
            }
        },
        onSaveButtonClick = {
            with(state) {
                if (productName.isEmpty()) {
                    submitError = SubmitError.ProductNameShouldNotBeEmpty
                } else if (productQuantityAmountText.toDoubleOrNull() == null || productQuantityAmountText.toDouble() !in 0.0..1000000.0) {
                    submitError = SubmitError.InvalidProductQuantityAmount
                } else if (priceAmountText.toDoubleOrNull() == null || priceAmountText.toDouble() !in 0.0..1000000.0) {
                    submitError = SubmitError.InvalidPriceAmount
                } else {
                    val priceStoreId = priceStoreId
                    if (priceStoreId == null) {
                        submitError = SubmitError.StoreCannotBeEmpty
                    } else {
                        state.submitError = SubmitError.None
                        newPriceScreenViewModel.submitForm(
                            productName,
                            productQuantityAmountText,
                            productQuantityUnit,
                            productCategoryId,
                            priceAmountText,
                            saleQuantity,
                            priceIsSale,
                            priceStoreId,
                        )
                    }
                }
            }
        },
        onProductNameChange = {
            state.productName = it

            if (it.isNotBlank()) {
                newPriceScreenViewModel.query = it
                productSuggestionsPagingItems.refresh()

                state.wantToShowSuggestionBox = true
            } else {
                state.wantToShowSuggestionBox = false
            }
        },
        onProductQuantityAmountChange = {
            state.productQuantityAmountText = it
        },
        onProductQuantityUnitChange = {
            state.productQuantityUnit = it
        },
        onProductAutoSuggestClick = {
            state.productName = it.name
            state.productQuantityAmountText = it.quantity.amount.toString()
            state.productQuantityUnit = it.quantity.unit
            state.productCategoryId = it.categoryId
            state.wantToShowSuggestionBox = false
            focusManager.clearFocus()
        },
        onCategoryClick = {
            focusManager.clearFocus()
            keyboardController?.hide()
            navigateToSelectCategory(state.productCategoryId)
        },
        onPriceAmountChange = {
            state.priceAmountText = it
        },
        onSaleQuantityChange = {
            state.saleQuantity = it
        },
        onIsSaleClick = {
            state.priceIsSale = it
        },
        onStoreButtonClick = {
            focusManager.clearFocus()
            keyboardController?.hide()
            navigateToSelectStore(state.priceStoreId)
        },
    )

    when (newPriceScreenViewModel.submitResultState) {
        is LoadingState.Success -> {
            LaunchedEffect(key1 = Unit) {
                keyboardController?.hide()
                navigateUp()
            }
        }

        is LoadingState.Error -> {
            val unknownErrorMessage =
                stringResource(id = R.string.new_price_submit_failed_message)
            LaunchedEffect(Unit) {
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = unknownErrorMessage,
                    withDismissAction = true
                )
                when (snackbarResult) {
                    SnackbarResult.Dismissed -> {
                        newPriceScreenViewModel.clearError()
                    }

                    else -> {}
                }
            }
        }

        else -> {}
    }

    if (state.isDiscardDialogShown) {
        DiscardAlertDialog(
            onConfirmButtonClick = { state.isDiscardDialogShown = false },
            onCancelButtonClick = {
                state.isDiscardDialogShown = false
                keyboardController?.hide()
                navigateUp()
            }
        )
    }

    val isImeVisible = WindowInsets.isImeVisible
    BackHandler {
        log.debug("NewPriceScreen: BackHandler")
        if (isImeVisible) {
            keyboardController?.hide()
        } else if (state.wantToShowSuggestionBox && productSuggestionsPagingItems.itemCount > 0) {
            state.wantToShowSuggestionBox = false
        } else if (state.hasModifications()) {
            state.isDiscardDialogShown = true
        } else {
            navigateUp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainLayout(
    viewModelState: NewPriceScreenViewModelState,
    state: NewPriceScreenStateHolder,
    snackbarHostState: SnackbarHostState,
    productSuggestionsPagingItems: LazyPagingItems<Product>,
    onCloseButtonClick: () -> Unit,
    onSaveButtonClick: () -> Unit,
    onProductNameChange: (String) -> Unit,
    onProductQuantityAmountChange: (String) -> Unit,
    onProductQuantityUnitChange: (ProductQuantityUnit) -> Unit,
    onProductAutoSuggestClick: (Product) -> Unit,
    onCategoryClick: () -> Unit,
    onPriceAmountChange: (String) -> Unit,
    onSaleQuantityChange: (SaleQuantity) -> Unit,
    onIsSaleClick: (Boolean) -> Unit,
    onStoreButtonClick: () -> Unit,
) {
    val topBarScrollState = rememberTopAppBarState()
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topBarScrollState)

    UseAnimatedFadeTopBarColorForStatusBarColor(topBarScrollState)
    UseDefaultSystemNavBarColor()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    CloseButton(
                        onClick = onCloseButtonClick,
                        modifier = Modifier
                            .padding(14.dp)
                            .size(24.dp, 24.dp)
                    )
                },
                title = {
                    Text(text = stringResource(id = R.string.new_price_title))
                },
                actions = {
                    SaveButton(
                        onClick = onSaveButtonClick,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, end = 10.dp)
                    )
                },
                scrollBehavior = topBarScrollBehavior,
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) {
        val scrollState = rememberScrollState()
        var productNameLayoutBounds by remember { mutableStateOf(Rect.Companion.Zero) }
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
                .verticalScroll(scrollState)
        ) {
            val focusRequester = remember { FocusRequester() }

            ProductInformationHeader(modifier = Modifier.padding(bottom = 4.dp))

            ProductNameField(
                productName = state.productName,
                onProductNameChange = { text ->
                    onProductNameChange(text.take(100))
                },
                focusRequester = focusRequester,
                scrollState = scrollState,
                submitError = state.submitError,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { layoutCoordinates ->
                        productNameLayoutBounds = layoutCoordinates.boundsInParent()
                    }
                    .onFocusChanged { focusState ->
                        if (!focusState.hasFocus) {
                            state.wantToShowSuggestionBox = false
                        }
                    },
            )
            LaunchedEffect(key1 = Unit) {
                if (state.isRequestingFirstFocus) {
                    state.isRequestingFirstFocus = false
                    focusRequester.requestFocus()
                }
            }

            ProductQuantityField(
                amountText = state.productQuantityAmountText,
                onAmountTextChange = { text -> onProductQuantityAmountChange(text.take(10)) },
                unit = state.productQuantityUnit,
                onUnitChange = { unit -> onProductQuantityUnitChange(unit) },
                scrollState = scrollState,
                submitError = state.submitError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )

            val category by viewModelState.categoryFlow.collectAsState()
            ProductCategoryField(
                productCategory = category,
                onClick = onCategoryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 4.dp, bottom = 4.dp)
            )

            PriceHeader(
                modifier = Modifier.padding(vertical = 6.dp)
            )

            val preferredCurrency by viewModelState.preferredCurrencyFlow.collectAsState()
            PriceAmountField(
                text = state.priceAmountText,
                onTextChange = { text -> onPriceAmountChange(text.take(10)) },
                preferredCurrency = preferredCurrency,
                scrollState = scrollState,
                submitError = state.submitError,
                modifier = Modifier
                    .fillMaxWidth()
            )

            SaleQuantityField(
                saleQuantity = state.saleQuantity,
                onSaleQuantityChange = onSaleQuantityChange,
                modifier = Modifier
                    .fillMaxWidth()
            )

            PriceIsSaleField(
                isChecked = state.priceIsSale,
                onCheckedChange = { isChecked -> onIsSaleClick(isChecked) },
                modifier = Modifier.fillMaxWidth()
            )

            StoreLocationHeader(
                modifier = Modifier.padding(vertical = 6.dp)
            )

            val store by viewModelState.storeFlow.collectAsState()
            StoreLocationField(
                selectedStore = store,
                onStoreButtonClick = onStoreButtonClick,
                scrollState = scrollState,
                submitError = state.submitError,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        if (state.wantToShowSuggestionBox && productSuggestionsPagingItems.itemCount > 0) {
            LaunchedEffect(Unit) {
                scrollState.animateScrollTo(0)
                snapshotFlow { scrollState.isScrollInProgress }
                    .filter { isScrollInProgress -> isScrollInProgress }
                    .collect {
                        state.wantToShowSuggestionBox = false
                    }
            }

            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                val density = LocalDensity.current
                Surface(
                    shape = RoundedCornerShape(10),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .offset(y = Dp(productNameLayoutBounds.bottom / density.density) - 8.dp)
                        .padding(8.dp)
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    LazyColumn {
                        items(
                            items = productSuggestionsPagingItems,
                            key = { product -> product.id }
                        ) { product ->
                            if (product != null) {
                                ProductSuggestionListItem(
                                    query = state.productName,
                                    product = product,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onProductAutoSuggestClick(product) }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductInformationHeader(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = R.string.new_price_product_info_header),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Composable
private fun ProductNameField(
    productName: String,
    onProductNameChange: (String) -> Unit,
    focusRequester: FocusRequester,
    scrollState: ScrollState,
    submitError: SubmitError,
    modifier: Modifier = Modifier,
) {
    var productNameScrollPosition by remember { mutableStateOf(0f) }
    Column(
        modifier = modifier
            .onGloballyPositioned { layoutCoordinates ->
                productNameScrollPosition = layoutCoordinates.positionInParent().y
            }
    ) {
        val focusManager = LocalFocusManager.current
        OutlinedTextField(
            value = productName,
            onValueChange = onProductNameChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            singleLine = true,
            label = {
                Text(text = stringResource(id = R.string.new_price_product_name_label))
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            isError = submitError == SubmitError.ProductNameShouldNotBeEmpty
        )

        AnimatedVisibility(visible = submitError == SubmitError.ProductNameShouldNotBeEmpty) {
            Text(
                text = stringResource(id = R.string.new_price_product_name_empty_error_text),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (submitError == SubmitError.ProductNameShouldNotBeEmpty) {
            LaunchedEffect(Unit) {
                scrollState.animateScrollTo(productNameScrollPosition.roundToInt())
            }
        }
    }
}

@Composable
private fun ProductSuggestionListItem(
    query: String,
    product: Product,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier.height(40.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                // TODO bold the query
//                val queryText = query.filter { it.isLetter() }
//                product.name.find
//                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
//                    append(query.())
//                }
                append(product.name)
//                product.name
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = product.quantity.getDisplayString(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.alpha(0.6f)
        )
    }
}

@Composable
private fun ProductQuantityField(
    amountText: String,
    onAmountTextChange: (String) -> Unit,
    unit: ProductQuantityUnit,
    onUnitChange: (ProductQuantityUnit) -> Unit,
    scrollState: ScrollState,
    submitError: SubmitError,
    modifier: Modifier
) {
    var quantityFieldScrollPosition by remember { mutableStateOf(0f) }
    Column(
        modifier = modifier
            .onGloballyPositioned { layoutCoordinates ->
                quantityFieldScrollPosition = layoutCoordinates.positionInParent().y
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val focusManager = LocalFocusManager.current
            OutlinedTextField(
                value = amountText,
                onValueChange = onAmountTextChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.new_price_product_quantity_label))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                isError = submitError == SubmitError.InvalidPriceAmount
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth(0.21f)
            ) {
                var expanded by remember { mutableStateOf(false) }
                TextButton(
                    onClick = { expanded = !expanded },
                    shape = RoundedCornerShape(4.dp),
                ) {
                    Text(
                        text = unit.getShortDisplayString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    ProductQuantityUnit.values().map {
                        val selectedColors = if (it == unit) {
                            MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.primary,
                                trailingIconColor = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            MenuDefaults.itemColors()
                        }
                        DropdownMenuItem(
                            text = { Text(text = it.getDisplayString()) },
                            trailingIcon = {
                                if (it == unit) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_baseline_check_24),
                                        contentDescription = null
                                    )
                                }
                            },
                            onClick = {
                                onUnitChange(it)
                                expanded = false
                            },
                            colors = selectedColors
                        )
                    }
                }
            }
        }

        AnimatedVisibility(visible = submitError == SubmitError.InvalidProductQuantityAmount) {
            Text(
                text = stringResource(id = R.string.new_price_product_quantity_input_error_text),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (submitError == SubmitError.InvalidProductQuantityAmount) {
            LaunchedEffect(Unit) {
                scrollState.animateScrollTo(quantityFieldScrollPosition.roundToInt())
            }
        }
    }
}

@Composable
private fun ProductCategoryField(
    productCategory: LoadingState<Category?>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onClick() },
    ) {
        val category = if (productCategory is LoadingState.Success)
            productCategory.data ?: NoCategory
        else
            NoCategory
        Image(
            painter = painterResource(id = category.icon.iconRes),
            contentDescription = stringResource(id = R.string.new_price_product_category_icon_content_description),
            modifier = Modifier
                .padding(start = 6.dp, end = 12.dp)
                .size(30.dp)
        )

        Text(
            text = category.name,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun PriceHeader(
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(id = R.string.new_price_price_info_header),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Composable
private fun PriceAmountField(
    text: String,
    onTextChange: (String) -> Unit,
    preferredCurrency: LoadingState<Currency>,
    scrollState: ScrollState,
    submitError: SubmitError,
    modifier: Modifier = Modifier
) {
    var priceTextFieldScrollPosition by remember { mutableStateOf(0f) }
    Column(
        modifier = modifier
            .onGloballyPositioned { layoutCoordinates ->
                priceTextFieldScrollPosition = layoutCoordinates.positionInParent().y
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val focusManager = LocalFocusManager.current
            OutlinedTextField(
                value = text,
                onValueChange = { text -> onTextChange(text) },
                modifier = Modifier
                    .weight(1f),
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.new_price_amount_label))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                leadingIcon = {
                    preferredCurrency.ifLoaded {
                        Text(text = it.symbol)
                    }
                },
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                isError = submitError == SubmitError.InvalidPriceAmount
            )
        }

        AnimatedVisibility(visible = submitError == SubmitError.InvalidPriceAmount) {
            Text(
                text = stringResource(id = R.string.new_price_amount_input_error_text),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (submitError == SubmitError.InvalidPriceAmount) {
            LaunchedEffect(Unit) {
                scrollState.animateScrollTo(priceTextFieldScrollPosition.roundToInt())
            }
        }
    }
}

@Composable
private fun SaleQuantityField(
    saleQuantity: SaleQuantity,
    onSaleQuantityChange: (SaleQuantity) -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.new_price_sale_quantity_label),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Box {
            var expanded by remember { mutableStateOf(false) }
            TextButton(
                onClick = { expanded = !expanded },
                shape = RoundedCornerShape(4.dp),
            ) {
                Text(
                    text = saleQuantity.getShortDisplayString(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                ProductQuantityUnit.values().map {
                    val selectedColors = if (it == unit) {
                        MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.primary,
                            trailingIconColor = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        MenuDefaults.itemColors()
                    }
                    DropdownMenuItem(
                        text = { Text(text = it.getDisplayString()) },
                        trailingIcon = {
                            if (it == unit) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_check_24),
                                    contentDescription = null
                                )
                            }
                        },
                        onClick = {
                            onUnitChange(it)
                            expanded = false
                        },
                        colors = selectedColors
                    )
                }
            }
        }
    }
}

@Composable
private fun PriceIsSaleField(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onCheckedChange(!isChecked) }
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
        )

        Text(
            text = stringResource(id = R.string.new_price_is_sale_label),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun StoreLocationHeader(
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(id = R.string.new_price_store_info_header),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Composable
private fun StoreLocationField(
    selectedStore: LoadingState<Store?>,
    onStoreButtonClick: () -> Unit,
    scrollState: ScrollState,
    submitError: SubmitError,
    modifier: Modifier = Modifier
) {
    var storeLocationFieldScrollPosition by remember { mutableStateOf(0f) }
    Column(
        modifier = modifier
            .onGloballyPositioned { layoutCoordinates ->
                storeLocationFieldScrollPosition = layoutCoordinates.positionInParent().y
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            selectedStore.ifLoaded {
                if (it == null) {
                    FilledTonalButton(
                        onClick = onStoreButtonClick,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_place_24),
                            contentDescription = stringResource(id = R.string.new_price_store_icon_content_description),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .size(24.dp),
                        )

                        Text(
                            text = stringResource(id = R.string.new_price_store_button_placeholder_text)
                        )
                    }
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_place_24),
                        contentDescription = stringResource(id = R.string.new_price_store_icon_content_description),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(24.dp),
                    )

                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onStoreButtonClick() }
                    ) {
                        Text(
                            text = it.name,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Text(
                            text = it.address.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

        }

        AnimatedVisibility(visible = submitError == SubmitError.StoreCannotBeEmpty) {
            Text(
                text = stringResource(id = R.string.new_price_store_not_selected_error_text),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (submitError == SubmitError.StoreCannotBeEmpty) {
            LaunchedEffect(Unit) {
                scrollState.animateScrollTo(storeLocationFieldScrollPosition.roundToInt())
            }
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    val viewModelState = object : NewPriceScreenViewModelState {
        override val productFlow: StateFlow<LoadingState<Product>> =
            MutableStateFlow(LoadingState.NotLoading())
        override val categoryFlow: StateFlow<LoadingState<Category?>> =
            MutableStateFlow(LoadingState.Success(null))
        override val preferredCurrencyFlow: StateFlow<LoadingState<Currency>> =
            MutableStateFlow(LoadingState.Success(Currency.getInstance("USD")))
        override val storeFlow: StateFlow<LoadingState<Store?>> =
            MutableStateFlow(LoadingState.Success(null))

        override val submitResultState: LoadingState<Unit> = LoadingState.NotLoading()

        override fun updateCategoryId(categoryId: CategoryId?) {}
        override fun updateStoreId(storeId: StoreId?) {}
    }

    val productsPagingItems = flowOf(
        PagingData.from(
            listOf(
                Product(
                    id = ProductId(0),
                    name = "Apple",
                    quantity = ProductQuantity(1.0, ProductQuantityUnit.Pounds),
                    categoryId = CategoryId(1),
                    creationTimestamp = 0,
                    updateTimestamp = 0,
                )
            )
        )
    ).collectAsLazyPagingItems()

    val state by rememberNewPriceScreenState(
        NewPriceScreenArgs(null, null),
        viewModelState,
        SelectCategoryScreenResultViewModel(SavedStateHandle()),
        SelectStoreScreenResultViewModel(SavedStateHandle()),
    )
    MainLayout(
        viewModelState = viewModelState,
        state = state,
        snackbarHostState = SnackbarHostState(),
        productSuggestionsPagingItems = productsPagingItems,
        onCloseButtonClick = {},
        onSaveButtonClick = {},
        onProductNameChange = {},
        onProductQuantityAmountChange = {},
        onProductQuantityUnitChange = {},
        onProductAutoSuggestClick = {},
        onCategoryClick = {},
        onPriceAmountChange = {},
        onSaleQuantityChange = {},
        onIsSaleClick = {},
        onStoreButtonClick = {},
    )
}