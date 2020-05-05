package com.example.hutorok.domain

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.hutorok.App
import com.example.hutorok.MainActivity
import com.example.hutorok.R
import com.example.hutorok.domain.model.Quest
import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.domain.storage.*
import com.example.hutorok.network.ApiHutorok
import com.example.hutorok.routing.RouteToStartScreenInteractor
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.json.JSONArray
import org.json.JSONObject
import java.io.*

class LoadDataInteractor(
    private val workersListInteractor: IWorkersListInteractor,
    private val tasksListInteractor: ITasksListInteractor,
    private val buildsListInteractor: IBuildsListInteractor,
    private val importantStatusNamesListInteractor: IImportantStatusNamesListInteractor,
    private val endTasksListInteractor: IEndTasksListInteractor,
    private val turnNumberInteractor: ITurnNumberInteractor,
    private val invisibleStatusNamesListInteractor: IInvisibleStatusNamesListInteractor,
    private val historyInteractor: IHistoryInteractor,
    private val startQuestInteractor: IStartQuestInteractor,
    private val generalDisableStatusListInteractor: IGeneralDisableStatusListInteractor,
    private val routeToStartScreenInteractor: RouteToStartScreenInteractor,
    private val apiHutorok: ApiHutorok
) : ILoadDataInteractor {

    companion object {
        val gson = Gson()
        const val BUILDS = "currenthutorok.json"
        const val WORKERS = "currentworkers.json"
        const val HISTORY = "history.json"
    }

    private val event = PublishSubject.create<Unit>()

    override fun execute() {
        event.onNext(Unit)
    }

    override fun restart() {
        deleteFile(BUILDS)
        deleteFile(WORKERS)
        deleteFile(HISTORY)
        val prefs = App.instance.getSharedPreferences(
            MainActivity.APP_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )
        prefs?.run {
            this.edit().putBoolean(MainActivity.FIRST_RUN, true).apply()
        }
        routeToStartScreenInteractor.execute()
    }

    private fun updateStaticData(
        events: MutableList<String>,
        turnNumber: Int
    ) {
        updateImportantNames()
        updateHistory(events, turnNumber)
        updateInvisibleStatuses()
        updateGeneralDisableStatuses()
    }

    private fun updateImportantNames() {
        val names = listOf(
            "Работал"
        )
        importantStatusNamesListInteractor.update(names)
    }

    private fun updateHistory(events: MutableList<String>, turnNumber: Int) {
        historyInteractor.update(events)
        turnNumberInteractor.update(turnNumber)
    }

    private fun updateInvisibleStatuses() {
        val codes = mutableListOf(
            "?INVISIBLE"
        )
        invisibleStatusNamesListInteractor.update(codes)
    }

    private fun updateGeneralDisableStatuses() {
        val conditions = mutableListOf(
            Triple(
                "worked",
                Task.Symbol.MORE,
                0.0
            ),
            Triple(
                "CHILDREN",
                Task.Symbol.MORE,
                0.0
            ),
            Triple(
                "MortaNAME",
                Task.Symbol.MORE,
                0.0
            )
        )
        generalDisableStatusListInteractor.update(conditions)
    }

    override fun get(): Observable<Unit> {
        val startQuestObservable = event.switchMap {
            startQuestData()
                .map {
                    loadStartQuest(it)
                }
        }
        val buildsObservable = event.switchMap {
            buildsData()
                .map {
                    loadBuilds(it)
                }
        }
        val workersObservable = event.switchMap {
            workersData()
                .map {
                    loadWorkers(it)
                    loadHistory()
                }
        }
        val tasksObservable = event.switchMap {
            tasksData()
                .map {
                    loadTasks(it)
                }
        }
        val endTasksObservable = event.switchMap {
            endTasksData()
                .map {
                    loadEndTasks(it)
                }
        }
        val observableStatic = Observable.merge(
            startQuestObservable,
            buildsObservable,
            workersObservable
        )
        val observableTask = Observable.merge(
            tasksObservable,
            endTasksObservable
        )
        return Observable.merge(
            observableTask,
            observableStatic
        )
    }

    private fun startQuestData(): Observable<Quest> {
        return apiHutorok.startQuest(App.CURRENT_ADVENTURE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun loadStartQuest(quest: Quest) {
        startQuestInteractor.update(quest)
    }

    private fun buildsData(): Observable<List<Status>> {
        return apiHutorok.builds(App.CURRENT_ADVENTURE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun loadBuilds(buildsFromNetwork: List<Status>) {
        val isHutorFilePresent = isFilePresent(BUILDS)
        val builds = if (isHutorFilePresent) {
            val hutorokText = read(BUILDS)
            val buildsFromMemory = mutableListOf<Status>()
            val hutorokStatusObject = JSONObject(hutorokText)
            val statusArray = hutorokStatusObject.getJSONArray("statuses")
            for (i in 0 until statusArray.length()) {
                buildsFromMemory.add(Status(statusArray.getJSONObject(i)))
            }
            buildsFromMemory
        } else {
            buildsFromNetwork
        }
        buildsListInteractor.update(builds as MutableList<Status>)
    }

    private fun workersData(): Observable<List<Worker>> {
        return apiHutorok.workers(App.CURRENT_ADVENTURE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


    private fun loadWorkers(workersFromNetwork: List<Worker>) {
        val isWorkersFilePresent = isFilePresent(WORKERS)
        val workers = if (isWorkersFilePresent) {
            val workersText = read(WORKERS)
            val workersFromMemory = mutableListOf<Worker>()
            val workersObject = JSONObject(workersText)
            val workersArray = workersObject.getJSONArray("workers")
            for (i in 0 until workersArray.length()) {
                workersFromMemory.add(Worker(workersArray.getJSONObject(i)))
            }
            workersFromMemory
        } else {
            workersFromNetwork
        }
        workersListInteractor.update(workers as MutableList<Worker>)
    }

    private fun tasksData(): Observable<List<Task>> {
        return apiHutorok.tasks(App.CURRENT_ADVENTURE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun loadTasks(tasks: List<Task>) {
        tasksListInteractor.update(tasks)
    }

    private fun endTasksData(): Observable<List<Task>> {
        return apiHutorok.endTasks(App.CURRENT_ADVENTURE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun loadEndTasks(tasks: List<Task>) {
        endTasksListInteractor.update(tasks)
    }

    private fun loadHistory() {
        val isHistoryFilePresent = isFilePresent(HISTORY)
        val historyText = if (isHistoryFilePresent) {
            read(HISTORY)
        } else {
            App.instance.resources.openRawResource(R.raw.start)
                .bufferedReader().use { it.readText() }
        }
        val events = mutableListOf<String>()
        val historyObject = JSONObject(historyText)
        val eventsArray = historyObject.getJSONArray("events")
        val turn = historyObject.getInt("turn")
        for (i in 0 until eventsArray.length()) {
            events.add(eventsArray.getString(i))
        }
        updateStaticData(events, turn)
    }

    private fun read(fileName: String): String {
        var string = ""
        val path = App.instance.filesDir.absolutePath + "/" + fileName
        File(path).bufferedReader().readLines().forEach {
            string += it
        }
        return string
    }

    private fun isFilePresent(fileName: String): Boolean {
        val path = App.instance.filesDir.absolutePath + "/" + fileName
        val file = File(path)
        return file.exists()
    }

    override fun saveResult() {
        saveWorkers()
        saveHutorokStatuses()
        saveHistory()
    }

    private fun saveWorkers() {
        try {
            // отрываем поток для записи
            val bw = BufferedWriter(
                OutputStreamWriter(
                    App.instance.openFileOutput(WORKERS, Context.MODE_PRIVATE)
                )
            )
            // пишем данные
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            workersListInteractor.value().forEach {
                jsonArray.put(JSONObject(gson.toJson(it)))
            }
            jsonObject.put("workers", jsonArray)
            bw.write(jsonObject.toString())
            // закрываем поток
            bw.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun saveHutorokStatuses() {
        try {
            val bw = BufferedWriter(
                OutputStreamWriter(
                    App.instance.openFileOutput(BUILDS, Context.MODE_PRIVATE)
                )
            )
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            buildsListInteractor.value().forEach {
                jsonArray.put(JSONObject(gson.toJson(it)))
            }
            jsonObject.put("statuses", jsonArray)
            bw.write(jsonObject.toString())
            // закрываем
            bw.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun saveHistory() {
        try {
            val bw = BufferedWriter(
                OutputStreamWriter(
                    App.instance.openFileOutput(HISTORY, Context.MODE_PRIVATE)
                )
            )
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            historyInteractor.value().forEach {
                jsonArray.put(it)
            }
            jsonObject.put("events", jsonArray)
            jsonObject.put("turn", turnNumberInteractor.value())
            bw.write(jsonObject.toString())
            bw.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun deleteFile(fileName: String) {
        val myFile = File(App.instance.filesDir.absolutePath + "/" + fileName)
        if (myFile.exists()) {
            myFile.delete()
        }
    }

}