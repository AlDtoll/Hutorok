package com.example.hutorok.screen.quest_screen

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.App
import com.example.hutorok.MainActivity
import com.example.hutorok.domain.IExecuteTaskInteractor
import com.example.hutorok.domain.ILoadDataInteractor
import com.example.hutorok.domain.model.*
import com.example.hutorok.domain.storage.*
import com.example.hutorok.routing.RouteToTasksScreenInteractor
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.reactivex.subjects.BehaviorSubject

class QuestViewModel(
    private val startQuestInteractor: IStartQuestInteractor,
    private val routeToTasksScreenInteractor: RouteToTasksScreenInteractor,
    private val taskInteractor: ITaskInteractor,
    private val executeTaskInteractor: IExecuteTaskInteractor,
    private val buildsListInteractor: IBuildsListInteractor,
    private val messageInteractor: IMessageInteractor,
    private val questInteractor: IQuestInteractor,
    private val loadDataInteractor: ILoadDataInteractor,
    private val turnNumberInteractor: ITurnNumberInteractor
) : IQuestViewModel {

    private var sceneCode = BehaviorSubject.create<String>()

    override fun clickSelect(select: Select) {
        taskInteractor.update(select.task)
        questInteractor.update(true)
        executeTaskInteractor.execute()
        sceneCode.onNext(select.nextScene)
    }

    override fun executeTaskDataResponse(): LiveData<Unit> =
        LiveDataReactiveStreams.fromPublisher(
            executeTaskInteractor.get().toFlowable(BackpressureStrategy.LATEST)
        )

    override fun sceneData(): LiveData<Scene> {
        val observable = Observable.combineLatest(
            sceneCode.startWith(""),
            startQuestInteractor.get(),
            buildsListInteractor.get(),
            Function3 { sceneCode: String, quest: Quest, statusesList: List<Status> ->
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
                        val filter = scene.selects.filter { select ->
                            Task.allConditionsIsComplete(
                                select.task.permissiveConditions,
                                statusesList
                            )
                        }
                        scene.selects = filter
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
        turnNumberInteractor.increment()
        loadDataInteractor.saveResult()
        routeToTasksScreenInteractor.execute()
    }

    override fun previousSelectResultData(): LiveData<String> {
        return LiveDataReactiveStreams.fromPublisher(
            messageInteractor.get().toFlowable(BackpressureStrategy.LATEST)
        )
    }

}