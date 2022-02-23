plugins {
    idea
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

allprojects {
    group = "pl.teksusik"
    version = "1.2-RELEASE"
}

subprojects {
    apply(plugin = "java")
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
    }
}

idea {
    project {
        jdkName = "17"
    }
}
