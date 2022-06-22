import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.internal.hash.Hashing

plugins {
    `kotlin-dsl`
    id("com.github.johnrengelman.shadow") version "6.0.0" apply false
    id("org.gradle.kotlin-dsl.ktlint-convention") version "0.8.0"
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.0.0-rc-3"
}

group = "org.gradle.kotlin"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

repositories {
    gradlePluginPortal()
}

gradlePlugin {
    plugins {
        register("ktlint-convention") {
            id = "org.gradle.kotlin-dsl.ktlint-convention"
            implementationClass = "org.gradle.kotlin.dsl.experiments.plugins.GradleKotlinDslKtlintConventionPlugin"
            displayName = "Gradle Kotlin DSL ktlint convention plugin"
            description = "Gradle Kotlin DSL ktlint convention plugin"
        }
    }
}


pluginBundle {
    tags = listOf("Kotlin", "DSL")
    website = "https://github.com/gradle/kotlin-dsl-conventions"
    vcsUrl = "https://github.com/gradle/kotlin-dsl-conventions"
}

dependencies {

    api("org.jlleitschuh.gradle:ktlint-gradle:10.3.0")
    implementation(kotlin("stdlib-jdk8"))

    runtimeOnly(kotlin("gradle-plugin"))

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.12")
}

tasks {
    validatePlugins {
        enableStricterValidation.set(true)
        failOnWarning.set(true)
    }
    jar {
        from(sourceSets.main.map { it.allSource })
        manifest.attributes.apply {
            put("Implementation-Title", "Gradle Kotlin DSL (${project.name})")
            put("Implementation-Version", archiveVersion.get())
        }
    }
}

// default versions ---------------------------------------------------

val ktlintVersion = "0.45.2"

val basePackagePath = "org/gradle/kotlin/dsl/experiments/plugins"
val processResources by tasks.existing(ProcessResources::class)
val writeDefaultVersionsProperties by tasks.registering(WriteProperties::class) {
    outputFile = processResources.get().destinationDir.resolve("$basePackagePath/default-versions.properties")
    property("ktlint", ktlintVersion)
}
processResources {
    dependsOn(writeDefaultVersionsProperties)
}

// ktlint custom ruleset ----------------------------------------------

val ruleset by sourceSets.creating
val rulesetShaded by configurations.creating
val rulesetCompileOnly by configurations.getting {
    extendsFrom(rulesetShaded)
}

val generatedResourcesRulesetJarDir = file("$buildDir/generated-resources/ruleset/resources")
val rulesetJar by tasks.registering(ShadowJar::class) {
    archiveFileName.set("gradle-kotlin-dsl-ruleset.jar")
    destinationDirectory.set(generatedResourcesRulesetJarDir.resolve(basePackagePath))
    configurations = listOf(rulesetShaded)
    from(ruleset.output)
}
val rulesetChecksum by tasks.registering {
    dependsOn(rulesetJar)
    val rulesetChecksumFile = generatedResourcesRulesetJarDir
        .resolve(basePackagePath)
        .resolve("gradle-kotlin-dsl-ruleset.md5")
    val archivePath = rulesetJar.get().archiveFile.get().asFile
    inputs.file(archivePath)
    outputs.file(rulesetChecksumFile)
    doLast {
        rulesetChecksumFile.parentFile.mkdirs()
        rulesetChecksumFile.writeText(Hashing.md5().hashBytes(archivePath.readBytes()).toString())
    }
}

sourceSets.main {
    output.dir(generatedResourcesRulesetJarDir, "builtBy" to listOf(rulesetJar, rulesetChecksum))
}

dependencies {
    rulesetShaded("com.pinterest.ktlint:ktlint-ruleset-standard:$ktlintVersion") {
        isTransitive = false
    }
    rulesetCompileOnly("com.pinterest.ktlint:ktlint-core:$ktlintVersion")
}
