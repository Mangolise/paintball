plugins {
    id("java")
    id("io.github.goooler.shadow") version("8.1.7")

}

group = "net.mangolise"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.serble.net/snapshots/")
    maven("https://jitpack.io")
}

dependencies {
    implementation("net.mangolise:mango-game-sdk:latest")
    implementation("net.minestom:minestom-snapshots:6c5cd6544e")
    implementation("dev.hollowcube:polar:1.11.1")
    implementation("com.github.EmortalMC:Rayfast:7975ac5e4c")
}

java {
    withSourcesJar()
}

tasks.withType<Jar> {
    manifest {
        // Change this to your main class
        attributes["Main-Class"] = "net.mangolise.paintball.Test"
    }
}

tasks.register("packageWorlds", net.mangolise.gamesdk.gradle.PackageWorldTask::class.java)
tasks.processResources {
    dependsOn("packageWorlds")
}
