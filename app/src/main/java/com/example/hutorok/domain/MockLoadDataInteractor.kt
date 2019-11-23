package com.example.hutorok.domain

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
    private val invisibleStatusNamesListInteractor: IInvisibleStatusNamesListInteractor
) : ILoadDataInteractor {

    override fun update(
        workers: MutableList<Worker>,
        tasks: MutableList<Task>,
        hutorokStatuses: MutableList<Status>,
        endTasks: MutableList<Task>
    ) {
        updateWorkers(workers)

        updateTasks(tasks)

        updateHutorStatuses(hutorokStatuses)

        updateImportantNames()

        updateEndTasks(endTasks)

        startHistory()

        updateInvisibleStatuses()
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

    private fun startHistory() {
        turnNumberInteractor.increment()
    }

    private fun updateInvisibleStatuses() {
        val codes = listOf(
            "?INVISIBLE"
        )
        invisibleStatusNamesListInteractor.update(codes)
    }

}