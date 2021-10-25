package doxflow.models.diagram

import common.ChildElement
import doxflow.models.ability.BusinessAbility
import common.KeyInfo
import doxflow.diagram_8x_flow.generateGenerics
import doxflow.dsl.context
import doxflow.models.ability.BusinessAbilityTable

abstract class Evidence<T>(
    val name: String,
    val context: context,
    val role: Role? = null,
    private val note: String? = null,
    override var resource: String = ""
) : ChildElement(name, "class", context), BusinessAbility<T>, KeyInfo<T>, Association {
    var isRole: Boolean = false
    var timestamps: Array<out String>? = null
    private var data: Array<out String>? = null

    /**
     * Evidence的类型：包括rfp，proposal，contract，request, confirmation
     * */
    abstract val type: String

    open fun getUriPrefix(): String = ""

    override var associationType: AssociationType = AssociationType.ONE_TO_ONE

    open fun getUri(): String = when (associationType) {
        AssociationType.ONE_TO_ONE -> "${getUriPrefix()}/$resource"
        AssociationType.ONE_TO_N -> "${getUriPrefix()}/${resource.pluralize()}/{${resource[0]}id}"
        else -> "${getUriPrefix()}/$resource"
    }

    fun addBusinessAbility(table: BusinessAbilityTable) {
        val roleName = role?.element?.name ?: ""
        val serviceName = "${context.element.name}服务"
        val singularUri: String
        when (associationType) {
            AssociationType.ONE_TO_ONE -> singularUri = "${getUriPrefix()}/$resource"

            AssociationType.ONE_TO_N -> {
                val pluralUri = "${getUriPrefix()}/${resource.pluralize()}"
                singularUri = "$pluralUri/{${resource[0]}id}"
                table.addRow(BusinessAbilityTable.Row("POST", pluralUri, "发起$name", serviceName, roleName))
                table.addRow(BusinessAbilityTable.Row("GET", pluralUri, "查看${name}列表", serviceName))
            }
            else -> singularUri = "${getUriPrefix()}/$resource"
        }
        table.addRow(BusinessAbilityTable.Row("GET", singularUri, "查看$name", serviceName, roleName))
        table.addRow(BusinessAbilityTable.Row("PUT", singularUri, "更改$name", serviceName, roleName))
        table.addRow(BusinessAbilityTable.Row("DELETE", singularUri, "取消$name", serviceName, roleName))
    }

    override fun key_timestamps(vararg timestamps: String) = timestamps.let { this.timestamps = it }

    override fun key_data(vararg data: String) = data.let { this.data = it }

    override fun toString(): String {
        return """
            ${note ?: ""}
            class $name${generateGenerics(role) ?: ""}<<$type>> ${if (isRole) "#Orange" else "#HotPink"}{
                ${if (timestamps != null) timestamps.contentToString() else ""}
                ${if (timestamps != null && data != null) ".." else ""}
                ${if (data != null) data.contentToString() else ""}
            }
        """.trimIndent()
    }
}