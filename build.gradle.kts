plugins {
    kotlin("jvm") version "2.2.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.edu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.6-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib-jdk8"))

    compileOnly("com.github.MilkBowl:Vault:1.7.3") {
        exclude(group = "org.bukkit", module = "bukkit")
        exclude(group = "org.bukkit", module = "craftbukkit")
    }
}

tasks.shadowJar {
    archiveBaseName.set("PaperEduPlugin")
    archiveClassifier.set("")
    archiveVersion.set("")

    destinationDirectory.set(file("/Users/kmj5004/Desktop/MyPaperServer/plugins/"))

    dependencies {
        exclude(dependency("io.papermc.paper:paper-api"))
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

kotlin {
    jvmToolchain(21)
}