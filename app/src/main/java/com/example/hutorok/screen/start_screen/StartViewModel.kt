package com.example.hutorok.screen.start_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.domain.IEndTurnInteractor
import com.example.hutorok.domain.storage.ITurnNumberInteractor
import com.example.hutorok.routing.RouteToBuildsScreenInteractor
import com.example.hutorok.routing.RouteToHistoryScreenInteractor
import com.example.hutorok.routing.RouteToTasksScreenInteractor
import com.example.hutorok.routing.RouteToWorkersScreenInteractor
import io.reactivex.BackpressureStrategy

class StartViewModel(
    private val routeToWorkersScreenInteractor: RouteToWorkersScreenInteractor,
    private val routeToBuildsScreenInteractor: RouteToBuildsScreenInteractor,
    private val routeToTasksScreenInteractor: RouteToTasksScreenInteractor,
    private val routeToHistoryScreenInteractor: RouteToHistoryScreenInteractor,
    private val endTurnInteractor: IEndTurnInteractor,
    private val turnNumberInteractor: ITurnNumberInteractor
) : IStartViewModel {

    override fun clickWorkersButton() {
        routeToWorkersScreenInteractor.execute()
    }

    override fun clickBuildsButton() {
        routeToBuildsScreenInteractor.execute()
    }

    override fun clickTasksButton() {
        routeToTasksScreenInteractor.execute()
    }

    override fun clickHistoryButton() {
        routeToHistoryScreenInteractor.execute()
    }

    override fun clickEndTurnButton() {
        endTurnInteractor.execute()
    }

    override fun turnNumberData(): LiveData<Int> =
        LiveDataReactiveStreams.fromPublisher(
            turnNumberInteractor.get()
                .toFlowable(BackpressureStrategy.LATEST)
        )


}