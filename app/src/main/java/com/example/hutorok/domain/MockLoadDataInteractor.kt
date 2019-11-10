package com.example.hutorok.domain

import com.example.hutorok.domain.model.*
import com.example.hutorok.domain.model.TaskFunction.Companion.MINUS_ITSELF_VALUE
import com.example.hutorok.domain.storage.*

class MockLoadDataInteractor(
    private val workersListInteractor: IWorkersListInteractor,
    private val tasksListInteractor: ITasksListInteractor,
    private val hutorStatusesListInteractor: IHutorStatusesListInteractor,
    private val importantStatusNamesListInteractor: IImportantStatusNamesListInteractor,
    private val endTasksListInteractor: IEndTasksListInteractor,
    private val turnNumberInteractor: ITurnNumberInteractor
) : ILoadDataInteractor {

    override fun update() {
        updateWorkers()

        updateTasks()

        updateHutorStatuses()

        updateImportantNames()

        updateEndTasks()

        startHistory()
    }

    private fun updateWorkers() {
        val workers = mutableListOf(
            Worker(
                "Гавриил",
                "С копьем ходил",
                Age.ADULT,
                mutableListOf(
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
                mutableListOf(
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
                        Pair("craftsman", 1.0),
                        Pair("?DISEASE", -1.0),
                        Pair("worked", MINUS_ITSELF_VALUE)
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
                        TaskResult.TaskTarget.HUTOR,
                        TaskResult.TaskAction.CHANGE_STATUS_VALUE,
                        Status(
                            "woodsRES",
                            "Строительная древесина",
                            "",
                            0.0,
                            true
                        ),
                        "Удалось добыть #VALUE дерева"
                    ),
                    TaskResult(
                        TaskResult.TaskTarget.HUTOR,
                        TaskResult.TaskAction.CHANGE_STATUS_VALUE,
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
                        Pair("hasBow", 1.5),
                        Pair("?DISEASE", -1.0)
                    ),
                    4
                ),
                TaskFunction(
                    emptyList(),
                    0
                ),
                listOf(
                    TaskResult(
                        TaskResult.TaskTarget.HUTOR,
                        TaskResult.TaskAction.CHANGE_STATUS_VALUE,
                        Status(
                            "foodsRES",
                            "Пища",
                            "",
                            0.0,
                            true
                        ),
                        "Удалось добыть #VALUE еды"
                    )
                )
            ),
            Task(
                "getBuilds",
                "Заниматься строительством",
                "Возводить дома, частоколы, памятники и т.д",
                TaskFunction(
                    listOf(
                        Pair("builder", 1.0),
                        Pair("hasTool", 1.5),
                        Pair("?DISEASE", -1.0),
                        Pair("worked", MINUS_ITSELF_VALUE)
                    )
                ),
                TaskFunction(
                    emptyList(),
                    0
                ),
                listOf(
                    TaskResult(
                        TaskResult.TaskTarget.HUTOR,
                        TaskResult.TaskAction.CHANGE_STATUS_VALUE,
                        Status(
                            "buildRES",
                            "Очки строительства дома",
                            "",
                            0.0,
                            true
                        ),
                        "Удалось построить #VALUE очков"
                    )
                ),
                listOf(
                    Triple("woodsRES", Task.Symbol.MORE, 1.0)
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
                        TaskResult.TaskTarget.HUTOR,
                        TaskResult.TaskAction.ADD_STATUS,
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
                        TaskResult.TaskTarget.HUTOR,
                        TaskResult.TaskAction.CHANGE_STATUS_VALUE_BY_FIXED_POINT,
                        Status(
                            "buildRES",
                            "-",
                            "-",
                            -35.0,
                            true
                        ),
                        "Количество древесины стало меньше на 35"
                    ),
                    TaskResult(
                        TaskResult.TaskTarget.HUTOR,
                        TaskResult.TaskAction.CHANGE_STATUS_VALUE_BY_FIXED_POINT,
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
                    Triple("woodsRES", Task.Symbol.MORE, 35.0),
                    Triple("buildRES", Task.Symbol.MORE, 35.0)
                ),
                Task.Type.BUILD
            ),
            Task(
                "makeTool",
                "Сделать инструмент",
                "Создать мотыго-лопато-топор",
                TaskFunction.nothing(),
                TaskFunction.nothing(),
                listOf(
                    TaskResult(
                        TaskResult.TaskTarget.HUTOR,
                        TaskResult.TaskAction.CHANGE_STATUS_VALUE_BY_FIXED_POINT,
                        Status(
                            "toolsRES",
                            "Инструмент",
                            "Мотыго-лопато-топор",
                            1.0,
                            true
                        ),
                        "Был сделан инструмент"
                    )
                )
            ),
            Task(
                "giveTool",
                "Дать инструмент",
                "Отдать мотыго-лопато-топор рабочему",
                TaskFunction.nothing(),
                TaskFunction.nothing(),
                listOf(
                    TaskResult(
                        TaskResult.TaskTarget.ONE_SELECTED_WORKER,
                        TaskResult.TaskAction.CHANGE_STATUS_VALUE_BY_FIXED_POINT,
                        Status(
                            "hasTool",
                            "Владеет рабочим инструментом",
                            "Инструмент помогает лучше осуществлять рабочую деятельность",
                            1.0,
                            true
                        ),
                        "#WORKER получил инструмент"
                    ),
                    TaskResult(
                        TaskResult.TaskTarget.HUTOR,
                        TaskResult.TaskAction.CHANGE_STATUS_VALUE_BY_FIXED_POINT,
                        Status(
                            "toolsRES",
                            "Инструмент",
                            "Мотыго-лопато-топор",
                            -1.0,
                            true
                        )
                    )
                ),
                listOf(
                    Triple("toolsRES", Task.Symbol.MORE, 0.0)
                ),
                Task.Type.PERSON,
                listOf(
                    Triple("hasTool", Task.Symbol.LESS, 1.0)
                )
            ),
            Task(
                "takeTool",
                "Взять инструмент",
                "Забрать мотыго-лопато-топор у рабочего",
                TaskFunction.nothing(),
                TaskFunction.nothing(),
                listOf(
                    TaskResult(
                        TaskResult.TaskTarget.ONE_SELECTED_WORKER,
                        TaskResult.TaskAction.CHANGE_STATUS_VALUE_BY_FIXED_POINT,
                        Status(
                            "hasTool",
                            "Владеет рабочим инструментом",
                            "Инструмент помогает лучше осуществлять рабочую деятельность",
                            -1.0,
                            true
                        ),
                        "#WORKER остался без инструмента"
                    ),
                    TaskResult(
                        TaskResult.TaskTarget.HUTOR,
                        TaskResult.TaskAction.CHANGE_STATUS_VALUE_BY_FIXED_POINT,
                        Status(
                            "toolsRES",
                            "Инструмент",
                            "Мотыго-лопато-топор",
                            1.0,
                            true
                        )
                    )
                ),
                emptyList(),
                Task.Type.PERSON,
                listOf(
                    Triple("hasTool", Task.Symbol.MORE, 0.0)
                )
            ),
            Task(
                "healWorkDisease",
                "Лечиться от недомогания",
                "Отдохнуть, да травы пожевать",
                TaskFunction.nothing(),
                TaskFunction.nothing(),
                listOf(
                    TaskResult(
                        TaskResult.TaskTarget.ONE_SELECTED_WORKER,
                        TaskResult.TaskAction.REMOVE_STATUS,
                        Status(
                            "workDISEASE",
                            "Недомогание",
                            "Жар и тошнота",
                            1.0,
                            true
                        ),
                        "#WORKER излечился от недомогания"
                    ),
                    TaskResult(
                        TaskResult.TaskTarget.ONE_SELECTED_WORKER,
                        TaskResult.TaskAction.ADD_STATUS,
                        Status(
                            "alreadyHeal",
                            "Уже лечился",
                            "На это ходу этот персонаж уже лечился",
                            1.0,
                            false
                        )
                    )
                ),
                emptyList(),
                Task.Type.PERSON,
                listOf(
                    Triple("workDISEASE", Task.Symbol.MORE, 0.0),
                    Triple("alreadyHeal", Task.Symbol.LESS, 1.0)
                )
            ),
            Task(
                "findMagicStones",
                "Искать магические камни",
                "Искать магические камни вокруг поселения",
                TaskFunction.nothing(),
                TaskFunction.nothing(),
                listOf(
                    TaskResult(
                        TaskResult.TaskTarget.HUTOR,
                        TaskResult.TaskAction.CHANGE_STATUS_BY_RANDOM_VALUE,
                        Status(
                            "magicStonesRES",
                            "Магический камень",
                            "Используется для творения волшебства и магических ритуалов",
                            5.0,
                            true
                        ),
                        "Нашлось #VALUE магических камней",
                        listOf(
                            Pair(Triple("forest", Task.Symbol.MORE, 0.0), 50.0)
                        ),
                        "Не удалось найти камней"
                    )
                ),
                emptyList(),
                Task.Type.WORK
            ),
            Task(
                "callWorker",
                "Провести ритуал призвания жителя",
                "Ритуал потратит 1 магический камень",
                TaskFunction.nothing(),
                TaskFunction.nothing(),
                listOf(
                    TaskResult(
                        TaskResult.TaskTarget.HUTOR,
                        TaskResult.TaskAction.ADD_WORKER,
                        Status(
                            "ADULT",
                            "Никодим",
                            "Носит шапку соболью",
                            5.0,
                            true
                        ),
                        "К селению присоединился #WORKER"
                    ),
                    TaskResult(
                        TaskResult.TaskTarget.ONE_SELECTED_WORKER,
                        TaskResult.TaskAction.CHANGE_STATUS_VALUE_BY_FIXED_POINT,
                        Status(
                            "hasSobolHat",
                            "Соболья шапка",
                            "Шапка из соболя",
                            1.0,
                            true
                        ),
                        ""
                    ),
                    TaskResult(
                        TaskResult.TaskTarget.HUTOR,
                        TaskResult.TaskAction.CHANGE_STATUS_VALUE_BY_FIXED_POINT,
                        Status(
                            "magicStonesRES",
                            "Магический камень",
                            "Используется для творения волшебства и магических ритуалов",
                            -1.0,
                            true
                        )
                    )
                ),
                listOf(
                    Triple("magicStonesRES", Task.Symbol.MORE, 0.0)
                ),
                Task.Type.BUILD
            )
        )
        tasksListInteractor.update(tasks)
    }

    private fun updateHutorStatuses() {
        val statuses = mutableListOf(
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

    private fun updateEndTasks() {
        val tasks = emptyList<Task>()
        endTasksListInteractor.update(tasks)
    }

    private fun startHistory() {
        turnNumberInteractor.increment()
    }

}