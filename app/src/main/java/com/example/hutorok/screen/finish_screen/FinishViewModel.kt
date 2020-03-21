package com.example.hutorok.screen.finish_screen

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.App
import com.example.hutorok.MainActivity
import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.storage.IBuildsListInteractor
import com.example.hutorok.routing.OnBackPressedInteractor
import io.reactivex.BackpressureStrategy

class FinishViewModel(
    private val buildsListInteractor: IBuildsListInteractor,
    private val onBackPressedInteractor: OnBackPressedInteractor
) : IFinishViewModel {

    override fun clickEndButton() {
        App.instance.cacheDir.deleteRecursively()
        val prefs = App.instance.getSharedPreferences(
            MainActivity.APP_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )
        prefs?.run {
            this.edit().putBoolean(MainActivity.FIRST_RUN, true).apply()
        }
        onBackPressedInteractor.execute()
    }

    override fun finishData(): LiveData<Status> {
        val observable = buildsListInteractor.get()
            .map {
                it.find { status -> status.code == "DEFEAT" || status.code == "VICTORY" } ?: Status(
                    "DEFEAT",
                    "Поражение"
                )
            }
        return LiveDataReactiveStreams.fromPublisher(
            observable.toFlowable(BackpressureStrategy.LATEST)
        )
    }

}