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

@Serializable(with = GameFinishResultSerializer::class)
enum class GameFinishResult(val value: String) {
    None(""),
    WhiteWin("White"),
    RedWin("Red"),
    BlackWin("Black");

    companion object {
        fun ofValue(value: String) = entries.toTypedArray().find { it.value == value } ?: None
    }
}

fun GameFinishResult.resultText(): String {
    return when (this) {
        GameFinishResult.BlackWin -> "Победа мафии"
        GameFinishResult.RedWin -> "Победа мирного города"
        GameFinishResult.WhiteWin -> "Победа маньяка"
        else -> ""
    }
}

@Suppress("EXTERNAL_SERIALIZER_USELESS")
@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
@Serializer(forClass = DayType::class)
private object GameFinishResultSerializer : KSerializer<GameFinishResult> {
    override val descriptor: SerialDescriptor = buildSerialDescriptor("finishResult", kind = SerialKind.ENUM)
    override fun serialize(encoder: Encoder, value: GameFinishResult) {
        encoder.encodeString(value.value)
    }
    override fun deserialize(decoder: Decoder): GameFinishResult {
        // Admittedly, this would accept "Error" in addition to "error".
        return GameFinishResult.ofValue(decoder.decodeString())
    }
}

