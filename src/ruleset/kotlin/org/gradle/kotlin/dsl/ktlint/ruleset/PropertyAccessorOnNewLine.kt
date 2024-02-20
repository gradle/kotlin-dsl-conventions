package org.gradle.kotlin.dsl.ktlint.ruleset

import com.pinterest.ktlint.rule.engine.core.api.Rule
import com.pinterest.ktlint.rule.engine.core.api.RuleId
import org.jetbrains.kotlin.KtNodeTypes

import org.jetbrains.kotlin.com.intellij.lang.ASTNode


class PropertyAccessorOnNewLine : Rule(RuleId("gradle-kotlin-dsl:property-get-new-line"), About()) {

    override fun beforeVisitChildNodes(
        node: ASTNode,
        autoCorrect: Boolean,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit
    ) {
        if (node.elementType == KtNodeTypes.PROPERTY_ACCESSOR) {
            if (!node.treePrev.text.contains("\n")) {
                emit(node.startOffset, "Property accessor must be on a new line", false)
            }
        }
    }
}
