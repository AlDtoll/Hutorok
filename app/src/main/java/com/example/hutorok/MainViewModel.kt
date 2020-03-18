package com.example.hutorok

import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.domain.ILoadDataInteractor
import com.example.hutorok.domain.model.Quest
import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.domain.storage.IMessageInteractor
import com.example.hutorok.domain.storage.INavigationBarVisibilityInteractor
import com.example.hutorok.domain.storage.ITurnNumberInteractor
import com.example.hutorok.routing.*
import io.reactivex.BackpressureStrategy

class MainViewModel(
    private val getNowScreenInteractor: IGetNowScreenInteractor,
    private val onBackPressedInteractor: OnBackPressedInteractor,
    private val loadDataInteractor: ILoadDataInteractor,
    private val messageInteractor: IMessageInteractor,
    private val routeToQuestScreenInteractor: RouteToQuestScreenInteractor,
    private val routeToWorkersScreenInteractor: RouteToWorkersScreenInteractor,
    private val routeToBuildsScreenInteractor: RouteToBuildsScreenInteractor,
    private val routeToTasksScreenInteractor: RouteToTasksScreenInteractor,
    private val routeToHistoryScreenInteractor: RouteToHistoryScreenInteractor,
    private val turnNumberInteractor: ITurnNumberInteractor,
    private val navigationBarVisibilityInteractor: INavigationBarVisibilityInteractor
) : IMainViewModel {

    override fun nowScreen(): LiveData<NowScreen> {
        return LiveDataReactiveStreams.fromPublisher(
            getNowScreenInteractor.execute().toFlowable(BackpressureStrategy.LATEST)
        )
    }

    override fun onBackPressed() {
        onBackPressedInteractor.execute()
    }

    override fun loadData(
        workers: MutableList<Worker>,
        tasks: MutableList<Task>,
        hutorokStatuses: MutableList<Status>,
        endTasks: MutableList<Task>,
        events: MutableList<String>,
        turnNumber: Int,
        startQuest: Quest
    ) {
        loadDataInteractor.update(
            workers,
            tasks,
            hutorokStatuses,
            endTasks,
            events,
            turnNumber,
            startQuest
        )
    }

    override fun messageData(): LiveData<String> {
        return LiveDataReactiveStreams.fromPublisher(
            messageInteractor.get().toFlowable(BackpressureStrategy.LATEST)
        )
    }

    override fun startQuest() {
        routeToQuestScreenInteractor.execute()
    }

    override fun clickAction(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.action_workers -> routeToWorkersScreenInteractor.execute()
            R.id.action_builds -> routeToBuildsScreenInteractor.execute()
            R.id.action_tasks -> routeToTasksScreenInteractor.execute()
            R.id.action_history -> routeToHistoryScreenInteractor.execute()
        }
    }

    override fun turnNumberData(): LiveData<Int> =
        LiveDataReactiveStreams.fromPublisher(
            turnNumberInteractor.get()
                .toFlowable(BackpressureStrategy.LATEST)
        )

    override fun getNavigationBarVisibility(): LiveData<Boolean> {
        return LiveDataReactiveStreams.fromPublisher(
            navigationBarVisibilityInteractor.get().toFlowable(BackpressureStrategy.LATEST)
        )
    }

    override fun onClose() {
        routeToTasksScreenInteractor.execute()
    }

}