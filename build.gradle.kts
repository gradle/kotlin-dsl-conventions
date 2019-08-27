import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.internal.hash.Hashing

plugins {

    `build-scan`

    `kotlin-dsl`
    id("com.github.johnrengelman.shadow") version "2.0.4" apply false

    id("org.gradle.kotlin-dsl.ktlint-convention") version "0.3.0"

    `maven-publish`
    id("com.gradle.plugin-publish") version "0.10.0"
}

group = "org.gradle.kotlin"

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    gradlePluginPortal()
}

gradlePlugin {
    plugins {
        register("ktlint-convention") {
            id = "org.gradle.kotlin-dsl.ktlint-convention"
            implementationClass = "org.gradle.kotlin.dsl.experiments.plugins.GradleKotlinDslKtlintConventionPlugin"
        }
    }
}


pluginBundle {
    tags = listOf("Kotlin", "DSL")
    website = "https://github.com/gradle/kotlin-dsl-conventions"
    vcsUrl = "https://github.com/gradle/kotlin-dsl-conventions"
    mavenCoordinates {
        group = project.group.toString()
        artifactId = base.archivesBaseName
    }
    plugins {
        named("ktlint-convention") {
            displayName = "Gradle Kotlin DSL ktlint convention plugin"
            description = "Gradle Kotlin DSL ktlint convention plugin"
        }
    }
}

dependencies {

    compile("org.jlleitschuh.gradle:ktlint-gradle:7.1.0")
    implementation(kotlin("stdlib-jdk8"))

    runtimeOnly(kotlin("gradle-plugin"))

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.12")
}

tasks {
    validateTaskProperties {
        failOnWarning = true
    }
    jar {
        from(sourceSets.main.map { it.allSource })
        manifest.attributes.apply {
            put("Implementation-Title", "Gradle Kotlin DSL (${project.name})")
            put("Implementation-Version", archiveVersion.get())
        }
    }
}

if (System.getenv("CI") == "true") {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlways()
        tag("CI")
    }
}

// default versions ---------------------------------------------------

val ktlintVersion = "0.30.0"

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
    rulesetShaded("com.github.shyiko.ktlint:ktlint-ruleset-standard:$ktlintVersion") {
        isTransitive = false
    }
    rulesetCompileOnly("com.github.shyiko.ktlint:ktlint-core:$ktlintVersion")
    rulesetCompileOnly(kotlin("reflect"))
}
