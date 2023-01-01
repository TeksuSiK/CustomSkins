import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

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

dependencies {
    implementation(project(":nms:api"))
    implementation(project(":nms:V1_12"))
    implementation(project(":nms:V1_13"))
    implementation(project(":nms:V1_14"))
    implementation(project(":nms:V1_15"))
    implementation(project(":nms:V1_16"))
    implementation(project(":nms:V1_17"))
    implementation(project(":nms:V1_18"))
    implementation(project(":nms:V1_19"))

    compileOnly("com.destroystokyo.paper:paper-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.12.2-R0.1-SNAPSHOT")

    implementation("org.mineskin:java-client:1.2.4-SNAPSHOT")

    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")

    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:4.0.8")

    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.mongodb:mongodb-driver-sync:4.8.1")
    implementation("org.postgresql:postgresql:42.5.1")

    implementation("net.kyori:adventure-text-minimessage:4.12.0")
    implementation("net.kyori:adventure-platform-bukkit:4.2.0")

    implementation("org.jsoup:jsoup:1.15.3")
    implementation("commons-io:commons-io:2.11.0")
    implementation("com.google.inject:guice:5.1.0")
    implementation("com.google.code.gson:gson:2.10")

    implementation("org.bstats:bstats-bukkit:3.0.0")
}
