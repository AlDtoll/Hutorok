package com.example.hutorok.screen.tasks_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.domain.IEndTurnInteractor
import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.storage.IHutorStatusesListInteractor
import com.example.hutorok.domain.storage.ITaskInteractor
import com.example.hutorok.domain.storage.ITasksListInteractor
import com.example.hutorok.routing.RouteToTaskInfoInteractor
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

class TasksViewModel(
    private val tasksListInteractor: ITasksListInteractor,
    private val taskInteractor: ITaskInteractor,
    private val routeToTaskInfoInteractor: RouteToTaskInfoInteractor,
    private val hutorStatusesListInteractor: IHutorStatusesListInteractor,
    private val endTurnInteractor: IEndTurnInteractor
) : ITasksViewModel {

    private var search = PublishSubject.create<String>()

    override fun tasksData(): LiveData<List<Task>> {
        val observable = Observable.combineLatest(
            tasksListInteractor.get(),
            hutorStatusesListInteractor.get(),
            BiFunction { tasksList: List<Task>, statusesList: List<Status> ->
                tasksList.filter { task ->
                    Task.allConditionsIsComplete(
                        task.permissiveConditions,
                        statusesList
                    )
                }
            }
        )
        val filter = Observable.combineLatest(
            observable,
            search.startWith(""),
            BiFunction { list: List<Task>, search: String ->
                if (search.isBlank()) {
                    list
                } else {
                    list.filter {
                        it.name.contains(search, true) || it.description.contains(
                            search,
                            true
                        )
                    }
                }
            }
        )
        return LiveDataReactiveStreams.fromPublisher(
            filter.toFlowable(BackpressureStrategy.LATEST)
        )
    }


    override fun clickTask(task: Task) {
        taskInteractor.update(task)
        routeToTaskInfoInteractor.execute()
    }

    override fun clickEndTurnButton() {
        endTurnInteractor.execute()
        tasksListInteractor.refresh()
    }

    override fun searchChange(searchData: String) {
        search.onNext(searchData)
    }

    override fun clickClearButton() {
        search.onNext("")
    }

    override fun searchData(): LiveData<String> =
        LiveDataReactiveStreams.fromPublisher(search.toFlowable(BackpressureStrategy.LATEST))

}