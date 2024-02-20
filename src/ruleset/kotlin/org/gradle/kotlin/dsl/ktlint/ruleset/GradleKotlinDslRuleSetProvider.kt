package org.gradle.kotlin.dsl.ktlint.ruleset

import com.pinterest.ktlint.cli.ruleset.core.api.RuleSetProviderV3
import com.pinterest.ktlint.rule.engine.core.api.RuleProvider
import com.pinterest.ktlint.rule.engine.core.api.RuleSetId
import com.pinterest.ktlint.ruleset.standard.rules.*


/**
 * Gradle Kotlin DSL ktlint RuleSetProvider.
 *
 * Reuse ktlint-standard-ruleset rules and add custom ones.
 */
class GradleKotlinDslRuleSetProvider : RuleSetProviderV3(RuleSetId("gradle-kotlin-dsl")) {

    override fun getRuleProviders(): Set<RuleProvider> = setOf(
        // ktlint standard ruleset rules --------------------------
        // See https://github.com/pinterest/ktlint/blob/master/ktlint-ruleset-standard/src/main/kotlin/com/pinterest/ktlint/ruleset/standard/StandardRuleSetProvider.kt

        // kotlin-dsl: disabled in favor of CustomChainWrappingRule
        // ChainWrappingRule(),
        RuleProvider { CommentSpacingRule() },
        RuleProvider { FilenameRule() },
        RuleProvider { FinalNewlineRule() },
        // disabled until it's clear how to reconcile difference in Intellij & Android Studio import layout
        // ImportOrderingRule(),
        RuleProvider { IndentationRule() },
        RuleProvider { MaxLineLengthRule() },
        RuleProvider { ModifierOrderRule() },
        RuleProvider { NoBlankLineBeforeRbraceRule() },
        // kotlin-dsl disabled in favor of BlankLinesRule
        // NoConsecutiveBlankLinesRule(),
        RuleProvider { NoEmptyClassBodyRule() },
        RuleProvider { NoLineBreakAfterElseRule() },
        RuleProvider { NoLineBreakBeforeAssignmentRule() },
        RuleProvider { NoMultipleSpacesRule() },
        RuleProvider { NoSemicolonsRule() },
        RuleProvider { NoTrailingSpacesRule() },
        RuleProvider { NoUnitReturnRule() },
        RuleProvider { NoUnusedImportsRule() },
        // kotlin-dsl: disabled in favor of CustomImportsRule
        // NoWildcardImportsRule(),
        RuleProvider { ParameterListWrappingRule() },
        RuleProvider { SpacingAroundColonRule() },
        RuleProvider { SpacingAroundCommaRule() },
        RuleProvider { SpacingAroundCurlyRule() },
        RuleProvider { SpacingAroundDotRule() },
        RuleProvider { SpacingAroundKeywordRule() },
        RuleProvider { SpacingAroundOperatorsRule() },
        RuleProvider { SpacingAroundParensRule() },
        RuleProvider { SpacingAroundRangeOperatorRule() },
        RuleProvider { StringTemplateRule() },

        // gradle-kotlin-dsl rules --------------------------------

        RuleProvider { BlankLinesRule() },
        RuleProvider { CustomChainWrappingRule() },
        RuleProvider { CustomImportsRule() },
        RuleProvider { VisibilityModifiersOwnLineRule() },
        RuleProvider { PropertyAccessorOnNewLine() }
    )
}
