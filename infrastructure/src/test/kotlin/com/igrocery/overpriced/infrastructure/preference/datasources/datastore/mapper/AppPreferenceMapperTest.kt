package com.igrocery.overpriced.infrastructure.preference.datasources.datastore.mapper

import com.igrocery.overpriced.domain.preference.models.AppPreference
import com.igrocery.overpriced.infrastructure.preference.datasources.datastore.AppPreferenceProto
import io.kotest.matchers.shouldBe

import org.junit.Test
import java.util.*

class AppPreferenceMapperTest {

    @Test
    fun mapToData() {
        val appPreferenceMapper = AppPreferenceMapper()
        val appPreference = AppPreference(
            preferredCurrency = Currency.getInstance("HKD")
        )

        val appPreferenceProto = appPreferenceMapper.mapToData(appPreference)

        val expectedAppPreferenceProto = AppPreferenceProto.newBuilder().apply {
            preferredCurrency = "HKD"
        }.build()
        appPreferenceProto shouldBe expectedAppPreferenceProto
    }

    @Test
    fun mapFromData() {
        val appPreferenceMapper = AppPreferenceMapper()
        val appPreferenceProto = AppPreferenceProto.newBuilder().apply {
            preferredCurrency = "HKD"
        }.build()

        val appPreference = appPreferenceMapper.mapFromData(appPreferenceProto)

        val expectedAppPreference = AppPreference(
            preferredCurrency = Currency.getInstance("HKD")
        )
        appPreference shouldBe expectedAppPreference
    }

}
