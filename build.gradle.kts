import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    application
}

group = "us.xylight.surveyer"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-alpha.22")
    implementation("io.github.cdimascio:dotenv-kotlin:6.3.1")
    implementation("org.litote.kmongo:kmongo:4.7.2")
   implementation("org.ocpsoft.prettytime:prettytime:5.0.6.Final")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("us.xylight.surveyer.MainKt")
}