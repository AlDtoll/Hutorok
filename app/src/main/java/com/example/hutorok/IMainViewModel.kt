package com.example.hutorok

import android.view.MenuItem
import androidx.lifecycle.LiveData

interface IMainViewModel {

    fun nowScreen(): LiveData<NowScreen>

    fun onBackPressed()

    fun messageData(): LiveData<String>

    fun showAdventures()

    fun clickAction(menuItem: MenuItem)

    fun turnNumberData(): LiveData<Int>

    fun getNavigationElementsVisibility(): LiveData<Boolean>

    fun onClose()

    fun loadDataResponse(): LiveData<Unit>

}