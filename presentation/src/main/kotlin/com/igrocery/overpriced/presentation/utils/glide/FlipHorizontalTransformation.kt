package com.igrocery.overpriced.presentation.utils.glide

import android.graphics.Bitmap
import android.graphics.Matrix
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.util.Util
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

private const val ID = "com.igrocery.overpriced.presentation.FlipHorizontalTransformation"
private val ID_BYTES = ID.toByteArray(StandardCharsets.UTF_8)

class FlipHorizontalTransformation : BitmapTransformation() {

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val matrix = Matrix()
        matrix.postScale(-1f, 1f)
        return Bitmap.createBitmap(toTransform, 0, 0, toTransform.width, toTransform.height, matrix, false)
    }

    override fun equals(other: Any?): Boolean {
        return other is FlipHorizontalTransformation
    }

    override fun hashCode(): Int {
        return Util.hashCode(ID.hashCode())
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }

}
