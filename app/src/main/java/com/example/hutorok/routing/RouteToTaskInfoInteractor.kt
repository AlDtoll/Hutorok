package com.example.hutorok.routing

class RouteToTaskInfoInteractor(
    private val router: IRouter,
    private val scenarioInteractor: IScenarioInteractor
) {

    fun execute() {
        scenarioInteractor.update(Scenario.ORDER)
        router.routeToWorkersScreen()
    }
}