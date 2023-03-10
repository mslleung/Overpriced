package com.igrocery.overpriced.presentation.selectcategory

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.editcategory.ConfirmDeleteCategoryDialog
import com.igrocery.overpriced.presentation.selectcategory.SelectCategoryScreenStateHolder.CategoryMoreDialogData
import com.igrocery.overpriced.presentation.shared.*
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Suppress("unused")
private val log = Logger { }

@Composable
internal fun SelectCategoryScreen(
    args: SelectCategoryScreenArgs,
    viewModel: SelectCategoryScreenViewModel,
    navigateUp: () -> Unit,
    navigateUpWithResults: (CategoryId) -> Unit,
    navigateToNewCategory: () -> Unit,
    navigateToEditCategory: (CategoryId) -> Unit,
) {
    log.debug("Composing SelectCategoryScreen")

    val state by rememberSelectCategoryScreenState(args)
    MainLayout(
        viewModelState = viewModel,
        state = state,
        onBackButtonClick = navigateUp,
        onCategoryClick = navigateUpWithResults,
        onNewCategoryClick = navigateToNewCategory,
        onCategoryMoreClick = {
            state.categoryMoreDialogData = CategoryMoreDialogData(it)
        }
    )

    state.categoryMoreDialogData?.let { dialogData ->
        ListSelectionDialog(
            selections = listOf(
                stringResource(id = R.string.select_category_more_edit),
                stringResource(id = R.string.select_category_more_delete)
            ),
            onDismiss = {
                state.categoryMoreDialogData = null
            },
            onSelected = {
                when (it) {
                    0 -> {
                        navigateToEditCategory(dialogData.category.id)
                        state.categoryMoreDialogData = null
                    }
                    1 -> {
                        state.deleteCategoryDialogData =
                            SelectCategoryScreenStateHolder.DeleteCategoryDialogData(dialogData.category)
                        state.categoryMoreDialogData = null
                    }
                    else -> { throw NotImplementedError("selection $it not handled") }
                }
            }
        )
    }

    state.deleteCategoryDialogData?.let { dialogData ->
        ConfirmDeleteCategoryDialog(
            onDismiss = {
                state.deleteCategoryDialogData = null
            },
            onConfirm = {
                viewModel.deleteCategory(dialogData.category)
                state.deleteCategoryDialogData = null
            }
        )
    }

    BackHandler {
        log.debug("SelectCategoryScreen: BackHandler")
        if (state.deleteCategoryDialogData != null) {
            state.deleteCategoryDialogData = null
        } else if (state.categoryMoreDialogData != null) {
            state.categoryMoreDialogData = null
        } else {
            navigateUp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainLayout(
    viewModelState: SelectCategoryScreenViewModelState,
    state: SelectCategoryScreenStateHolder,
    onBackButtonClick: () -> Unit,
    onNewCategoryClick: () -> Unit,
    onCategoryClick: (CategoryId) -> Unit,
    onCategoryMoreClick: (Category) -> Unit,
) {
    val topBarScrollState = rememberTopAppBarState()
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topBarScrollState)

    UseAnimatedFadeTopBarColorForStatusBarColor(topBarScrollState)
    UseDefaultSystemNavBarColor()

    val allCategories by viewModelState.allCategoriesFlow.collectAsState()
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
                    Text(text = stringResource(id = R.string.select_category_title))
                },
                actions = {
                    if (allCategories.isNotEmpty()) {
                        IconButton(
                            onClick = onNewCategoryClick,
                            modifier = Modifier
                                .size(48.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_add_24),
                                contentDescription = stringResource(id = R.string.select_category_new_category_icon_content_description)
                            )
                        }
                    }
                },
                scrollBehavior = topBarScrollBehavior,
            )
        },
    ) {
        if (allCategories.isEmpty()) {
            EmptyLayout(
                onNewCategoryClick = onNewCategoryClick,
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                modifier = Modifier
                    .padding(it)
                    .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
            ) {
                items(
                    items = allCategories,
                    key = { category -> category.id }
                ) { category ->
                    CategoryItemLayout(
                        category = category,
                        isSelected = category.id == state.selectedCategoryId,
                        onClick = onCategoryClick,
                        onMoreClick = onCategoryMoreClick,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyLayout(
    onNewCategoryClick: () -> Unit,
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
            painter = painterResource(id = R.drawable.outline_category_24),
            contentDescription = stringResource(id = R.string.select_category_empty_icon_content_description),
            modifier = Modifier
                .size(200.dp, 200.dp)
                .padding(bottom = 32.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )

        Text(
            text = stringResource(id = R.string.select_category_empty_text),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .padding(bottom = 24.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        Button(
            onClick = onNewCategoryClick,
        ) {
            Text(
                text = stringResource(id = R.string.select_category_empty_new_category_button_text),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun CategoryItemLayout(
    category: Category,
    isSelected: Boolean,
    onClick: (CategoryId) -> Unit,
    onMoreClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier
            .clickable { onClick(category.id) }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(24.dp),
        ) {
            if (isSelected) {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_check_24),
                    contentDescription = stringResource(id = R.string.new_price_select_store_dialog_selected_store_content_description),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .size(24.dp),
                )
            }
        }

        Image(
            painter = painterResource(id = category.icon.iconRes),
            contentDescription = stringResource(id = R.string.select_category_category_icon_content_description),
            modifier = Modifier
                .padding(end = 6.dp)
                .size(30.dp)
        )

        Text(
            text = category.name,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            onClick = { onMoreClick(category) },
            modifier = Modifier
                .size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_more_vert_24),
                contentDescription = stringResource(id = R.string.select_category_edit_content_description)
            )
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    val viewModelState = object : SelectCategoryScreenViewModelState {
        override var allCategoriesFlow: StateFlow<List<Category>> = MutableStateFlow(
            listOf(
                Category(id = CategoryId(0), icon = CategoryIcon.Apple, name = "Fruits"),
                Category(id = CategoryId(1), icon = CategoryIcon.Broccoli, name = "Vegetables")
            )
        )
    }

    MainLayout(
        viewModelState = viewModelState,
        state = SelectCategoryScreenStateHolder(null, null, null),
        onBackButtonClick = {},
        onNewCategoryClick = {},
        onCategoryClick = {},
        onCategoryMoreClick = {},
    )
}
