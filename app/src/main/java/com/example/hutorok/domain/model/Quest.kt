package com.example.hutorok.domain.model

import org.json.JSONArray
import org.json.JSONObject

class Quest(
    val scenes: List<Scene>
) {
    constructor(jsonObject: JSONObject) : this(
        scenes = parseScenes(jsonObject.optJSONArray("scenes"))
    )

    companion object {
        fun parseScenes(jsonArray: JSONArray?): List<Scene> {
            jsonArray?.run {
                val scenes = mutableListOf<Scene>()
                for (i in 0 until jsonArray.length()) {
                    val scene = Scene(jsonArray.getJSONObject(i))
                    scenes.add(
                        scene
                    )
                }
                return scenes
            }
            return mutableListOf()
        }
    }
}

class Scene(
    val code: String,
    val text: String,
    val selects: List<Select>,
    val type: Type = Type.STEP
) {
    constructor(jsonObject: JSONObject) : this(
        code = jsonObject.optString("code"),
        text = jsonObject.optString("text"),
        selects = parseSelects(jsonObject.optJSONArray("selects")),
        type = if (jsonObject.optString("type").isNotEmpty()) {
            Type.valueOf(jsonObject.optString("type"))
        } else {
            Type.STEP
        }
    )

    companion object {
        fun parseSelects(jsonArray: JSONArray?): List<Select> {
            jsonArray?.run {
                val selects = mutableListOf<Select>()
                for (i in 0 until jsonArray.length()) {
                    val select = Select(jsonArray.getJSONObject(i))
                    selects.add(select)
                }
                return selects
            }
            return mutableListOf()
        }
    }

    enum class Type {
        BEFORE,
        STEP,
        END
    }
}

class Select(
    val task: Task,
    val nextScene: String
) {
    constructor(jsonObject: JSONObject) : this(
        nextScene = jsonObject.optString("nextScene"),
        task = Task(jsonObject.optJSONObject("task"))
    )
}