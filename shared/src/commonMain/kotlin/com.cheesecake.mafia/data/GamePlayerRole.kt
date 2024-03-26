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

    @Serializable
    data object None: GamePlayerRole() {
        override val iconRes: String = ""
        override val name: String = ""
    }

    @Serializable
    sealed class Red(override val name: String, override val iconRes: String): GamePlayerRole() {

        @Serializable
        data object Сivilian : Red("Мирный", "ic_user.xml")

        @Serializable
        data object Sheriff : Red("Шериф", "ic_role_sheriff.xml")

        @Serializable
        data object Doctor : Red("Доктор", "ic_role_doctor.xml")

        @Serializable
        data object Whore : Red("Путана", "ic_role_whore.xml")
    }

    @Serializable
    sealed class Black(override val name: String, override val iconRes: String) : GamePlayerRole() {

        @Serializable
        data object Mafia: Black("Мафия", "ic_role_mafia.xml")

        @Serializable
        data object Don : Black("Дон", "ic_role_don.xml")
    }

    @Serializable
    sealed class White(override val name: String, override val iconRes: String) : GamePlayerRole() {

        @Serializable
        data object Maniac : White("Маньяк","ic_role_maniac.xml")
    }

    companion object {
        fun ofName(name: String) = roleValues().find { it.name == name } ?: None
    }
}

fun roleValues() = listOf(
    GamePlayerRole.Red.Сivilian,
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


