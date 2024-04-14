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
    enum class Dead(val value: String, private val _dayType: DayType): GameActionType {
        Night("DeadNight", DayType.Night),
        Day("DeadDay", DayType.Day);

        override fun value() = value
        override fun dayType(): DayType = _dayType
        companion object {
            fun ofDayType(dayType: DayType): Dead = entries.first { it._dayType == dayType }
        }
    }

    @Serializable
    enum class DayAction(
        val value: String,
        private val iconRes: String
    ): GameActionType {
        Voted("Voted", "ic_like_button.xml"),
        Deleted("Deleted", "ic_close.xml"),
        ThreeFouls("ThreeFouls", "ic_looks_3.xml");

        override fun value() = value
        override fun iconRes(): String = iconRes
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

        override fun value() = value
        override fun iconRes(): String = role.iconRes
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
        fun values(): List<GameActionType> {
            return Dead.entries +
                    DayAction.entries.toTypedArray() +
                    NightActon.entries.toTypedArray()
        }
    }
}

@Suppress("EXTERNAL_SERIALIZER_USELESS")
@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
@Serializer(forClass = GameActionType::class)
object GameActionTypeSerializer : KSerializer<GameActionType> {
    override val descriptor: SerialDescriptor = buildSerialDescriptor("action", kind = SerialKind.CONTEXTUAL)
    override fun deserialize(decoder: Decoder): GameActionType {
        val value = decoder.decodeString()
        return GameActionType.values().first { type -> type.value() == value }
    }

    override fun serialize(encoder: Encoder, value: GameActionType) {
        encoder.encodeString(value.value())
    }
}