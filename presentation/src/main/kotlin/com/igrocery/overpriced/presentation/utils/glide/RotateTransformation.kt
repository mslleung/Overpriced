package com.igrocery.overpriced.presentation.utils.glide

import android.graphics.Bitmap
import android.graphics.Matrix
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.util.Util
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

private const val ID = "com.igrocery.overpriced.presentation.RotateTransformation"
private val ID_BYTES = ID.toByteArray(StandardCharsets.UTF_8)

class RotateTransformation(private val rotationDegrees: Int) : BitmapTransformation() {

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        return if (rotationDegrees == 0) {
            toTransform
        } else {
            val matrix = Matrix()
            matrix.postRotate(rotationDegrees.toFloat())
            Bitmap.createBitmap(toTransform, 0, 0, toTransform.width, toTransform.height, matrix, false)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is RotateTransformation) {
            return rotationDegrees == other.rotationDegrees
        }
        return false
    }

    override fun hashCode(): Int {
        return Util.hashCode(ID.hashCode(), Util.hashCode(rotationDegrees))
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)

        val rotationData = ByteBuffer.allocate(4).putInt(rotationDegrees).array()
        messageDigest.update(rotationData)
    }

}
