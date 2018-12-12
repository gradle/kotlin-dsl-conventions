import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.internal.hash.Hashing

plugins {

    `kotlin-dsl`
    id("com.github.johnrengelman.shadow") version "2.0.4" apply false

    id("org.gradle.kotlin.ktlint-convention") version "0.1.15"

    signing
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.10.0"
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    gradlePluginPortal()
}

pluginBundle {
    tags = listOf("Kotlin", "DSL")
    website = "https://github.com/gradle/kotlin-dsl-conventions"
    vcsUrl = "https://github.com/gradle/kotlin-dsl-conventions"
}

gradlePlugin {
    plugins {
        register("ktlint-convention") {
            id = "org.gradle.kotlin-dsl.ktlint-convention"
            implementationClass = "org.gradle.kotlin.dsl.experiments.plugins.GradleKotlinDslKtlintConventionPlugin"
        }
    }
}

dependencies {

    implementation("org.jlleitschuh.gradle:ktlint-gradle:6.3.1")
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
            put("Implementation-Version", this@jar.version)
        }
    }
}

signing {
    useGpgCmd()
    sign(configurations.archives.get())
    setRequired(Callable {
        gradle.taskGraph.hasTask("publishPlugins")
    })
}

// default versions ---------------------------------------------------

val ktlintVersion = "0.29.0"

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
    archiveName = "gradle-kotlin-dsl-ruleset.jar"
    destinationDir = generatedResourcesRulesetJarDir.resolve(basePackagePath)
    configurations = listOf(rulesetShaded)
    from(ruleset.output)
}
val rulesetChecksum by tasks.registering {
    dependsOn(rulesetJar)
    val rulesetChecksumFile = generatedResourcesRulesetJarDir
        .resolve(basePackagePath)
        .resolve("gradle-kotlin-dsl-ruleset.md5")
    val archivePath = rulesetJar.get().archivePath
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
