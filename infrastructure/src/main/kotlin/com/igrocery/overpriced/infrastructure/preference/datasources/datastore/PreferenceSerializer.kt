package com.igrocery.overpriced.infrastructure.preference.datasources.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object AppPreferenceProtoSerializer : Serializer<AppPreferenceProto> {
    override val defaultValue: AppPreferenceProto = AppPreferenceProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AppPreferenceProto {
        try {
            return AppPreferenceProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: AppPreferenceProto,
        output: OutputStream
    ) = t.writeTo(output)

}

val Context.preferenceDataStore: DataStore<AppPreferenceProto> by dataStore(
    fileName = "preferences.pb",
    serializer = AppPreferenceProtoSerializer
)
