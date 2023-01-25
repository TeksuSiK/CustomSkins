import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    idea
    java
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-paper") version "2.0.1"
}

group = "pl.teksusik"
version = "1.5-SNAPSHOT"

apply(plugin = "java")
apply(plugin = "java-library")
apply(plugin = "maven-publish")
apply(plugin = "com.github.johnrengelman.shadow")

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
    mavenLocal()
    maven(url = "https://papermc.io/repo/repository/maven-public/")
    maven(url = "https://repo.aikar.co/content/groups/aikar/")
    maven(url = "https://storehouse.okaeri.eu/repository/maven-public/")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    maven(url = "https://repo.inventivetalent.org/repository/public/")
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.12.2-R0.1-SNAPSHOT")

    implementation("org.mineskin:java-client:1.2.4-SNAPSHOT")

    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")

    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:5.0.0-beta.2")
    implementation("eu.okaeri:okaeri-i18n-configs:5.0.1-beta.4")
    implementation("eu.okaeri:okaeri-i18n-minecraft-adventure:5.0.1-beta.4")

    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.mongodb:mongodb-driver-sync:4.8.1")
    implementation("org.postgresql:postgresql:42.5.1")

    implementation("net.kyori:adventure-text-minimessage:4.12.0")
    implementation("net.kyori:adventure-platform-bukkit:4.2.0")

    implementation("commons-io:commons-io:2.11.0")
    implementation("com.google.inject:guice:5.1.0")
    implementation("com.google.code.gson:gson:2.10")

    implementation("org.bstats:bstats-bukkit:3.0.0")
}

idea {
    project {
        jdkName = "17"
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

tasks.named<ShadowJar>("shadowJar") {
    relocate("org.mineskin", "pl.teksusik.customskins.libs.org.mineskin")

    relocate("co.aikar.commands", "pl.teksusik.customskins.libs.acf")
    relocate("co.aikar.locales", "pl.teksusik.customskins.libs.locales")

    relocate("eu.okaeri", "pl.teksusik.customskins.libs.eu.okaeri")

    relocate("com.zaxxer", "pl.teksusik.customskins.libs.com.zaxxer")
    relocate("com.mongodb", "pl.teksusik.customskins.libs.com.mongodb")

    relocate("net.kyori", "pl.teksusik.customskins.libs.net.kyori")

    relocate("org.jsoup", "pl.teksusik.customskins.libs.org.jsoup")
    relocate("org.apache", "pl.teksusik.customskins.libs.org.apache")
    relocate("com.google", "pl.teksusik.customskins.libs.com.google")

    relocate("org.bstats", "pl.teksusik.customskins.libs.org.bstats")
}

tasks {
    runServer {
        minecraftVersion("1.19.3")
    }
}

tasks.processResources {
    expand(
            "customSkinsVersion" to version,
    )
}

publishing {
    repositories {
        maven {
            name = "teksusik"
            url = uri("https://repo.teksusik.pl/${if (version.toString().endsWith("-SNAPSHOT")) "snapshots" else "releases"}")
            credentials {
                username = System.getenv("MAVEN_NAME")
                password = System.getenv("MAVEN_TOKEN")
            }
        }
    }

    publications {
        create<MavenPublication>("library") {
            from(components.getByName("java"))

            // https://github.com/FunnyGuilds/FunnyGuilds/blob/master/build.gradle
            // Add external repositories to published artifacts
            // ~ btw: pls don't touch this
            pom.withXml {
                val repositories = asNode().appendNode("repositories")
                project.repositories.findAll(closureOf<Any> {
                    if (this is MavenArtifactRepository && this.url.toString().startsWith("https")) {
                        val repository = repositories.appendNode("repository")
                        repository.appendNode("id", this.url.toString().replace("https://", "").replace("/", "-").replace(".", "-").trim())
                        repository.appendNode("url", this.url.toString().trim())
                    }
                })
            }
        }
    }
}

