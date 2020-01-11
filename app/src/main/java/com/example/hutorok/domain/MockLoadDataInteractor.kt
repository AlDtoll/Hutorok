package com.example.hutorok.domain

import com.example.hutorok.domain.model.Quest
import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.domain.storage.*

class MockLoadDataInteractor(
    private val workersListInteractor: IWorkersListInteractor,
    private val tasksListInteractor: ITasksListInteractor,
    private val hutorStatusesListInteractor: IHutorStatusesListInteractor,
    private val importantStatusNamesListInteractor: IImportantStatusNamesListInteractor,
    private val endTasksListInteractor: IEndTasksListInteractor,
    private val turnNumberInteractor: ITurnNumberInteractor,
    private val invisibleStatusNamesListInteractor: IInvisibleStatusNamesListInteractor,
    private val historyInteractor: IHistoryInteractor,
    private val questInteractor: IQuestInteractor,
    private val generalDisableStatusListInteractor: IGeneralDisableStatusListInteractor
) : ILoadDataInteractor {

    override fun update(
        workers: MutableList<Worker>,
        tasks: MutableList<Task>,
        hutorokStatuses: MutableList<Status>,
        endTasks: MutableList<Task>,
        events: MutableList<String>,
        turnNumber: Int,
        startQuest: Quest
    ) {
        updateWorkers(workers)

        updateTasks(tasks)

        updateHutorStatuses(hutorokStatuses)

        updateImportantNames()

        updateEndTasks(endTasks)

        updateHistory(events, turnNumber)

        updateInvisibleStatuses()

        updateQuest(startQuest)

        updateGeneralDisableStatuses()
    }

    private fun updateWorkers(workers: MutableList<Worker>) {
        workersListInteractor.update(workers)
    }

    private fun updateTasks(tasks: MutableList<Task>) {
        tasksListInteractor.update(tasks)
    }

    private fun updateHutorStatuses(hutorokStatuses: MutableList<Status>) {
        hutorStatusesListInteractor.update(hutorokStatuses)
    }

    private fun updateImportantNames() {
        val names = listOf(
            "Работал"
        )
        importantStatusNamesListInteractor.update(names)
    }

    private fun updateEndTasks(endTasks: MutableList<Task>) {
        endTasksListInteractor.update(endTasks)
    }

    private fun updateHistory(events: MutableList<String>, turnNumber: Int) {
        historyInteractor.update(events)
        turnNumberInteractor.update(turnNumber)
    }

    private fun updateInvisibleStatuses() {
        val codes = mutableListOf(
            "?INVISIBLE"
        )
        invisibleStatusNamesListInteractor.update(codes)
    }

    private fun updateGeneralDisableStatuses() {
        val conditions = mutableListOf(
            Triple(
                "worked",
                Task.Symbol.MORE,
                0.0
            ),
            Triple(
                "CHILDREN",
                Task.Symbol.MORE,
                0.0
            ),
            Triple(
                "MortaNAME",
                Task.Symbol.MORE,
                0.0
            )
        )
        generalDisableStatusListInteractor.update(conditions)
    }

    private fun updateQuest(quest: Quest) {
        questInteractor.update(quest)
    }

}