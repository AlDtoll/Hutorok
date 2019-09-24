package com.example.hutorok.screen.tasks_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.storage.IHutorStatusesListInteractor
import com.example.hutorok.domain.storage.ITaskInteractor
import com.example.hutorok.domain.storage.ITasksListInteractor
import com.example.hutorok.routing.RouteToTaskInfoInteractor
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class TasksViewModel(
    private val tasksListInteractor: ITasksListInteractor,
    private val taskInteractor: ITaskInteractor,
    private val routeToTaskInfoInteractor: RouteToTaskInfoInteractor,
    private val hutorStatusesListInteractor: IHutorStatusesListInteractor
) : ITasksViewModel {

    override fun tasksData(): LiveData<List<Task>> {
        val observable = Observable.combineLatest(
            tasksListInteractor.get(),
            hutorStatusesListInteractor.get(),
            BiFunction { tasksList: List<Task>, statusesList: List<Status> ->
                val visibleTasksList = emptyList<Task>().toMutableList()
                tasksList.forEach { task ->
                    if (isTaskVisible(task, statusesList)) {
                        visibleTasksList.add(task)
                    }
                }
                return@BiFunction visibleTasksList.toList()
            }
        )
        return LiveDataReactiveStreams.fromPublisher(
            observable.toFlowable(BackpressureStrategy.LATEST)
        )
    }


    override fun clickTask(task: Task) {
        taskInteractor.update(task)
        routeToTaskInfoInteractor.execute()
    }

    private fun isTaskVisible(task: Task, statusesList: List<Status>): Boolean {
        if (task.permissiveCondition.isEmpty()) {
            return true
        }
        task.permissiveCondition.forEach { condition ->
            val find = statusesList.find { condition.first == it.code }
            if (find == null) {
                return false
            }
            if (find.value < condition.second) {
                return false
            }
        }
        return true
    }

}