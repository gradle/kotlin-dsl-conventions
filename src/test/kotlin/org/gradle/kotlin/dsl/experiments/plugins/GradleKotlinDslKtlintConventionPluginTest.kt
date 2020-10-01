package org.gradle.kotlin.dsl.experiments.plugins

import org.gradle.kotlin.dsl.embeddedKotlinVersion

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome

import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo

import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import java.io.File


class GradleKotlinDslKtlintConventionPluginTest {

    @Rule
    @JvmField
    val tmpDir = TemporaryFolder()

    private
    val projectDir: File
        get() = tmpDir.root

    @Before
    fun setup() {
        withSettings()
        withBuildScript(
            """
            plugins {
                kotlin("jvm") version "$embeddedKotlinVersion"
                id("org.gradle.kotlin-dsl.ktlint-convention")
            }

            repositories {
                jcenter()
            }
            """
        )
    }

    @Test
    fun `ktlint dependencies include kotlin-reflect`() {

        assertThat(
            build("dependencies", "--configuration", "ktlint", "-s").output,
            containsString("org.jetbrains.kotlin:kotlin-reflect")
        )
    }

    @Test
    fun `ktlint check tasks are cacheable`() {

        withFile("gradle.properties", "org.gradle.caching=true")
        withSettings(
            """
            buildCache {
                local { isEnabled = false }
                remote<DirectoryBuildCache> {
                    directory = file("local-build-cache")
                    isEnabled = true
                    isPush = true
                }
            }

            """
        )

        withSource(
            """
            val foo = "bar"

            """
        )

        build("ktlintMainSourceSetCheck").apply {

            assertThat(outcomeOf(":ktlintMainSourceSetCheck"), equalTo(TaskOutcome.SUCCESS))
        }

        build("ktlintMainSourceSetCheck").apply {

            assertThat(outcomeOf(":ktlintMainSourceSetCheck"), equalTo(TaskOutcome.UP_TO_DATE))
        }

        build("clean")

        build("ktlintMainSourceSetCheck").apply {

            assertThat(outcomeOf(":ktlintMainSourceSetCheck"), equalTo(TaskOutcome.FROM_CACHE))
        }
    }

    @Test
    fun `visibility modifiers on their own single line`() {

        withSource(
            """

            private val bar = false


            class Bazar(private val name: String) {

                private lateinit
                var description: String

                private inline
                fun something() = Unit
            }

            """
        )

        buildAndFail("ktlintMainSourceSetCheck")

        assertKtlintErrors(3)
        assertKtLintError("Visibility modifiers must be on their own single line", 2, 1)
        assertKtLintError("Visibility modifiers must be on their own single line", 7, 5)
        assertKtLintError("Visibility modifiers must be on their own single line", 10, 5)

        withSource(
            """

            private
            val bar = false


            class Bazar(private val name: String) {

                private
                lateinit var description: String

                private
                inline fun something() = Unit
            }

            """
        )

        build("ktlintMainSourceSetCheck")
    }

    @Test
    fun `allowed wildcard imports`() {

        withSource(
            """

            import java.util.*
            import org.w3c.dom.*

            import org.gradle.kotlin.dsl.*

            """
        )

        buildAndFail("ktlintMainSourceSetCheck")

        assertKtlintErrors(1)
        assertKtLintError("Wildcard import not allowed (org.w3c.dom.*)", 3, 1)
    }

    @Test
    fun `blank lines`() {

        withSource(
            """
            package some

            import org.gradle.kotlin.dsl.*

            val foo = "bar"

            interface Foo



            object Bar


            data class Some(val name: String)

            """
        )

        buildAndFail("ktlintMainSourceSetCheck")

        assertKtlintErrors(3)
        assertKtLintError("Top level elements must be separated by two blank lines", 3, 31)
        assertKtLintError("Top level elements must be separated by two blank lines", 5, 16)
        assertKtLintError("Needless blank line(s)", 9, 1)

        withSource(
            """
            /*
             * Copyright 2016 the original author or authors.
             */

            // Random words
            @file:JvmName("Something")

            /**
             * Package kdoc.
             */
            package some

            import org.gradle.kotlin.dsl.*


            /*
             * Some file documentation.
             */


            val foo = "bar"


            /**
             * Interface kdoc.
             */
            interface Foo


            object Bar


            data class Some(val name: String)

            """
        )

        build("ktlintMainSourceSetCheck")
    }

    @Test
    fun `new lines starting with ANDAND are allowed`() {

        withSource(
            """

            val foo = "bar".isNotEmpty()
                && "bazar".isNotEmpty() // either

            """
        )

        build("ktlintMainSourceSetCheck")
    }

    @Test
    fun `property accessors on new line`() {

        withSource(
            """

            val foo get() = "bar"


            val bar: String get() { return "bar" }

            """
        )

        buildAndFail("ktlintMainSourceSetCheck")

        assertKtlintErrors(2)
        assertKtLintError("Property accessor must be on a new line", 2, 9)
        assertKtLintError("Property accessor must be on a new line", 5, 17)

        withSource(
            """

            val foo
                get() = "bar"


            val bar: String
                get() { return "bar" }

            """
        )

        build("ktlintMainSourceSetCheck")
    }

    private
    fun build(vararg arguments: String) =
        gradleRunnerFor(*arguments).build()

    private
    fun buildAndFail(vararg arguments: String) =
        gradleRunnerFor(*arguments).buildAndFail()

    private
    fun gradleRunnerFor(vararg arguments: String) =
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments(*arguments)

    private
    fun withBuildScript(text: String) =
        withFile("build.gradle.kts", text)

    private
    fun withSettings(text: String = "") =
        withFile("settings.gradle.kts", text)

    private
    fun withSource(text: String) =
        withFile("src/main/kotlin/source.kt", text)

    private
    fun withFile(path: String, text: String) =
        projectDir.resolve(path).apply {
            parentFile.mkdirs()
            writeText(text.trimIndent())
        }

    private
    fun BuildResult.outcomeOf(taskPath: String): TaskOutcome? =
        task(taskPath)?.outcome

    private
    val ktlintReportFile: File by lazy { projectDir.resolve("build/reports/ktlint/ktlintMainSourceSetCheck/ktlintMainSourceSetCheck.txt") }

    private
    fun assertKtlintErrors(count: Int) =
        assertThat(
            "ktlint error count in\n${ktlintReportFile.readText()}",
            ktlintReportFile.readLines().filter { it.contains("source.kt") }.count(),
            equalTo(count)
        )

    private
    fun assertKtLintError(error: String, line: Int, column: Int) =
        assertThat(
            ktlintReportFile.readText().withoutAnsiColorCodes(),
            containsString("source.kt:$line:$column: $error")
        )

    private
    fun String.withoutAnsiColorCodes() =
        replace(Regex("\u001B\\[[;\\d]*m"), "")
}
