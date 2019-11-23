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
        tasks: MutableList<Task>
    ) {
        updateWorkers(workers)

        updateTasks(tasks)

        updateHutorStatuses()

        updateImportantNames()

        updateEndTasks()

        startHistory()

        updateInvisibleStatuses()
    }

    private fun updateWorkers(workers: MutableList<Worker>) {
        workersListInteractor.update(workers)
    }

    private fun updateTasks(tasks: MutableList<Task>) {
        tasksListInteractor.update(tasks)
    }

    private fun updateHutorStatuses() {
        val statuses = mutableListOf(
            Status(
                "houseBUILDING",
                "Жилой дом",
                "Тут живут люди",
                1.0,
                true
            ),
            Status(
                "houseBUILDING",
                "Жилой дом",
                "Тут живут люди",
                1.0,
                true
            ),
            Status(
                "forest",
                "Лес",
                "Хутор окружен деревьями",
                1.0,
                true
            ),
            Status(
                "foodsRES",
                "Еда",
                "Нужна для пропитания хутора",
                10.0,
                true
            )
        )
        hutorStatusesListInteractor.update(statuses)
    }

    private fun updateImportantNames() {
        val names = listOf(
            "Работал"
        )
        importantStatusNamesListInteractor.update(names)
    }

    private fun updateEndTasks() {
        val tasks = emptyList<Task>()
        endTasksListInteractor.update(tasks)
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