package org.gradle.kotlin.dsl.ktlint.ruleset

import com.pinterest.ktlint.core.RuleProvider
import com.pinterest.ktlint.core.RuleSet
import com.pinterest.ktlint.core.RuleSetProvider
import com.pinterest.ktlint.core.RuleSetProviderV2
import com.pinterest.ktlint.ruleset.standard.CommentSpacingRule
import com.pinterest.ktlint.ruleset.standard.FilenameRule
import com.pinterest.ktlint.ruleset.standard.FinalNewlineRule
import com.pinterest.ktlint.ruleset.standard.IndentationRule
import com.pinterest.ktlint.ruleset.standard.MaxLineLengthRule
import com.pinterest.ktlint.ruleset.standard.ModifierOrderRule
import com.pinterest.ktlint.ruleset.standard.NoBlankLineBeforeRbraceRule
import com.pinterest.ktlint.ruleset.standard.NoEmptyClassBodyRule
import com.pinterest.ktlint.ruleset.standard.NoLineBreakAfterElseRule
import com.pinterest.ktlint.ruleset.standard.NoLineBreakBeforeAssignmentRule
import com.pinterest.ktlint.ruleset.standard.NoMultipleSpacesRule
import com.pinterest.ktlint.ruleset.standard.NoSemicolonsRule
import com.pinterest.ktlint.ruleset.standard.NoTrailingSpacesRule
import com.pinterest.ktlint.ruleset.standard.NoUnitReturnRule
import com.pinterest.ktlint.ruleset.standard.NoUnusedImportsRule
import com.pinterest.ktlint.ruleset.standard.ParameterListWrappingRule
import com.pinterest.ktlint.ruleset.standard.SpacingAroundColonRule
import com.pinterest.ktlint.ruleset.standard.SpacingAroundCommaRule
import com.pinterest.ktlint.ruleset.standard.SpacingAroundCurlyRule
import com.pinterest.ktlint.ruleset.standard.SpacingAroundDotRule
import com.pinterest.ktlint.ruleset.standard.SpacingAroundKeywordRule
import com.pinterest.ktlint.ruleset.standard.SpacingAroundOperatorsRule
import com.pinterest.ktlint.ruleset.standard.SpacingAroundParensRule
import com.pinterest.ktlint.ruleset.standard.SpacingAroundRangeOperatorRule
import com.pinterest.ktlint.ruleset.standard.StringTemplateRule


/**
 * Gradle Kotlin DSL ktlint RuleSetProvider.
 *
 * Reuse ktlint-standard-ruleset rules and add custom ones.
 */
class GradleKotlinDslRuleSetProvider : RuleSetProvider, RuleSetProviderV2(
    "gradle-kotlin-dsl",
    About(
        maintainer = "Gradle",
        description = "Gradle Kotlin DSL conventional plugins",
        license = "Apache-2.0",
        repositoryUrl = "https://github.com/gradle/kotlin-dsl-conventions",
        issueTrackerUrl = "https://github.com/gradle/kotlin-dsl-conventions/issues"
    )) {

    @Suppress("DEPRECATION")
    override fun get(): RuleSet =
        RuleSet(
            "gradle-kotlin-dsl",

             *(getRuleProviders().map { it.createNewRuleInstance() }.toTypedArray())
        )

    override fun getRuleProviders(): Set<RuleProvider> =
        setOf(
            // ktlint standard ruleset rules --------------------------
            // See https://github.com/pinterest/ktlint/blob/master/ktlint-ruleset-standard/src/main/kotlin/com/pinterest/ktlint/ruleset/standard/StandardRuleSetProvider.kt

            // kotlin-dsl: disabled in favor of CustomChainWrappingRule
            // ChainWrappingRule(),
            RuleProvider { CommentSpacingRule() },
            // disabled, since it requires all files to be PascalCase
            // RuleProvider { FilenameRule() },
            RuleProvider { FinalNewlineRule() },
            // disabled until it's clear how to reconcile difference in Intellij & Android Studio import layout
            // RuleProvider { ImportOrderingRule() },
            RuleProvider { IndentationRule() },
            RuleProvider { MaxLineLengthRule() },
            RuleProvider { ModifierOrderRule() },
            RuleProvider { NoBlankLineBeforeRbraceRule() },
            // kotlin-dsl disabled in favor of BlankLinesRule
            // RuleProvider { NoConsecutiveBlankLinesRule() },
            RuleProvider { NoEmptyClassBodyRule() },
            RuleProvider { NoLineBreakAfterElseRule() },
            RuleProvider { NoLineBreakBeforeAssignmentRule() },
            RuleProvider { NoMultipleSpacesRule() },
            RuleProvider { NoSemicolonsRule() },
            RuleProvider { NoTrailingSpacesRule() },
            RuleProvider { NoUnitReturnRule() },
            RuleProvider { NoUnusedImportsRule() },
            // kotlin-dsl: disabled in favor of CustomImportsRule
            // RuleProvider { NoWildcardImportsRule() },
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
