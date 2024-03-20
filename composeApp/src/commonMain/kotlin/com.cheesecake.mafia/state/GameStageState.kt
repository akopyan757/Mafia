package com.cheesecake.mafia.state

sealed class LiveStage(val type: StageDayType) {

    data object Start: LiveStage(StageDayType.Night)

    sealed class Day : LiveStage(StageDayType.Day) {
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

    class Night : LiveStage(StageDayType.Night)

    fun canAddCandidate(): Boolean {
        return this is Day.Speech && !this.candidateForElimination
    }
}

enum class StageDayType(val value: String) {
    Day("День"),
    Night("Ночь")
}

fun generateHistory(stage: StageDayType, count: Int): List<Pair<StageDayType, Int>> {
    return (0..count).map { index ->
        if (index == 0) {
            if (stage == StageDayType.Night && count == 0) {
                listOf()
            } else {
                listOf(StageDayType.Day to index)
            }
        } else {
            if (stage == StageDayType.Night && index == count) {
                listOf(StageDayType.Night to index)
            } else {
                listOf(StageDayType.Night to index, StageDayType.Day to index)
            }
        }
    }.flatten()
}