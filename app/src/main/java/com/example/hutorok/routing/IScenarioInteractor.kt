package com.example.hutorok.routing

import io.reactivex.Observable

interface IScenarioInteractor {

    fun update(scenario: Scenario)

    fun get(): Observable<Scenario>

    fun value(): Scenario
}