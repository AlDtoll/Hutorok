package com.example.hutorok.screen.history_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.domain.storage.IHistoryInteractor
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

class HistoryViewModel(
    private val historyInteractor: IHistoryInteractor
) : IHistoryViewModel {

    private var search = PublishSubject.create<String>()

    override fun historyData(): LiveData<List<String>> {
        val filtered = Observable.combineLatest(historyInteractor.get(),
            search.startWith(""),
            BiFunction { list: MutableList<String>, search: String ->
                if (search.isBlank()) {
                    list
                } else {
                    list.filter { it.contains(search, true) }
                }.reversed()
            })
        return LiveDataReactiveStreams
            .fromPublisher(filtered.toFlowable(BackpressureStrategy.LATEST))
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