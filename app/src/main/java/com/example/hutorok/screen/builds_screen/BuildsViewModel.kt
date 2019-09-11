package com.example.hutorok.screen.builds_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.storage.IHutorStatusesListInteractor
import io.reactivex.BackpressureStrategy

class BuildsViewModel(
    private val hutorStatusesListInteractor: IHutorStatusesListInteractor
) : IBuildsViewModel {

    override fun statusesData(): LiveData<List<Status>> {
        val observable = hutorStatusesListInteractor.get().map {
            it.filter { status -> status.visible }
        }
        return LiveDataReactiveStreams.fromPublisher(
            observable.toFlowable(BackpressureStrategy.LATEST)
        )
    }

}