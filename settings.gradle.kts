plugins {
    `gradle-enterprise`
}

rootProject.name = "gradle-kotlin-dsl-conventions"

if (System.getenv("CI") == "true") {
    gradleEnterprise {
        buildScan {
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
            publishAlways()
            tag("CI")
        }
    }
}
