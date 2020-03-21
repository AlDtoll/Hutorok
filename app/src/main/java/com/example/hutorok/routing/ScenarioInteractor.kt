package com.example.hutorok.routing

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class ScenarioInteractor : IScenarioInteractor {

    private val value = BehaviorSubject.create<Scenario>()

    override fun update(scenario: Scenario) {
        value.onNext(scenario)
    }

    override fun get(): Observable<Scenario> = value

    override fun value(): Scenario = value.value ?: Scenario.SPEAK

}