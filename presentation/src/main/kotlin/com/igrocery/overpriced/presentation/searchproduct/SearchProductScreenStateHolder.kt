package com.igrocery.overpriced.presentation.searchproduct

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.igrocery.overpriced.domain.productpricehistory.models.Product

class SearchProductScreenStateHolder(
    savedState: List<*>? = null,
    val productPagingItems: LazyPagingItems<Product>
) {

    var isRequestingFirstFocus by mutableStateOf(savedState?.get(0) as? Boolean ?: true)
    var query by mutableStateOf(savedState?.get(1) as? String ?: "")

}

@Composable
fun rememberSearchProductScreenState(viewModelState: SearchProductScreenViewModelState): MutableState<SearchProductScreenStateHolder> {
    val productPagingItems = viewModelState.productsPagingDataFlow.collectAsLazyPagingItems()
    return rememberSaveable(
        stateSaver = listSaver(
            save = {
                listOf(
                    it.isRequestingFirstFocus,
                    it.query,
                )
            },
            restore = { savedState ->
                SearchProductScreenStateHolder(savedState, productPagingItems)
            }
        )
    ) {
        mutableStateOf(SearchProductScreenStateHolder(null, productPagingItems))
    }
}
