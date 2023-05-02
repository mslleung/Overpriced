package com.igrocery.overpriced.domain.grocerylist.models

import android.os.Parcelable
import com.igrocery.overpriced.domain.AggregateRoot
import com.igrocery.overpriced.domain.GroceryListId
import com.igrocery.overpriced.domain.GroceryListItemId
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroceryListItem(
    override val id: GroceryListItemId = GroceryListItemId(0),
    override val creationTimestamp: Long = 0,
    override val updateTimestamp: Long = 0,
    val groceryListId: GroceryListId,
    val name: String,
    val quantity: String,    // quantity is very arbitrary, so we store strings for maximum flexibility
    val isChecked: Boolean = false
) : AggregateRoot(id, creationTimestamp, updateTimestamp), Parcelable {

    init {
        require(groceryListId.value > 0)
        require(name.isNotBlank() && name.length < 100)
        require(quantity.length < 100)
    }

}
