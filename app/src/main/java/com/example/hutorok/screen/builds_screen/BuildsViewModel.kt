package com.example.hutorok.screen.builds_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.BuildConfig
import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.storage.IHutorStatusesListInteractor
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

class BuildsViewModel(
    private val hutorStatusesListInteractor: IHutorStatusesListInteractor
) : IBuildsViewModel {

    private var search = PublishSubject.create<String>()

    override fun statusesData(): LiveData<List<Status>> {
        val observable = hutorStatusesListInteractor.get().map {
            if (BuildConfig.DEBUG) {
                it
            } else {
                it.filter { status -> status.visible }
            }
        }
        val filtered = Observable.combineLatest(observable,
            search.startWith(""),
            BiFunction { list: List<Status>, search: String ->
                if (search.isBlank()) {
                    list
                } else {
                    list.filter {
                        it.name.contains(search, true) || it.description.contains(
                            search,
                            true
                        )
                    }
                }
            })
        return LiveDataReactiveStreams.fromPublisher(
            filtered.toFlowable(BackpressureStrategy.LATEST)
        )
    }

    override fun searchChange(searchData: String) {
        search.onNext(searchData)
    }

    override fun clickClearButton() {
        search.onNext("")
    }

    override fun searchData(): LiveData<String> =
        LiveDataReactiveStreams.fromPublisher(search.toFlowable(BackpressureStrategy.LATEST))

}