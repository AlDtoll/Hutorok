package com.example.hutorok.routing

class RouteToWorkersScreenInteractor(
    private val router: IRouter,
    private val scenarioInteractor: IScenarioInteractor
) {

    fun execute() {
        scenarioInteractor.update(Scenario.SPEAK)
        router.routeToWorkersScreen()
    }
}