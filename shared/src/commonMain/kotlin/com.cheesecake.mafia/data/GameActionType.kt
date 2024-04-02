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

@Serializable(with = GameActionTypeSerializer::class)
sealed interface GameActionType {

    @Serializable
    data class Dead(val dayType: DayType): GameActionType {
        override fun dayType(): DayType = dayType
        override fun value(): String  = "Dead" + dayType.value
    }

    @Serializable
    enum class DayAction(
        private val value: String,
        private val iconRes: String
    ): GameActionType {
        Voted("Voted", "ic_like_button.xml"),
        Deleted("Deleted","ic_close.xml"),
        ThreeFouls("ThreeFouls", "ic_looks_3.xml");

        override fun iconRes(): String = iconRes
        override fun value(): String  = value
        override fun dayType(): DayType = DayType.Day
    }

    @Serializable
    enum class NightActon(val value: String, val role: GamePlayerRole): GameActionType {
        MafiaKilling("MafiaKilling", GamePlayerRole.Black.Mafia),
        DonChecking("DonChecking", GamePlayerRole.Black.Don),
        ManiacKilling("ManiacKilling", GamePlayerRole.White.Maniac),
        SheriffChecking("SheriffChecking", GamePlayerRole.Red.Sheriff),
        Doctor("Doctor", GamePlayerRole.Red.Doctor),
        ClientChoose("ClientChoose", GamePlayerRole.Red.Whore);

        override fun iconRes(): String = role.iconRes
        override fun value(): String  = value
        override fun dayType(): DayType = DayType.Night

        companion object {
            fun activeRoles(playerRoles: List<GamePlayerRole>): List<NightActon> {
                return entries.filter { action ->
                    playerRoles.contains(action.role) ||
                    (action == MafiaKilling && playerRoles.firstOrNull { it is GamePlayerRole.Black } != null)
                }
            }
        }
    }

    fun iconRes(): String = ""
    fun value(): String = ""
    fun dayType(): DayType

    companion object {
        private fun values(): List<GameActionType> {
            return listOf(Dead(DayType.Day), Dead(DayType.Night)) +
                    DayAction.entries.toTypedArray() +
                    NightActon.entries.toTypedArray()
        }

        fun ofValue(value: String): GameActionType? {
            return values().find { it.value() == value }
        }
    }
}

@Suppress("EXTERNAL_SERIALIZER_USELESS")
@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
@Serializer(forClass = GameActionType::class)
object GameActionTypeSerializer : KSerializer<GameActionType> {
    override val descriptor: SerialDescriptor = buildSerialDescriptor("role", kind = SerialKind.CONTEXTUAL)
    override fun deserialize(decoder: Decoder): GameActionType {
        return GameActionType.ofValue(decoder.decodeString()) ?: GameActionType.Dead(DayType.Day)
    }

    override fun serialize(encoder: Encoder, value: GameActionType) {
        encoder.encodeString(value.value())
    }
}