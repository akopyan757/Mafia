package com.cheesecake.mafia.state

import com.cheesecake.mafia.data.DayType


sealed class LiveStage(val type: DayType) {

    data object Start: LiveStage(DayType.Night)

    sealed class Day : LiveStage(DayType.Day) {
        data class LastDeathSpeech(val playerNumber: Int): Day()
        data class LastVotedSpeech(val playerNumber: Int): Day()
        data class Speech(
            val playerNumber: Int,
            val candidateForElimination: Boolean = false,
        ): Day()
        data class Vote(
            val reVote: Boolean = false,
            val candidates: List<Int> = emptyList(),
        ): Day()
    }

    class Night : LiveStage(DayType.Night)

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

fun generateHistory(stage: DayType, count: Int): List<Pair<DayType, Int>> {
    return (0..count).map { index ->
        if (index == 0) {
            if (stage == DayType.Night && count == 0) {
                listOf()
            } else {
                listOf(DayType.Day to index)
            }
        } else {
            if (stage == DayType.Night && index == count) {
                listOf(DayType.Night to index)
            } else {
                listOf(DayType.Night to index, DayType.Day to index)
            }
        }
    }.flatten()
}