package org.gradle.kotlin.dsl.ktlint.ruleset

import com.pinterest.ktlint.core.RuleSet
import com.pinterest.ktlint.core.RuleSetProvider
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
class GradleKotlinDslRuleSetProvider : RuleSetProvider {

    override fun get(): RuleSet =
        RuleSet(
            "gradle-kotlin-dsl",

            // ktlint standard ruleset rules --------------------------
            // See https://github.com/pinterest/ktlint/blob/master/ktlint-ruleset-standard/src/main/kotlin/com/pinterest/ktlint/ruleset/standard/StandardRuleSetProvider.kt

            // kotlin-dsl: disabled in favor of CustomChainWrappingRule
            // ChainWrappingRule(),
            CommentSpacingRule(),
            FilenameRule(),
            FinalNewlineRule(),
            // disabled until it's clear how to reconcile difference in Intellij & Android Studio import layout
            // ImportOrderingRule(),
            IndentationRule(),
            MaxLineLengthRule(),
            ModifierOrderRule(),
            NoBlankLineBeforeRbraceRule(),
            // kotlin-dsl disabled in favor of BlankLinesRule
            // NoConsecutiveBlankLinesRule(),
            NoEmptyClassBodyRule(),
            NoLineBreakAfterElseRule(),
            NoLineBreakBeforeAssignmentRule(),
            NoMultipleSpacesRule(),
            NoSemicolonsRule(),
            NoTrailingSpacesRule(),
            NoUnitReturnRule(),
            NoUnusedImportsRule(),
            // kotlin-dsl: disabled in favor of CustomImportsRule
            // NoWildcardImportsRule(),
            ParameterListWrappingRule(),
            SpacingAroundColonRule(),
            SpacingAroundCommaRule(),
            SpacingAroundCurlyRule(),
            SpacingAroundDotRule(),
            SpacingAroundKeywordRule(),
            SpacingAroundOperatorsRule(),
            SpacingAroundParensRule(),
            SpacingAroundRangeOperatorRule(),
            StringTemplateRule(),

            // gradle-kotlin-dsl rules --------------------------------

            BlankLinesRule(),
            CustomChainWrappingRule(),
            CustomImportsRule(),
            VisibilityModifiersOwnLineRule(),
            PropertyAccessorOnNewLine()
        )
}
