package com.example.hutorok.network

import com.example.hutorok.domain.model.*
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiHutorok {

    @GET("hutorok/{adventure}/workers")
    fun workers(@Path("adventure") adventure: String): Observable<List<Worker>>

    @GET("hutorok/{adventure}/builds")
    fun builds(@Path("adventure") adventure: String): Observable<List<Status>>

    @GET("hutorok/{adventure}/startquest")
    fun startQuest(@Path("adventure") adventure: String): Observable<Quest>

    @GET("hutorok/{adventure}/tasks")
    fun tasks(@Path("adventure") adventure: String): Observable<List<Task>>

    @GET("hutorok/{adventure}/endtasks")
    fun endTasks(@Path("adventure") adventure: String): Observable<List<Task>>

    @GET("hutorok/{adventure}/adventures")
    fun adventures(@Path("adventure") adventure: String): Observable<List<Adventure>>

}