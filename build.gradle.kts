import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("com.github.johnrengelman.shadow") version("7.1.2")
    kotlin("plugin.serialization") version "1.7.20"
    application
}

group = "us.xylight.neptune"
version = "1.2.2-SNAPSHOT"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://jitpack.io/")
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-alpha.22")
    implementation("io.github.cdimascio:dotenv-kotlin:6.3.1")
    implementation("org.litote.kmongo:kmongo-coroutine:4.7.2")
    implementation("org.ocpsoft.prettytime:prettytime:5.0.6.Final")
    implementation("com.github.minndevelopment:jda-ktx:0.9.6-alpha.22")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    implementation(files("lib/lib.jar"))

    implementation("com.charleskorn.kaml:kaml:0.49.0")
}

tasks.jar {
    doFirst {
        manifest {
            attributes(
                "Main-Class" to "us.xylight.neptune.MainKt"
            )
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

sourceSets {
    main {
        resources {
            srcDir("resources")
        }
    }
}

application {
    mainClass.set("us.xylight.neptune.MainKt")

}
