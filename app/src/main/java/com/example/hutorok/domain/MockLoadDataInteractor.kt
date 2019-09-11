package com.example.hutorok.domain

import com.example.hutorok.domain.model.*
import com.example.hutorok.domain.storage.ITasksListInteractor
import com.example.hutorok.domain.storage.IWorkersListInteractor

class MockLoadDataInteractor(
    private val workersListInteractor: IWorkersListInteractor,
    private val tasksListInteractor: ITasksListInteractor
) : ILoadDataInteractor {

    override fun update() {
        val workers = listOf(
            Worker(
                "Гавриил",
                "С копьем ходил",
                Age.ADULT,
                listOf(
                    Status(
                        "hasTool",
                        "Владеет рабочим инструментом",
                        "Инструмент помогает лучше осуществлять рабочую деятельность",
                        1.0,
                        true
                    ),
                    Status(
                        "lumberjackExp",
                        "Опыт в дровосечестве",
                        "Заработав достаточно опыта, будет лучше рубить деревья",
                        5.0,
                        false
                    )
                )
            ),
            Worker(
                "Агафья",
                "",
                Age.CHILDREN,
                listOf(
                    Status(
                        "hasRelative",
                        "Есть родственник",
                        "Люди с родственниками реже попадают в неприятности",
                        1.0,
                        true
                    ),
                    Status(
                        "hasPet",
                        "Есть питомец",
                        "Кошка, собака, а можеть птичка",
                        1.0,
                        false
                    )
                )
            )
        )
        workersListInteractor.update(workers)

        val tasks = listOf(
            Task(
                "getWoods",
                "Добыть строительной древесины",
                "Для этой задачи лучше подойдут ремесленники и люди с инструментами",
                TaskFunction(
                    listOf(
                        Pair("hasTool", 1.5),
                        Pair("craftsman", 1.0)
                    )
                ),
                TaskFunction(
                    listOf(
                        Pair("forest", 1.0),
                        Pair("wasteland", -3.0)
                    ),
                    0
                )
            ),
            Task(
                "getFoods",
                "Добыть какой-то еды",
                "Охота, собирательство и вот это все",
                TaskFunction(
                    listOf(
                        Pair("hunter", 1.0),
                        Pair("hasBow", 1.5)
                    ),
                    4
                ),
                TaskFunction(
                    emptyList(),
                    0
                )
            )
        )
        tasksListInteractor.update(tasks)
    }

}