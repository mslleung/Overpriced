package com.igrocery.overpriced.presentation.newprice

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import com.igrocery.overpriced.presentation.newprice.NewPriceScreenViewModel.SubmitFormResultState
import com.igrocery.overpriced.presentation.selectcategory.SelectCategoryDialog
import com.igrocery.overpriced.presentation.selectcategory.SelectCategoryDialogViewModel
import com.igrocery.overpriced.presentation.selectstore.SelectStoreDialog
import com.igrocery.overpriced.presentation.selectstore.SelectStoreDialogViewModel
import com.igrocery.overpriced.presentation.shared.CloseButton
import com.igrocery.overpriced.presentation.shared.SaveButton
import com.igrocery.overpriced.shared.Logger
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.shared.NoCategory
import kotlinx.coroutines.flow.flowOf
import java.util.*
import kotlin.math.roundToInt

@Suppress("unused")
private val log = Logger { }

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NewPriceScreen(
    newPriceScreenViewModel: NewPriceScreenViewModel,
    selectCategoryDialogViewModel: SelectCategoryDialogViewModel,
    selectStoreDialogViewModel: SelectStoreDialogViewModel,
    navigateUp: () -> Unit,
    navigateToScanBarcode: () -> Unit,
    navigateToNewCategory: () -> Unit,
    navigateToEditCategory: (Category) -> Unit,
    navigateToNewStore: () -> Unit,
    navigateToEditStore: (Store) -> Unit,
) {
    log.debug("Composing NewPriceScreen")

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
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val productName by newPriceScreenViewModel.productNameFlow.collectAsState()
    val productDescription by newPriceScreenViewModel.productDescriptionFlow.collectAsState()
    val productSuggestionsPagingItems =
        newPriceScreenViewModel.productsPagedFlow.collectAsLazyPagingItems()
    val attachedBarcode by newPriceScreenViewModel.attachedBarcodeFlow.collectAsState()
    val productCategory by newPriceScreenViewModel.productCategoryFlow.collectAsState()
    val preferredCurrency by newPriceScreenViewModel.preferredCurrencyFlow.collectAsState()
    val storesCount by newPriceScreenViewModel.storesCountFlow.collectAsState()
    val selectedStore by newPriceScreenViewModel.selectedStoreFlow.collectAsState()
    val state by rememberNewPriceScreenState()
    MainLayout(
        productName = productName,
        productDescription = productDescription,
        productSuggestionsPagingItems = productSuggestionsPagingItems,
        productCategory = productCategory,
        attachedBarcode = attachedBarcode,
        preferredCurrency = preferredCurrency,
        selectedStore = selectedStore,
        submitResult = newPriceScreenViewModel.submitFormResult,
        state = state,
        onCloseButtonClick = {
            if (state.hasModifications() || newPriceScreenViewModel.hasModifications()) {
                state.isDiscardDialogShown = true
            } else {
                keyboardController?.hide()
                navigateUp()
            }
        },
        onSaveButtonClick = {
            newPriceScreenViewModel.submitForm(
                productName.trim(),
                productDescription.trim(),
                attachedBarcode?.trim(),
                productCategory?.id,
                state.priceAmountText.trim(),
                selectedStore,
            )
        },
        onProductNameChange = {
            newPriceScreenViewModel.setProductName(it)

            if (it.isNotBlank()) {
                state.wantToShowSuggestionBox = true
                productSuggestionsPagingItems.refresh()
            } else {
                state.wantToShowSuggestionBox = false
            }
        },
        onProductDescriptionChange = {
            newPriceScreenViewModel.setProductDescription(it)
        },
        onProductAutoSuggestClick = {
            newPriceScreenViewModel.setProductName(it.name)
            newPriceScreenViewModel.setProductDescription(it.description)
            newPriceScreenViewModel.setBarcode(it.barcode)
            newPriceScreenViewModel.setProductCategoryId(it.categoryId)
            state.wantToShowSuggestionBox = false
            focusManager.clearFocus()
        },
        onAttachBarcodeButtonClick = {
            keyboardController?.hide()
            navigateToScanBarcode()
        },
        onCategoryClick = {
            keyboardController?.hide()
            state.isSelectCategoryDialogShown = true
        },
        onStoreButtonClick = {
            keyboardController?.hide()
            if (storesCount == 0) {
                keyboardController?.hide()
                navigateToNewStore()
            } else {
                state.isSelectStoreDialogShown = true
            }
        },
        onSubmitErrorDismissed = { newPriceScreenViewModel.submitFormResult = null },
    )

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

    if (state.isSelectStoreDialogShown) {
        SelectStoreDialog(
            viewModel = selectStoreDialogViewModel,
            selectedStoreId = selectedStore?.id ?: 0,
            onDismiss = { state.isSelectStoreDialogShown = false },
            onStoreSelect = {
                state.isSelectStoreDialogShown = false
                newPriceScreenViewModel.selectStore(it.id)
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

    if (state.isSelectCategoryDialogShown) {
        SelectCategoryDialog(
            viewModel = selectCategoryDialogViewModel,
            selectedCategoryId = productCategory?.id ?: 0L,
            onDismiss = { state.isSelectCategoryDialogShown = false },
            onCategorySelect = {
                state.isSelectCategoryDialogShown = false
                newPriceScreenViewModel.setProductCategoryId(it.id)
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

    if (newPriceScreenViewModel.submitFormResult is SubmitFormResultState.Success) {
        LaunchedEffect(key1 = Unit) {
            keyboardController?.hide()
            navigateUp()
        }
    }

    BackHandler {
        if (state.wantToShowSuggestionBox && productSuggestionsPagingItems.itemCount > 0) {
            state.wantToShowSuggestionBox = false
        } else if (state.hasModifications() || newPriceScreenViewModel.hasModifications()) {
            state.isDiscardDialogShown = true
        } else {
            keyboardController?.hide()
            navigateUp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainLayout(
    productName: String,
    productDescription: String,
    productSuggestionsPagingItems: LazyPagingItems<Product>,
    attachedBarcode: String?,
    productCategory: Category?,
    preferredCurrency: Currency,
    selectedStore: Store?,
    submitResult: SubmitFormResultState?,
    state: NewPriceScreenStateHolder,
    onCloseButtonClick: () -> Unit,
    onSaveButtonClick: () -> Unit,
    onProductNameChange: (String) -> Unit,
    onProductDescriptionChange: (String) -> Unit,
    onProductAutoSuggestClick: (Product) -> Unit,
    onAttachBarcodeButtonClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onStoreButtonClick: () -> Unit,
    onSubmitErrorDismissed: () -> Unit,
) {
    val topBarScrollState = rememberTopAppBarState()
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topBarScrollState)
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            SmallTopAppBar(
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
                modifier = Modifier.statusBarsPadding()
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
            )
        }
    ) {
        val scrollState = rememberScrollState()
        var productNameLayoutBounds by remember { mutableStateOf(Rect.Companion.Zero) }
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
                .imePadding()
                .fillMaxSize()
                .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
                .verticalScroll(scrollState)
        ) {
            ProductInformationHeader(modifier = Modifier.padding(bottom = 4.dp))

            var productNameScrollPosition by remember { mutableStateOf(0f) }
            ProductNameTextField(
                productName = { productName },
                onProductNameChange = { text ->
                    onProductNameChange(text.take(100))
                },
                requestFocus = { state.isRequestingFirstFocus },
                onFocusRequested = { state.isRequestingFirstFocus = false },
                isError = { submitResult == SubmitFormResultState.Error(SubmitFormResultState.ErrorReason.NameEmptyError) },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { layoutCoordinates ->
                        productNameLayoutBounds = layoutCoordinates.boundsInParent()
                        productNameScrollPosition = layoutCoordinates.positionInParent().y
                    }
                    .onFocusChanged { focusState ->
                        if (!focusState.hasFocus) {
                            state.wantToShowSuggestionBox = false
                        }
                    },
            )

            ProductDescriptionTextField(
                productDescription = { productDescription },
                onProductDescriptionChange = { text ->
                    onProductDescriptionChange(text.take(100))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )

            // TODO
//            ProductBarcode(
//                productBarcode = attachedBarcode ?: "",
//                onAttachBarcodeButtonClick = onAttachBarcodeButtonClick
//            )

            ProductCategory(
                productCategory = productCategory,
                onClick = onCategoryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 4.dp, bottom = 4.dp)
            )

            PriceHeader(
                modifier = Modifier.padding(vertical = 6.dp)
            )

            var priceTextFieldScrollPosition by remember { mutableStateOf(0f) }
            PriceTextFieldButton(
                text = { state.priceAmountText },
                onTextChange = { text ->
                    if (text.length > 100) {
                        state.priceAmountText = text.substring(0, 10)
                    } else {
                        state.priceAmountText = text
                    }
                },
                preferredCurrency = preferredCurrency,
                isInputError = { submitResult == SubmitFormResultState.Error(SubmitFormResultState.ErrorReason.PriceAmountInputError) },
                isInvalidError = { submitResult == SubmitFormResultState.Error(SubmitFormResultState.ErrorReason.PriceAmountInvalidError) },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { layoutCoordinates ->
                        priceTextFieldScrollPosition = layoutCoordinates.positionInParent().y
                    }
                    .padding(bottom = 4.dp)
            )

            StoreLocationHeader(
                modifier = Modifier.padding(vertical = 6.dp)
            )

            var storeLocationFieldScrollPosition by remember { mutableStateOf(0f) }
            StoreLocation(
                selectedStore = selectedStore,
                onStoreButtonClick = onStoreButtonClick,
                isError = { submitResult == SubmitFormResultState.Error(SubmitFormResultState.ErrorReason.StoreNotSelectedError) },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { layoutCoordinates ->
                        storeLocationFieldScrollPosition = layoutCoordinates.positionInParent().y
                    }
            )

            if (submitResult is SubmitFormResultState.Error) {
                val unknownErrorMessage =
                    stringResource(id = R.string.new_price_submit_failed_message)
                when (submitResult.reason) {
                    SubmitFormResultState.ErrorReason.NameEmptyError -> {
                        LaunchedEffect(Unit) {
                            scrollState.animateScrollTo(productNameScrollPosition.roundToInt())
                        }
                    }
                    SubmitFormResultState.ErrorReason.PriceAmountInputError,
                    SubmitFormResultState.ErrorReason.PriceAmountInvalidError -> {
                        LaunchedEffect(Unit) {
                            scrollState.animateScrollTo(priceTextFieldScrollPosition.roundToInt())
                        }
                    }
                    SubmitFormResultState.ErrorReason.StoreNotSelectedError -> {
                        LaunchedEffect(Unit) {
                            scrollState.animateScrollTo(storeLocationFieldScrollPosition.roundToInt())
                        }
                    }
                    SubmitFormResultState.ErrorReason.UnknownError -> {
                        LaunchedEffect(Unit) {
                            val snackbarResult = snackbarHostState.showSnackbar(
                                message = unknownErrorMessage,
                                withDismissAction = true
                            )
                            when (snackbarResult) {
                                SnackbarResult.Dismissed -> {
                                    onSubmitErrorDismissed()
                                }
                                else -> {}
                            }
                        }
                    }
                }
            }
        }

        if (state.wantToShowSuggestionBox && productSuggestionsPagingItems.itemCount > 0) {
            Box(
                modifier = Modifier
                    .padding(it)
                    .navigationBarsPadding()
                    .imePadding()
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
                                    query = productName,
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
    productName: () -> String,
    onProductNameChange: (String) -> Unit,
    requestFocus: () -> Boolean,
    onFocusRequested: () -> Unit,
    isError: () -> Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        val focusManager = LocalFocusManager.current
        val focusRequester = remember { FocusRequester() }
        OutlinedTextField(
            value = productName(),
            onValueChange = { text ->
                onProductNameChange(text)
            },
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
            isError = isError()
        )
        if (requestFocus()) {
            onFocusRequested()
            LaunchedEffect(key1 = Unit) {
                focusRequester.requestFocus()
            }
        }

        AnimatedVisibility(visible = isError()) {
            Text(
                text = stringResource(id = R.string.new_price_product_name_empty_error_text),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDescriptionTextField(
    productDescription: () -> String,
    onProductDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = productDescription(),
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
private fun ProductBarcode(
    productBarcode: String,
    onAttachBarcodeButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (productBarcode.isBlank()) {
        TextButton(
            onClick = onAttachBarcodeButtonClick,
            modifier = modifier
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_camera_alt_24),
                contentDescription = stringResource(id = R.string.new_price_product_barcode_icon_content_description),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(24.dp)
            )
            Text(text = stringResource(id = R.string.new_price_product_barcode_attach_button_text))
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.new_price_product_barcode_label),
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                text = productBarcode,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            BarcodeCameraButton(
                onClick = onAttachBarcodeButtonClick,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun BarcodeCameraButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_camera_alt_24),
            contentDescription = stringResource(id = R.string.new_price_product_barcode_icon_content_description)
        )
    }
}

@Composable
private fun ProductCategory(
    productCategory: Category?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onClick() },
    ) {
        val category = productCategory ?: NoCategory
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
    text: () -> String,
    onTextChange: (String) -> Unit,
    preferredCurrency: Currency,
    isInputError: () -> Boolean,
    isInvalidError: () -> Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(bottom = 4.dp)
        ) {
            val focusManager = LocalFocusManager.current
            OutlinedTextField(
                value = text(),
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
                    Text(text = preferredCurrency.symbol)
                },
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                isError = isInputError() || isInvalidError()
            )
        }

        AnimatedVisibility(visible = isInputError() || isInvalidError()) {
            Text(
                text = stringResource(id = R.string.new_price_amount_input_error_text),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
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
    selectedStore: Store?,
    onStoreButtonClick: () -> Unit,
    isError: () -> Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {

            if (selectedStore == null) {
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
                        text = selectedStore.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = selectedStore.address.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        AnimatedVisibility(visible = isError()) {
            Text(
                text = stringResource(id = R.string.new_price_store_not_selected_error_text),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    val productsPagingItems = flowOf(
        PagingData.from(
            listOf(
                Product(
                    id = 0,
                    name = "Apple",
                    description = "Pack of 6",
                    barcode = null,
                    categoryId = 0L,
                    creationTimestamp = 0,
                    updateTimestamp = 0,
                )
            )
        )
    ).collectAsLazyPagingItems()

    MainLayout(
        productName = "",
        productDescription = "",
        productSuggestionsPagingItems = productsPagingItems,
        attachedBarcode = "",
        productCategory = Category(icon = CategoryIcon.Carrot, name = "Vegetables"),
        preferredCurrency = Currency.getInstance(Locale.getDefault()),
        selectedStore = null,
        submitResult = null,
        state = NewPriceScreenStateHolder(),
        onCloseButtonClick = {},
        onSaveButtonClick = {},
        onProductNameChange = {},
        onProductDescriptionChange = {},
        onProductAutoSuggestClick = {},
        onAttachBarcodeButtonClick = {},
        onCategoryClick = {},
        onStoreButtonClick = {},
        onSubmitErrorDismissed = {}
    )
}