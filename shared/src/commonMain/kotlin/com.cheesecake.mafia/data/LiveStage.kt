package com.cheesecake.mafia.data


sealed class LiveStage(val type: DayType) {

    data object Start: LiveStage(DayType.Night)

    sealed class Day : LiveStage(DayType.Day) {
        data class LastDeathSpeech(override val playerNumber: Int): Day()
        data class LastVotedSpeech(override val playerNumber: Int): Day()
        data class Speech(
            override val playerNumber: Int,
            val candidateForElimination: Boolean = false,
        ): Day()
        data class Vote(
            val reVote: Boolean = false,
            val candidates: List<Int> = emptyList(),
        ): Day()

        override val isSpeech = this !is Vote
    }

    class Night : LiveStage(DayType.Night)

    open val isSpeech: Boolean = false
    open val playerNumber: Int = -1

    fun canAddCandidate(): Boolean {
        return this is Day.Speech && !this.candidateForElimination
    }
}

fun DayType.toText(): String {
    return when (this) {
        DayType.Day -> "День"
        DayType.Night -> "Ночь"
    }
}

fun generateHistory(stage: DayType, count: Byte): List<Pair<DayType, Byte>> {
    return (0 .. count).map { index ->
        if (index == 0) {
            if (stage == DayType.Night && count == 0.toByte()) {
                listOf()
            } else {
                listOf(DayType.Day to index.toByte())
            }
        } else {
            if (stage == DayType.Night && index.toByte() == count) {
                listOf(DayType.Night to index.toByte())
            } else {
                listOf(DayType.Night to index.toByte(), DayType.Day to index.toByte())
            }
        }
    }.flatten()
}