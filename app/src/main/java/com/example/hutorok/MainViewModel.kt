package com.example.hutorok

import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.domain.ILoadDataInteractor
import com.example.hutorok.domain.storage.IMessageInteractor
import com.example.hutorok.domain.storage.INavigationElementsVisibilityInteractor
import com.example.hutorok.domain.storage.ITurnNumberInteractor
import com.example.hutorok.routing.*
import io.reactivex.BackpressureStrategy

class MainViewModel(
    //todo очень толстая модель
    private val getNowScreenInteractor: IGetNowScreenInteractor,
    private val onBackPressedInteractor: OnBackPressedInteractor,
    private val loadDataInteractor: ILoadDataInteractor,
    private val messageInteractor: IMessageInteractor,
    private val routeToStartScreenInteractor: RouteToStartScreenInteractor,
    private val routeToWorkersScreenInteractor: RouteToWorkersScreenInteractor,
    private val routeToBuildsScreenInteractor: RouteToBuildsScreenInteractor,
    private val routeToTasksScreenInteractor: RouteToTasksScreenInteractor,
    private val routeToHistoryScreenInteractor: RouteToHistoryScreenInteractor,
    private val turnNumberInteractor: ITurnNumberInteractor,
    private val navigationElementsVisibilityInteractor: INavigationElementsVisibilityInteractor
) : IMainViewModel {

    override fun nowScreen(): LiveData<NowScreen> {
        return LiveDataReactiveStreams.fromPublisher(
            getNowScreenInteractor.execute().toFlowable(BackpressureStrategy.LATEST)
        )
    }

    override fun onBackPressed() {
        onBackPressedInteractor.execute()
    }

    override fun messageData(): LiveData<String> {
        return LiveDataReactiveStreams.fromPublisher(
            messageInteractor.get().toFlowable(BackpressureStrategy.LATEST)
        )
    }

    override fun showAdventures() {
        routeToStartScreenInteractor.execute()
    }

    override fun clickAction(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.action_workers -> routeToWorkersScreenInteractor.execute()
            R.id.action_builds -> routeToBuildsScreenInteractor.execute()
            R.id.action_tasks -> routeToTasksScreenInteractor.execute()
            R.id.action_history -> routeToHistoryScreenInteractor.execute()
            android.R.id.home -> onBackPressed()
        }
    }

    override fun turnNumberData(): LiveData<Int> =
        LiveDataReactiveStreams.fromPublisher(
            turnNumberInteractor.get()
                .toFlowable(BackpressureStrategy.LATEST)
        )

    override fun getNavigationElementsVisibility(): LiveData<Boolean> {
        return LiveDataReactiveStreams.fromPublisher(
            navigationElementsVisibilityInteractor.get().toFlowable(BackpressureStrategy.LATEST)
        )
    }

    override fun onClose() {
        routeToTasksScreenInteractor.execute()
    }

    override fun loadDataResponse(): LiveData<Unit> {
        return LiveDataReactiveStreams.fromPublisher(
            loadDataInteractor.get().toFlowable(BackpressureStrategy.LATEST)
        )
    }

}