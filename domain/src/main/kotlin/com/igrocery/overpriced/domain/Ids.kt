package com.igrocery.overpriced.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// type-safe ids

// tagging interface
interface Id

@Parcelize
@JvmInline
value class CategoryId(val value: Long) : Id, Parcelable

@Parcelize
@JvmInline
value class GroceryListId(val value: Long) : Id, Parcelable

@Parcelize
@JvmInline
value class GroceryListItemId(val value: Long) : Id, Parcelable

@Parcelize
@JvmInline
value class PriceRecordId(val value: Long) : Id, Parcelable

@Parcelize
@JvmInline
value class ProductId(val value: Long) : Id, Parcelable

@Parcelize
@JvmInline
value class StoreId(val value: Long) : Id, Parcelable
