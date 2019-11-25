package com.example.hutorok.domain.storage

import android.content.Context.MODE_PRIVATE
import com.example.hutorok.App
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.FileNotFoundException
import java.io.IOException
import java.io.OutputStreamWriter


class TurnNumberInteractor(
    private val historyInteractor: IHistoryInteractor,
    private val workersListInteractor: IWorkersListInteractor
) : ITurnNumberInteractor {

    companion object {
        val gson = Gson()
    }

    private val item = BehaviorSubject.create<Int>()

    override fun update(turnNumber: Int) {
        item.onNext(turnNumber)
    }

    override fun get(): Observable<Int> = item

    override fun value(): Int? = item.value

    override fun increment() {
        if (item.value == null) {
            update(0)
        }
        item.value?.run {
            val value = this + 1
            update(value)
            if (value == 1) {
                historyInteractor.add("Начало")
            }
            historyInteractor.add("Ход: $value")
        }
        saveWorkers()
        saveHutorokStatuses()
    }

    private fun saveWorkers() {
        try {
            // отрываем поток для записи
            val bw = BufferedWriter(
                OutputStreamWriter(
                    App.instance.openFileOutput("currentworkers.json", MODE_PRIVATE)
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
            // отрываем поток для записи
            val bw = BufferedWriter(
                OutputStreamWriter(
                    App.instance.openFileOutput("currenthutorok.json", MODE_PRIVATE)
                )
            )
            // пишем данные
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            workersListInteractor.value().forEach {
                jsonArray.put(JSONObject(gson.toJson(it)))
            }
            jsonObject.put("statuses", jsonArray)
            bw.write(jsonObject.toString())
            // закрываем поток
            bw.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}