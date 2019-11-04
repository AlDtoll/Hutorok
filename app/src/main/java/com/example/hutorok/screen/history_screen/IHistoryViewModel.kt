package com.example.hutorok.screen.history_screen

import androidx.lifecycle.LiveData

interface IHistoryViewModel {

    fun historyData(): LiveData<List<String>>

    fun searchChange(searchData: String)

    fun searchData(): LiveData<String>

    fun clickClearButton()
}