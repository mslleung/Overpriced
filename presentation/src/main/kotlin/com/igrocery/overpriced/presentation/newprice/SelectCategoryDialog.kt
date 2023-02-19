package com.igrocery.overpriced.presentation.newprice

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon
import com.igrocery.overpriced.presentation.R

@Composable
fun SelectCategoryDialog(
    viewModel: SelectCategoryDialogViewModel,
    selectedCategoryId: CategoryId?,
    onDismiss: () -> Unit,
    onCategorySelect: (Category) -> Unit,
    onEditCategoryClick: (Category) -> Unit,
    onNewCategoryClick: () -> Unit,
) {
    val allCategories by viewModel.allCategoriesFlow.collectAsState()
    MainLayout(
        allCategories,
        selectedCategoryId,
        onDismiss,
        onCategorySelect,
        onEditCategoryClick,
        onNewCategoryClick
    )
}

@Composable
private fun MainLayout(
    categoryList: List<Category>,
    selectedCategoryId: CategoryId?,
    onDismiss: () -> Unit,
    onCategorySelect: (Category) -> Unit,
    onEditCategoryClick: (Category) -> Unit,
    onNewCategoryClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { },
        modifier = Modifier.padding(horizontal = 16.dp),
        title = {
            Text(
                text = stringResource(id = R.string.select_category_dialog_title)
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                modifier = Modifier
                    .navigationBarsPadding()
            ) {
                items(
                    items = categoryList,
                    key = { category -> category.id }
                ) { category ->
                    CategoryOptionLayout(
                        category = category,
                        isSelected = category.id == selectedCategoryId,
                        onClick = onCategorySelect,
                        onEditClick = onEditCategoryClick,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

                item {
                    NewCategoryItemLayout(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clickable { onNewCategoryClick() }
                    )
                }
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

@Composable
private fun CategoryOptionLayout(
    category: Category,
    isSelected: Boolean,
    onClick: (Category) -> Unit,
    onEditClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier
            .clickable { onClick(category) }
    ) {
        Image(
            painter = if (isSelected) {
                painterResource(id = R.drawable.ic_baseline_check_circle_24)
            } else {
                painterResource(id = R.drawable.ic_baseline_radio_button_unchecked_24)
            },
            contentDescription = stringResource(id = R.string.new_price_select_store_dialog_selected_store_content_description),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .padding(end = 12.dp)
                .size(24.dp)
                .clickable { onClick(category) },
        )

        Image(
            painter = painterResource(id = category.icon.iconRes),
            contentDescription = stringResource(id = R.string.select_category_dialog_category_icon_content_description),
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
            onClick = { onEditClick(category) },
            modifier = Modifier
                .size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_edit_24),
                contentDescription = stringResource(id = R.string.select_category_dialog_edit_content_description)
            )
        }
    }
}

@Composable
private fun NewCategoryItemLayout(
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_add_24),
            contentDescription = stringResource(id = R.string.select_category_dialog_new_category_icon_content_description),
            modifier = Modifier
                .padding(end = 12.dp)
                .size(24.dp)
        )

        Text(
            text = stringResource(id = R.string.select_category_dialog_new_category),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    val categoryList = listOf(
        Category(id = CategoryId(0), icon = CategoryIcon.Apple, name = "Fruits"),
        Category(id = CategoryId(1), icon = CategoryIcon.Broccoli, name = "Vegetables")
    )

    MainLayout(
        categoryList = categoryList,
        selectedCategoryId = CategoryId(0),
        onDismiss = {},
        onCategorySelect = {},
        onEditCategoryClick = {},
        onNewCategoryClick = {},
    )
}
