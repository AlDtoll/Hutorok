package com.example.hutorok

import androidx.lifecycle.LiveData

interface IMainViewModel {

    fun nowScreen(): LiveData<NowScreen>

    fun onBackPressed()

}