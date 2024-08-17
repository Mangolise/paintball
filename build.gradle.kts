plugins {
    id("java")
}

group = "net.mangolise"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.Mangolise:mango-game-sdk:main-SNAPSHOT")
    implementation("net.minestom:minestom-snapshots:6c5cd6544e")
}
