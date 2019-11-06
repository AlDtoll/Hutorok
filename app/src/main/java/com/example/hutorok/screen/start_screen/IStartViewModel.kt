package com.example.hutorok.screen.start_screen

import androidx.lifecycle.LiveData

interface IStartViewModel {

    fun clickWorkersButton()

    fun clickBuildsButton()

    fun clickTasksButton()

    fun clickEndTurnButton()

    fun clickHistoryButton()

    fun turnNumberData(): LiveData<Int>

}