package com.example.hutorok.screen.start_screen

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.App
import com.example.hutorok.MainActivity
import com.example.hutorok.domain.ILoadDataInteractor
import com.example.hutorok.domain.model.Adventure
import com.example.hutorok.domain.storage.IAdventuresListInteractor
import com.example.hutorok.network.ApiHutorok
import com.example.hutorok.routing.RouteToQuestScreenInteractor
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class StartViewModel(
    private val adventuresListInteractor: IAdventuresListInteractor,
    private val routeToQuestScreenInteractor: RouteToQuestScreenInteractor,
    private val apiHutorok: ApiHutorok,
    private val loadDataInteractor: ILoadDataInteractor
) : IStartViewModel {

    override fun clickAdventure(adventure: Adventure) {
        App.CURRENT_ADVENTURE = adventure.code
        val prefs = App.instance.getSharedPreferences(
            MainActivity.APP_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )
        prefs?.run {
            this.edit().putString(MainActivity.PATH, App.CURRENT_ADVENTURE).apply()
        }
        routeToQuestScreenInteractor.execute()
        loadDataInteractor.execute()
    }

    override fun adventuresData(): LiveData<List<Adventure>> =
        LiveDataReactiveStreams.fromPublisher(
            adventuresListInteractor.get()
                .toFlowable(BackpressureStrategy.LATEST)
        )

    override fun loadAdventuresData(): LiveData<Unit> {
        val observable = apiHutorok.adventures(App.CURRENT_ADVENTURE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                adventuresListInteractor.update(it)
            }
        return LiveDataReactiveStreams
            .fromPublisher(observable.toFlowable(BackpressureStrategy.LATEST))
    }

}