package org.gradle.kotlin.dsl.experiments.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

import org.gradle.cache.internal.GeneratedGradleJarCache

import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

import org.gradle.kotlin.dsl.*
import org.gradle.kotlin.dsl.support.serviceOf
import java.util.concurrent.Callable


private
val rulesetChecksum by lazy {
    GradleKotlinDslKtlintConventionPlugin::class.java.getResource("gradle-kotlin-dsl-ruleset.md5").readText()
}


private
val rulesetJar by lazy {
    GradleKotlinDslKtlintConventionPlugin::class.java.getResource("gradle-kotlin-dsl-ruleset.jar")
}


class GradleKotlinDslKtlintConventionPlugin : Plugin<Project> {

    override fun apply(project: Project): Unit = project.run {

        apply(plugin = "org.jlleitschuh.gradle.ktlint")

        configure<KtlintExtension> {
            version.set(DefaultVersions.ktlint)
            reporters {
                reporter(ReporterType.PLAIN)
            }
        }

        val ktlint = configurations.maybeCreate("ktlint").apply {
            exclude(module = "ktlint-ruleset-standard")
        }

        dependencies {
            ktlint.name(files(gradleKotlinDslKtlintRulesetJar()))
            ktlint.name(kotlin("reflect"))
        }
    }

    private
    fun Project.gradleKotlinDslKtlintRulesetJar() = Callable {
        serviceOf<GeneratedGradleJarCache>().get("ktlint-convention-ruleset-$rulesetChecksum") {
            outputStream().use { it.write(rulesetJar.readBytes()) }
        }
    }
}
