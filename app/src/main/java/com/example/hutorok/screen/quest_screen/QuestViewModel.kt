package com.example.hutorok.screen.quest_screen

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.App
import com.example.hutorok.MainActivity
import com.example.hutorok.domain.IExecuteTaskInteractor
import com.example.hutorok.domain.model.Quest
import com.example.hutorok.domain.model.Scene
import com.example.hutorok.domain.model.Select
import com.example.hutorok.domain.storage.IQuestInteractor
import com.example.hutorok.domain.storage.ITaskInteractor
import com.example.hutorok.routing.RouteToStartScreenInteractor
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class QuestViewModel(
    private val questInteractor: IQuestInteractor,
    private val routeToStartScreenInteractor: RouteToStartScreenInteractor,
    private val taskInteractor: ITaskInteractor,
    private val executeTaskInteractor: IExecuteTaskInteractor
) : IQuestViewModel {

    private var sceneCode = BehaviorSubject.create<String>()

    override fun clickSelect(select: Select) {
        sceneCode.onNext(select.nextScene)
        taskInteractor.update(select.task)
        executeTaskInteractor.execute(true)
    }

    override fun sceneData(): LiveData<Scene> {
        val observable = Observable.combineLatest(
            sceneCode.startWith(""),
            questInteractor.get(),
            BiFunction { sceneCode: String, quest: Quest ->
                val scenes = quest.scenes
                if (scenes.isEmpty()) {
                    Scene(
                        "end",
                        "Технический конец: квест не содержит сцен",
                        emptyList(),
                        Scene.Type.END
                    )
                } else {
                    val scene = if (sceneCode.isEmpty()) {
                        scenes[0]
                    } else {
                        val findScene = scenes.find { scene -> scene.code == sceneCode }
                        findScene ?: Scene(
                            "end",
                            "Технический конец: переход к неизвестной сцене",
                            emptyList(),
                            Scene.Type.END
                        )
                    }
                    if (scene.type != Scene.Type.END && scene.selects.isEmpty()) {
                        Scene(
                            "end",
                            "Технический конец: нет вариантов выбора",
                            emptyList(),
                            Scene.Type.END
                        )
                    } else {
                        scene
                    }
                }
            }
        )
        return LiveDataReactiveStreams.fromPublisher(
            observable.toFlowable(BackpressureStrategy.LATEST)
        )
    }

    override fun clickEndButton() {
        val prefs = App.instance.getSharedPreferences(
            MainActivity.APP_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )
        prefs?.run {
            if (this.getBoolean(MainActivity.FIRST_RUN, true)) {
                this.edit().putBoolean(MainActivity.FIRST_RUN, false).apply()
            }
        }
        routeToStartScreenInteractor.execute()
    }

}