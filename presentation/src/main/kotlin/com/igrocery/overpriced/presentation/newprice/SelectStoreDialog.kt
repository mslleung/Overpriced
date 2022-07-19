package com.igrocery.overpriced.presentation.newprice

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import com.ireceipt.receiptscanner.presentation.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SelectStoreDialog(
    storesPagingItems: LazyPagingItems<Store>,
    state: SelectStoreDialogStateHolder,
    onDismiss: () -> Unit,
    onStoreSelect: (Store) -> Unit,
    onEditStoreClick: (Store) -> Unit,
    onNewStoreClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { },
        modifier = Modifier.padding(horizontal = 16.dp),
        title = {
            Text(
                text = stringResource(id = R.string.new_price_select_store_dialog_title_text)
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
                    items = storesPagingItems,
                    key = { store -> store.id }
                ) { store ->
                    if (store != null) {
                        StoreLocationOptionLayout(
                            store = store,
                            isSelected = state.selectedStoreId == store.id,
                            onStoreSelect = onStoreSelect,
                            onEditStoreClick = onEditStoreClick,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }

                item {
                    NewStoreLocationItemLayout(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clickable { onNewStoreClick() }
                    )
                }
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

@Composable
private fun StoreLocationOptionLayout(
    store: Store,
    isSelected: Boolean,
    onStoreSelect: (Store) -> Unit,
    onEditStoreClick: (Store) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier
            .clickable { onStoreSelect(store) }
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
                .clickable { onStoreSelect(store) },
        )

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = store.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = store.address.toString(),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .alpha(0.8f)
            )
        }

        IconButton(
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            onClick = { onEditStoreClick(store) },
            modifier = Modifier
                .size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_edit_24),
                contentDescription = stringResource(id = R.string.new_price_select_store_dialog_edit_content_description)
            )
        }
    }
}

@Composable
private fun NewStoreLocationItemLayout(
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_add_24),
            contentDescription = stringResource(id = R.string.new_price_select_store_dialog_new_store_icon_content_description),
            modifier = Modifier
                .padding(end = 12.dp)
                .size(24.dp)
        )

        Text(
            text = stringResource(id = R.string.new_price_select_store_dialog_new_store_text),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}
