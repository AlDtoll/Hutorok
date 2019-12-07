package com.example.hutorok

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.domain.ILoadDataInteractor
import com.example.hutorok.domain.model.Quest
import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.domain.storage.IMessageInteractor
import com.example.hutorok.routing.IGetNowScreenInteractor
import com.example.hutorok.routing.OnBackPressedInteractor
import com.example.hutorok.routing.RouteToQuestScreenInteractor
import io.reactivex.BackpressureStrategy

class MainViewModel(
    private val getNowScreenInteractor: IGetNowScreenInteractor,
    private val onBackPressedInteractor: OnBackPressedInteractor,
    private val loadDataInteractor: ILoadDataInteractor,
    private val messageInteractor: IMessageInteractor,
    private val routeToQuestScreenInteractor: RouteToQuestScreenInteractor
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

}