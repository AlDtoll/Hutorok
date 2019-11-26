package com.example.hutorok.domain.storage

import com.example.hutorok.domain.EndTurnInteractor.Companion.END_TURN_PREFIX
import com.example.hutorok.domain.ExecuteTaskInteractor.Companion.EXECUTE_TASK_PREFIX
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class HistoryInteractor : IHistoryInteractor {

    private val list = BehaviorSubject.create<MutableList<String>>()

    override fun update(statuses: MutableList<String>) {
        list.onNext(statuses)
    }

    override fun get(): Observable<MutableList<String>> = list

    override fun add(message: String) {
        if (list.value == null) {
            update(mutableListOf())
        }
        list.value?.run {
            this.add(convertMessage(message))
            update(this)

        }
    }

    private fun convertMessage(message: String): String {
        if (message.contains(END_TURN_PREFIX)) {
            return message.replace(END_TURN_PREFIX, "")
        }
        return message.replace(EXECUTE_TASK_PREFIX, "")
    }

    override fun value(): MutableList<String> = list.value ?: emptyList<String>().toMutableList()

}
