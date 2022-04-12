import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

tasks.named<ShadowJar>("shadowJar") {
    relocate("co.aikar.commands", "pl.teksusik.customskins.libs.acf")
    relocate("co.aikar.locales", "pl.teksusik.customskins.libs.locales")

    relocate("eu.okaeri", "pl.teksusik.customskins.libs.eu.okaeri")

    relocate("com.zaxxer", "pl.teksusik.customskins.libs.com.zaxxer")

    relocate("net.kyori", "pl.teksusik.customskins.libs.net.kyori")

    relocate("org.jsoup", "pl.teksusik.customskins.libs.org.jsoup")
}

dependencies {
    implementation(project(":customskins-nms"))
    implementation(project(":V1_12"))
    implementation(project(":V1_13"))
    implementation(project(":V1_14"))
    implementation(project(":V1_15"))
    implementation(project(":V1_16"))
    implementation(project(":V1_17"))
    implementation(project(":V1_18"))

    compileOnly("com.destroystokyo.paper:paper-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.12.2-R0.1-SNAPSHOT")

    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")

    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:3.4.2")

    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.mongodb:mongo-java-driver:3.12.10")

    implementation("net.kyori:adventure-text-minimessage:4.10.1")
    implementation("net.kyori:adventure-platform-bukkit:4.1.0")

    implementation("org.jsoup:jsoup:1.14.3")
}
