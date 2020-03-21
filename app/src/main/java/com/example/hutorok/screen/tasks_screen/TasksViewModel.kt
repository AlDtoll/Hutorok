package com.example.hutorok.screen.tasks_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.domain.IEndTurnInteractor
import com.example.hutorok.domain.ILoadDataInteractor
import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.storage.IBuildsListInteractor
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
    private val buildsListInteractor: IBuildsListInteractor,
    private val endTurnInteractor: IEndTurnInteractor,
    private val loadDataInteractor: ILoadDataInteractor
) : ITasksViewModel {

    private val search = PublishSubject.create<String>()

    override fun tasksData(): LiveData<List<Task>> {
        val observable = Observable.combineLatest(
            tasksListInteractor.get(),
            buildsListInteractor.get(),
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

    override fun endTurnDataResponse(): LiveData<Unit> =
        LiveDataReactiveStreams.fromPublisher(
            endTurnInteractor.get().toFlowable(
                BackpressureStrategy.LATEST
            )
        )

    override fun searchChange(searchData: String) {
        search.onNext(searchData)
    }

    override fun clickClearButton() {
        search.onNext("")
    }

    override fun searchData(): LiveData<String> =
        LiveDataReactiveStreams.fromPublisher(search.toFlowable(BackpressureStrategy.LATEST))

    override fun loadAdventure() {
        loadDataInteractor.execute()
    }

}