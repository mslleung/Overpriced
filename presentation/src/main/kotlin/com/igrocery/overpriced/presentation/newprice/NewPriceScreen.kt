package com.igrocery.overpriced.presentation.newprice

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.newprice.NewPriceScreenStateHolder.SubmitError
import com.igrocery.overpriced.presentation.shared.*
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import java.util.*
import kotlin.math.roundToInt

@Suppress("unused")
private val log = Logger { }

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Composable
fun NewPriceScreen(
    savedStateHandle: SavedStateHandle,
    newPriceScreenViewModel: NewPriceScreenViewModel,
    navigateUp: () -> Unit,
    navigateToNewCategory: () -> Unit,
    navigateToEditCategory: (Category) -> Unit,
    navigateToNewStore: () -> Unit,
    navigateToEditStore: (Store) -> Unit,
) {
    log.debug("Composing NewPriceScreen")

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val coroutineScope = rememberCoroutineScope()
    val state by rememberNewPriceScreenState(savedStateHandle, coroutineScope, newPriceScreenViewModel)
    val productSuggestionsPagingItems =
        newPriceScreenViewModel.suggestedProductsPagingDataFlow.collectAsLazyPagingItems()
    val storesCount by newPriceScreenViewModel.storesCountFlow.collectAsState()
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
                } else if (priceAmountText.toDoubleOrNull() == null || priceAmountText.toDouble() !in 0.0..1000000.0) {
                    submitError = SubmitError.InvalidPriceAmount
                } else {
                    val price = priceStoreId
                    if (price == null) {
                        submitError = SubmitError.StoreCannotBeEmpty
                    } else {
                        state.submitError = SubmitError.None
                        newPriceScreenViewModel.submitForm(
                            productName.trim(),
                            productDescription.trim(),
                            productCategoryId,
                            priceAmountText.trim(),
                            price,
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
        onProductDescriptionChange = {
            state.productDescription = it.take(100)
        },
        onProductAutoSuggestClick = {
            state.productName = it.name
            state.productDescription = it.description
            state.productCategoryId = it.categoryId
            state.wantToShowSuggestionBox = false
            focusManager.clearFocus()
        },
        onCategoryClick = {
            keyboardController?.hide()
            state.isSelectCategoryDialogShown = true
        },
        onStoreButtonClick = {
            keyboardController?.hide()
            storesCount.let {
                if (it is LoadingState.Success) {
                    if (it.data == 0) {
                        keyboardController?.hide()
                        navigateToNewStore()
                    } else {
                        state.isSelectStoreDialogShown = true
                    }
                }
            }
        },
    )

    if (state.isSelectCategoryDialogShown) {
        val selectCategoryDialogViewModel = hiltViewModel<SelectCategoryDialogViewModel>()
        SelectCategoryDialog(
            viewModel = selectCategoryDialogViewModel,
            selectedCategoryId = state.productCategoryId,
            onDismiss = { state.isSelectCategoryDialogShown = false },
            onCategorySelect = {
                state.isSelectCategoryDialogShown = false
                state.productCategoryId = it.id
            },
            onEditCategoryClick = {
                state.isSelectCategoryDialogShown = false
                navigateToEditCategory(it)
            },
            onNewCategoryClick = {
                state.isSelectCategoryDialogShown = false
                navigateToNewCategory()
            },
        )
    }

    if (state.isSelectStoreDialogShown) {
        val selectStoreDialogViewModel = hiltViewModel<SelectStoreDialogViewModel>()
        SelectStoreDialog(
            viewModel = selectStoreDialogViewModel,
            selectedStoreId = state.priceStoreId,
            onDismiss = { state.isSelectStoreDialogShown = false },
            onStoreSelect = {
                state.isSelectStoreDialogShown = false
                state.priceStoreId = it.id
            },
            onEditStoreClick = {
                state.isSelectStoreDialogShown = false
                keyboardController?.hide()
                navigateToEditStore(it)
            },
            onNewStoreClick = {
                state.isSelectStoreDialogShown = false
                keyboardController?.hide()
                navigateToNewStore()
            },
        )
    }

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
    onProductDescriptionChange: (String) -> Unit,
    onProductAutoSuggestClick: (Product) -> Unit,
    onCategoryClick: () -> Unit,
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

            ProductNameTextField(
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

            ProductDescriptionTextField(
                productDescription = state.productDescription,
                onProductDescriptionChange = { text ->
                    onProductDescriptionChange(text.take(100))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )

            val category by viewModelState.categoryFlow.collectAsState()
            ProductCategory(
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
            PriceTextFieldButton(
                text = state.priceAmountText,
                onTextChange = { text ->
                    if (text.length > 100) {
                        state.priceAmountText = text.substring(0, 10)
                    } else {
                        state.priceAmountText = text
                    }
                },
                preferredCurrency = preferredCurrency,
                scrollState = scrollState,
                submitError = state.submitError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )

            StoreLocationHeader(
                modifier = Modifier.padding(vertical = 6.dp)
            )

            val store by viewModelState.storeFlow.collectAsState()
            StoreLocation(
                selectedStore = store,
                onStoreButtonClick = onStoreButtonClick,
                scrollState = scrollState,
                submitError = state.submitError,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        if (state.wantToShowSuggestionBox && productSuggestionsPagingItems.itemCount > 0) {
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
fun ProductSuggestionListItem(
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

        if (product.description.isNotBlank()) {
            Text(
                text = product.description,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alpha(0.6f)
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductNameTextField(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDescriptionTextField(
    productDescription: String,
    onProductDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = productDescription,
        onValueChange = { text -> onProductDescriptionChange(text) },
        modifier = modifier,
        singleLine = true,
        label = {
            Text(text = stringResource(id = R.string.new_price_product_description_label))
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = {
            focusManager.moveFocus(FocusDirection.Down)
        }),
    )
}

@Composable
private fun ProductCategory(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PriceTextFieldButton(
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
            modifier = Modifier
                .padding(bottom = 4.dp)
        ) {
            val focusManager = LocalFocusManager.current
            OutlinedTextField(
                value = text,
                onValueChange = { text -> onTextChange(text) },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp),
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.new_price_amount_label))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
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
private fun StoreLocation(
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
        override val categoryFlow: StateFlow<LoadingState<Category?>> =
            MutableStateFlow(LoadingState.Success(null))
        override val preferredCurrencyFlow: StateFlow<LoadingState<Currency>> =
            MutableStateFlow(LoadingState.Success(Currency.getInstance("USD")))
        override val storesCountFlow: StateFlow<LoadingState<Int>> =
            MutableStateFlow(LoadingState.Success(5))
        override val storeFlow: StateFlow<LoadingState<Store?>> =
            MutableStateFlow(LoadingState.Success(null))

        override val submitResultState: LoadingState<Unit> = LoadingState.NotLoading()

        override fun updateCategoryId(categoryId: Long?) {}
        override fun updateStoreId(storeId: Long?) {}
    }

    val productsPagingItems = flowOf(
        PagingData.from(
            listOf(
                Product(
                    id = 0,
                    name = "Apple",
                    description = "Pack of 6",
                    categoryId = 0L,
                    creationTimestamp = 0,
                    updateTimestamp = 0,
                )
            )
        )
    ).collectAsLazyPagingItems()

    MainLayout(
        viewModelState = viewModelState,
        state = NewPriceScreenStateHolder(
            SavedStateHandle(),
            rememberCoroutineScope(),
            viewModelState
        ),
        snackbarHostState = SnackbarHostState(),
        productSuggestionsPagingItems = productsPagingItems,
        onCloseButtonClick = {},
        onSaveButtonClick = {},
        onProductNameChange = {},
        onProductDescriptionChange = {},
        onProductAutoSuggestClick = {},
        onCategoryClick = {},
        onStoreButtonClick = {},
    )
}