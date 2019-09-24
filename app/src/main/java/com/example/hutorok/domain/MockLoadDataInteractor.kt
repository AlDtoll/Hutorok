package com.example.hutorok.domain

import com.example.hutorok.domain.model.*
import com.example.hutorok.domain.storage.IHutorStatusesListInteractor
import com.example.hutorok.domain.storage.IImportantStatusNamesListInteractor
import com.example.hutorok.domain.storage.ITasksListInteractor
import com.example.hutorok.domain.storage.IWorkersListInteractor

class MockLoadDataInteractor(
    private val workersListInteractor: IWorkersListInteractor,
    private val tasksListInteractor: ITasksListInteractor,
    private val hutorStatusesListInteractor: IHutorStatusesListInteractor,
    private val importantStatusNamesListInteractor: IImportantStatusNamesListInteractor
) : ILoadDataInteractor {

    override fun update() {
        updateWorkers()

        updateTasks()

        updateHutorStatuses()

        updateImportantNames()
    }

    private fun updateWorkers() {
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
    }

    private fun updateTasks() {
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
                            "woodsRES",
                            "Строительная древесина",
                            "",
                            0.0,
                            true
                        ),
                        "Удалось добыть N дерева"
                    ),
                    TaskResult(
                        TaskTarget.HUTOR,
                        TaskAction.CHANGE_STATUS_VALUE,
                        Status(
                            "felling",
                            "Вырубка",
                            "Когда-то здесь был лес, а теперь только пни",
                            1.0,
                            true
                        ),
                        "Возле селения образовалась вырубка"
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
                            "foodsRES",
                            "Пища",
                            "",
                            0.0,
                            true
                        ),
                        "Удалось добыть N еды"
                    )
                )
            ),
            Task(
                "buildHouse",
                "Строить жилой дом",
                "Дом из дерева. 35 древесины, 35 очков строительства",
                TaskFunction(
                    listOf(
                        Pair("builder", 1.0),
                        Pair("hasTool", 1.5)
                    )
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
                            "houseRES",
                            "Очки строительства дома",
                            "",
                            0.0,
                            true
                        ),
                        "Удалось построить N очков"
                    )
                ),
                listOf(
                    Pair("woodsRES", 0.0)
                )
            ),
            Task(
                "completeHouse",
                "Завершить строительство жилого дома",
                "Отнимет 35 древесины и 35 очков строительства",
                TaskFunction(
                    emptyList()
                ),
                TaskFunction(
                    emptyList()
                ),
                listOf(
                    TaskResult(
                        TaskTarget.HUTOR,
                        TaskAction.ADD_STATUS,
                        Status(
                            "houseBUILDING",
                            "Жилой дом",
                            "Тут живут люди",
                            1.0,
                            true
                        ),
                        "Завершено строительство дома"
                    ),
                    TaskResult(
                        TaskTarget.HUTOR,
                        TaskAction.CHANGE_STATUS_VALUE_BY_FIXED_POINT,
                        Status(
                            "houseRES",
                            "-",
                            "-",
                            -35.0,
                            true
                        ),
                        "Количество древесины стало меньше на 35"
                    ),
                    TaskResult(
                        TaskTarget.HUTOR,
                        TaskAction.CHANGE_STATUS_VALUE_BY_FIXED_POINT,
                        Status(
                            "woodsRES",
                            "-",
                            "-",
                            -35.0,
                            true
                        ),
                        "Количество очков строительства стало меньше на 35"
                    )
                ),
                listOf(
                    Pair("woodsRES", 35.0),
                    Pair("houseRES", 35.0)
                )
            )
        )
        tasksListInteractor.update(tasks)
    }

    private fun updateHutorStatuses() {
        val statuses = listOf(
            Status(
                "houseBUILDING",
                "Жилой дом",
                "Тут живут люди",
                1.0,
                true
            ),
            Status(
                "houseBUILDING",
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
            ),
            Status(
                "foodsRES",
                "Еда",
                "Нужна для пропитания хутора",
                10.0,
                true
            )
        )
        hutorStatusesListInteractor.update(statuses)
    }

    private fun updateImportantNames() {
        val names = listOf(
            "Работал"
        )
        importantStatusNamesListInteractor.update(names)
    }

}