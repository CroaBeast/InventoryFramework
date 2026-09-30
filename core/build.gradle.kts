plugins {
    id("com.gradleup.shadow")
    `maven-publish`
}

val adventureVersion = providers.gradleProperty("adventureVersion").get()

base {
    archivesName.set("InventoryFramework")
}

java {
    withSourcesJar()
    withJavadocJar()
}

configurations.testImplementation {
    extendsFrom(configurations.compileOnly.get())
}

dependencies {
    implementation(project(":nms:abstraction"))
    rootProject.project(":nms").subprojects.filter { it.name != "abstraction" }.forEach { implementation(project(it.path)) }

    compileOnly("net.kyori:adventure-api:$adventureVersion")
    // Provided, but commons-lang3 is not accessible on the server
    compileOnly("org.spigotmc:spigot-api:1.20.3-R0.1-SNAPSHOT") {
        exclude("org.apache.commons", "commons-lang3")
    }
    compileOnly("com.mojang:authlib:1.5.26") {
        exclude("org.apache.commons", "commons-lang3")
    }

    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveClassifier.set("plain")
}

tasks.shadowJar {
    archiveClassifier.set("")
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "framework"
            from(components["shadow"])
            artifact(tasks.named("sourcesJar"))
            artifact(tasks.named("javadocJar"))
        }
    }
}
