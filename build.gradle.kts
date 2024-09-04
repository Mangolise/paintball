plugins {
    id("java")
    id("io.github.goooler.shadow") version("8.1.7")
    id("maven-publish")
}

var versionStr = System.getenv("GIT_COMMIT") ?: "dev"

group = "net.mangolise"
version = versionStr

repositories {
    mavenCentral()
    maven("https://maven.serble.net/snapshots/")
    maven("https://jitpack.io")
    maven("https://reposilite.worldseed.online/public")
}

dependencies {
    implementation("net.mangolise:mango-game-sdk:latest")
    implementation("net.minestom:minestom-snapshots:6c5cd6544e")
    implementation("dev.hollowcube:polar:1.11.1")
    implementation("com.github.EmortalMC:Rayfast:7975ac5e4c")
    implementation("net.worldseed.multipart:WorldSeedEntityEngine:11.0.1")
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

tasks.register("packageResourcePack", net.mangolise.gradle.PackageResourcePack::class.java)
tasks.processResources {
    dependsOn("packageResourcePack")
}

publishing {
    repositories {
        maven {
            name = "serbleMaven"
            url = uri("https://maven.serble.net/snapshots/")
            credentials {
                username = System.getenv("SERBLE_REPO_USERNAME")?:""
                password = System.getenv("SERBLE_REPO_PASSWORD")?:""
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        create<MavenPublication>("mavenGitCommit") {
            groupId = "net.mangolise"
            artifactId = "paintball"
            version = versionStr
            from(components["java"])
        }

        create<MavenPublication>("mavenLatest") {
            groupId = "net.mangolise"
            artifactId = "paintball"
            version = "latest"
            from(components["java"])
        }
    }
}
