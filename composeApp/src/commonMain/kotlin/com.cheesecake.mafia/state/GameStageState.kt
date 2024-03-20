package com.cheesecake.mafia.state

data class GameStageState(
    val count: Int,
    val stageAction: StageAction,
) {

    override fun toString(): String {
        return stageAction.type.value + " " + count.toString()
    }
}

sealed class StageAction(val type: StageDayType) {

    data object Start: StageAction(StageDayType.Night)

    sealed class Day : StageAction(StageDayType.Day) {
        data class LastDeathSpeech(val playerNumber: Int): Day()
        data class LastVotedSpeech(val playerNumber: Int): Day()
        data class Speech(
            val playerNumber: Int,
            val candidateForElimination: Boolean = false,
        ): Day()
        data class Vote(
            val candidates: List<Int> = emptyList(),
            val totalVotes: Int = 0,
            val reVote: Boolean = false,
        ): Day()
    }

    class Night : StageAction(StageDayType.Night)

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