package com.example.hutorok.screen.tasks_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.storage.ITaskInteractor
import com.example.hutorok.domain.storage.ITasksListInteractor
import com.example.hutorok.routing.RouteToTaskInfoInteractor
import io.reactivex.BackpressureStrategy

class TasksViewModel(
    private val tasksListInteractor: ITasksListInteractor,
    private val taskInteractor: ITaskInteractor,
    private val routeToTaskInfoInteractor: RouteToTaskInfoInteractor
) : ITasksViewModel {

    override fun tasksData(): LiveData<List<Task>> =
        LiveDataReactiveStreams.fromPublisher(
            tasksListInteractor.get().toFlowable(BackpressureStrategy.LATEST)
        )


    override fun clickTask(task: Task) {
        taskInteractor.update(task)
        routeToTaskInfoInteractor.execute()
    }

}