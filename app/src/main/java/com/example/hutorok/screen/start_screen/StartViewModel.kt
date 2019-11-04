package com.example.hutorok.screen.start_screen

import com.example.hutorok.domain.IEndTurnInteractor
import com.example.hutorok.routing.RouteToBuildsScreenInteractor
import com.example.hutorok.routing.RouteToHistoryScreenInteractor
import com.example.hutorok.routing.RouteToTasksScreenInteractor
import com.example.hutorok.routing.RouteToWorkersScreenInteractor

class StartViewModel(
    private val routeToWorkersScreenInteractor: RouteToWorkersScreenInteractor,
    private val routeToBuildsScreenInteractor: RouteToBuildsScreenInteractor,
    private val routeToTasksScreenInteractor: RouteToTasksScreenInteractor,
    private val routeToHistoryScreenInteractor: RouteToHistoryScreenInteractor,
    private val endTurnInteractor: IEndTurnInteractor
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

}