plugins {
    id("java")
}

group = "net.mangolise"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.serble.net/snapshots/")
    maven("https://reposilite.worldseed.online/public")
}

dependencies {
    implementation("net.mangolise:mango-game-sdk:latest")
    implementation("net.minestom:minestom-snapshots:6c5cd6544e")
    implementation("dev.hollowcube:polar:1.11.1")
    implementation("net.worldseed.multipart:WorldSeedEntityEngine:11.0.1")
    implementation(gradleApi())
    implementation("org.zeroturnaround:zt-zip:1.17")
}
