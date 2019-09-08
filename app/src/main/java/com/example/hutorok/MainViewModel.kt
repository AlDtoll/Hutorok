package com.example.hutorok

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.routing.IGetNowScreenInteractor
import com.example.hutorok.routing.OnBackPressedInteractor
import io.reactivex.BackpressureStrategy

class MainViewModel(
    private val getNowScreenInteractor: IGetNowScreenInteractor,
    private val onBackPressedInteractor: OnBackPressedInteractor
) : IMainViewModel {

    override fun nowScreen(): LiveData<NowScreen> {
        return LiveDataReactiveStreams.fromPublisher(
            getNowScreenInteractor.execute().toFlowable(BackpressureStrategy.LATEST)
        )
    }

    override fun onBackPressed() {
        onBackPressedInteractor.execute()
    }

}