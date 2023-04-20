package com.igrocery.overpriced.domain.productpricehistory.models

import android.os.Parcelable
import androidx.core.text.trimmedLength
import com.igrocery.overpriced.domain.AggregateRoot
import com.igrocery.overpriced.domain.CategoryId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    override val id: CategoryId = CategoryId(0),
    override val creationTimestamp: Long = 0,
    override val updateTimestamp: Long = 0,
    val icon: CategoryIcon,
    val name: String,
) : AggregateRoot(id, creationTimestamp, updateTimestamp), Parcelable {

    init {
        require(name.trimmedLength() == name.length)
        require(name.isNotBlank())
        require(name.length <= 100)
    }

}
