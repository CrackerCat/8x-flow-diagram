package models

import dsl.ASSOCIATE
import dsl.PLAY_TO

data class Participant(val name: String, val type: Type) {
    private val associates: MutableList<Evidence<*>> = mutableListOf()

    enum class Type {
        PARTY,
        PLACE,
        THING
    }

    infix fun <T> associate(associate: Evidence<out T>) {
        associates.add(associate)
    }

    override fun toString(): String {
        return buildString {
            appendLine("""class $name <<${type.name.lowercase()}>> #ForestGreen""".trimIndent())
            associates.forEach {
                appendLine("$name $ASSOCIATE ${it.name}")
            }
        }
    }
}