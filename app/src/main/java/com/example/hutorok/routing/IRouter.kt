package com.example.hutorok.routing

import com.example.hutorok.NowScreen
import io.reactivex.Observable

interface IRouter {

    fun nowScreen(): Observable<NowScreen>

    fun routeToStartScreen()

    fun routeToBuildsScreen()

    fun routeToTasksScreen()

    fun routeToWorkersScreen()

    fun onBackPressed()

}