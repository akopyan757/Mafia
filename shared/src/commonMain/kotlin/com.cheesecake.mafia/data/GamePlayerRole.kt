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

@Serializable(GamePlayerRoleSerializer::class)
sealed class GamePlayerRole {
    abstract val iconRes: String
    abstract val name: String

    data object None: GamePlayerRole() {
        override val iconRes: String = ""
        override val name: String = ""
    }

    sealed class Red(override val name: String, override val iconRes: String): GamePlayerRole() {

        data object Civilian : Red("Сivilian", "ic_user.xml")

        data object Sheriff : Red("Sheriff", "ic_role_sheriff.xml")

        data object Doctor : Red("Doctor", "ic_role_doctor.xml")

        data object Whore : Red("Whore", "ic_role_whore.xml")
    }

    sealed class Black(override val name: String, override val iconRes: String) : GamePlayerRole() {

        data object Mafia: Black("Mafia", "ic_role_mafia.xml")

        data object Don : Black("Don", "ic_role_don.xml")
    }

    sealed class White(override val name: String, override val iconRes: String) : GamePlayerRole() {

        data object Maniac : White("Maniac","ic_role_maniac.xml")
    }

    companion object {
        fun ofName(name: String) = roleValues().find { it.name == name } ?: None
    }
}

fun GamePlayerRole.russianText() = when(this) {
    is GamePlayerRole.Red.Civilian -> "Мирный"
    is GamePlayerRole.Red.Sheriff -> "Шериф"
    is GamePlayerRole.Red.Doctor -> "Доктор"
    is GamePlayerRole.Red.Whore -> "Путана"
    is GamePlayerRole.Black.Mafia -> "Мафия"
    is GamePlayerRole.Black.Don -> "Дон"
    is GamePlayerRole.White.Maniac -> "Маньяк"
    else -> ""
}

fun roleValues() = listOf(
    GamePlayerRole.Red.Civilian,
    GamePlayerRole.Red.Sheriff,
    GamePlayerRole.Red.Doctor,
    GamePlayerRole.Red.Whore,
    GamePlayerRole.Black.Mafia,
    GamePlayerRole.Black.Don,
    GamePlayerRole.White.Maniac,
)

@Suppress("EXTERNAL_SERIALIZER_USELESS")
@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
@Serializer(forClass = GamePlayerRole::class)
object GamePlayerRoleSerializer : KSerializer<GamePlayerRole> {
    override val descriptor: SerialDescriptor = buildSerialDescriptor("role", kind = SerialKind.CONTEXTUAL)
    override fun serialize(encoder: Encoder, value: GamePlayerRole) {
        encoder.encodeString(value.name)
    }
    override fun deserialize(decoder: Decoder): GamePlayerRole {
        return GamePlayerRole.ofName(decoder.decodeString())
    }
}


