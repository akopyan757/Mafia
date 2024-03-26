package com.cheesecake.mafia.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = DayTypeSerializer::class)
enum class DayType( val order: Byte, val value: String) {
    Day(1, "Day"),
    Night(0, "Night");

    companion object {
        fun ofValue(value: String) = entries.find { it.value == value } ?: Day
    }
}

@Suppress("EXTERNAL_SERIALIZER_USELESS")
@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
@Serializer(forClass = DayType::class)
private object DayTypeSerializer : KSerializer<DayType> {
    override val descriptor: SerialDescriptor = buildSerialDescriptor("dayType", kind = SerialKind.ENUM)
    override fun serialize(encoder: Encoder, value: DayType) {
        encoder.encodeString(value.value)
    }
    override fun deserialize(decoder: Decoder): DayType {
        // Admittedly, this would accept "Error" in addition to "error".
        return DayType.ofValue(decoder.decodeString())
    }
}

