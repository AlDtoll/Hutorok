package com.example.hutorok

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.routing.IGetNowScreenInteractor
import io.reactivex.BackpressureStrategy

class MainViewModel(
    private val getNowScreenInteractor: IGetNowScreenInteractor
) : IMainViewModel {

    override fun nowScreen(): LiveData<NowScreen> {
        return LiveDataReactiveStreams.fromPublisher(
            getNowScreenInteractor.execute().toFlowable(BackpressureStrategy.LATEST)
        )
    }

}