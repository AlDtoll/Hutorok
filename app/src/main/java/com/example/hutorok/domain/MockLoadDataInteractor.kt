package com.example.hutorok.domain

import com.example.hutorok.domain.model.*
import com.example.hutorok.domain.storage.IHutorStatusesListInteractor
import com.example.hutorok.domain.storage.ITasksListInteractor
import com.example.hutorok.domain.storage.IWorkersListInteractor

class MockLoadDataInteractor(
    private val workersListInteractor: IWorkersListInteractor,
    private val tasksListInteractor: ITasksListInteractor,
    private val hutorStatusesListInteractor: IHutorStatusesListInteractor
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
                ),
                listOf(
                    TaskResult(
                        TaskTarget.HUTOR,
                        TaskAction.CHANGE_STATUS_VALUE,
                        Status(
                            "woods",
                            "Строительная древесина",
                            "",
                            0.0,
                            true
                        ),
                        "Удалось добыть N дерева"
                    )
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
                ),
                listOf(
                    TaskResult(
                        TaskTarget.HUTOR,
                        TaskAction.CHANGE_STATUS_VALUE,
                        Status(
                            "foods",
                            "Пища",
                            "",
                            0.0,
                            true
                        ),
                        "Удалось добыть N дерева"
                    )
                )
            )
        )
        tasksListInteractor.update(tasks)

        val statuses = listOf(
            Status(
                "houseBuild",
                "Жилой дом",
                "Тут живут люди",
                1.0,
                true
            ),
            Status(
                "houseBuild",
                "Жилой дом",
                "Тут живут люди",
                1.0,
                true
            ),
            Status(
                "forest",
                "Лес",
                "Хутор окружен деревьями",
                1.0,
                true
            )
        )
        hutorStatusesListInteractor.update(statuses)
    }

}