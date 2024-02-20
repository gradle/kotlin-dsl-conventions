package org.gradle.kotlin.dsl.ktlint.ruleset

import com.pinterest.ktlint.rule.engine.core.api.Rule
import com.pinterest.ktlint.rule.engine.core.api.RuleId

import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes


private
val allowedWildcardImports = listOf(

    "java.util.*",
    "org.gradle.kotlin.dsl.*",

    "org.junit.Assert.*",
    "org.hamcrest.CoreMatchers.*",
    "com.nhaarman.mockito_kotlin.*"
)


class CustomImportsRule : Rule(RuleId("gradle-kotlin-dsl:imports"), About()) {

    override fun beforeVisitChildNodes(
        node: ASTNode,
        autoCorrect: Boolean,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit
    ) {
        if (node.elementType == KtStubElementTypes.IMPORT_DIRECTIVE) {
            val importDirective = node.psi as KtImportDirective
            val path = importDirective.importPath?.pathStr
            if (path != null && path.contains('*') && path !in allowedWildcardImports) {
                emit(node.startOffset, "Wildcard import not allowed ($path)", false)
            }
        }
    }
}
