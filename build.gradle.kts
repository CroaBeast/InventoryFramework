plugins {
    id("io.freefair.lombok") version "9.7.0" apply false
    id("com.gradleup.shadow") version "9.6.1" apply false
}

allprojects {
    group = "me.croabeast.inventory"
    version = "0.1.0"
}

configure(subprojects.filter { it.childProjects.isEmpty() }) {
    apply(plugin = "java-library")
    apply(plugin = "io.freefair.lombok")

    repositories {
        // BuildTools installs the Spigot server jars and their mappings here
        mavenLocal {
            content { includeGroup("org.spigotmc") }
        }
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://libraries.minecraft.net/")
    }

    dependencies {
        "compileOnly"("org.jetbrains:annotations:26.1.0")
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        // Server jars and Adventure 5 declare newer JVMs; they are only compiled against (compileOnly)
        disableAutoTargetJvm()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-Xlint:-options")
    }

    // Delombok uses sun.misc.Unsafe, which JDK 24+ warns about, so it runs on JDK 21
    val toolchains = extensions.getByType<JavaToolchainService>()
    tasks.withType<io.freefair.gradle.plugins.lombok.tasks.Delombok>().configureEach {
        launcher.set(toolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(21))
        })
    }

    tasks.withType<Javadoc>().configureEach {
        isFailOnError = false
        (options as StandardJavadocDocletOptions).apply {
            encoding = "UTF-8"
            addStringOption("Xdoclint:none", "-quiet")
        }
    }
}

// Spigot build each NMS module compiles against, installed by BuildTools. 1.17 - 1.21.x are compiled against Mojang
// names (BuildTools --remapped) and remapped back to Spigot names; older ones and 26.1+ need no remapping.
val nmsVersions = mapOf(
    "1_14" to "1.14.4-R0.1-SNAPSHOT",
    "1_15" to "1.15.2-R0.1-SNAPSHOT",
    "1_16_1" to "1.16.1-R0.1-SNAPSHOT",
    "1_16_2-3" to "1.16.3-R0.1-SNAPSHOT",
    "1_16_4-5" to "1.16.5-R0.1-SNAPSHOT",
    "1_17_1" to "1.17.1-R0.1-SNAPSHOT",
    "1_18_2" to "1.18.2-R0.1-SNAPSHOT",
    "1_19_4" to "1.19.4-R0.1-SNAPSHOT",
    "1_20_0" to "1.20-R0.1-SNAPSHOT",
    "1_20_1" to "1.20.1-R0.1-SNAPSHOT",
    "1_20_2" to "1.20.2-R0.1-SNAPSHOT",
    "1_20_3-4" to "1.20.4-R0.1-SNAPSHOT",
    "1_20_5" to "1.20.5-R0.1-SNAPSHOT",
    "1_20_6" to "1.20.6-R0.1-SNAPSHOT",
    "1_21_0" to "1.21-R0.1-SNAPSHOT",
    "1_21_1" to "1.21.1-R0.1-SNAPSHOT",
    "1_21_2-3" to "1.21.3-R0.1-SNAPSHOT",
    "1_21_4" to "1.21.4-R0.1-SNAPSHOT",
    "1_21_5" to "1.21.5-R0.1-SNAPSHOT",
    "1_21_6-8" to "1.21.8-R0.1-SNAPSHOT",
    "1_21_9-10" to "1.21.10-R0.1-SNAPSHOT",
    "1_21_11" to "1.21.11-R0.2-SNAPSHOT",
    "26_1" to "26.1.2-R0.1-SNAPSHOT",
)

project(":nms").subprojects.filter { it.name != "abstraction" }.forEach { module ->
    val mc = nmsVersions.getValue(module.name)
    val remapped = mc.startsWith("1.") && mc.substringBefore('-').split('.')[1].toInt() >= 17

    module.dependencies {
        "implementation"(project(":nms:abstraction"))
        "compileOnly"("org.spigotmc:spigot:$mc" + if (remapped) ":remapped-mojang" else "")
    }

    if (remapped) module.remapToSpigot(mc)
}

// Same two SpecialSource passes as Spigot's documented plugin setup: Mojang -> obfuscated -> Spigot names
fun Project.remapToSpigot(mc: String) {
    fun single(name: String, notation: String) = configurations.create(name) {
        isTransitive = false
        dependencies.add(project.dependencies.create(notation))
    }

    val specialSource = configurations.create("specialSource") {
        dependencies.add(project.dependencies.create("net.md-5:SpecialSource:1.11.6"))
    }
    val mojangServer = single("mojangServer", "org.spigotmc:spigot:$mc:remapped-mojang")
    val obfServer = single("obfServer", "org.spigotmc:spigot:$mc:remapped-obf")
    val mojangMaps = single("mojangMaps", "org.spigotmc:minecraft-server:$mc:maps-mojang@txt")
    val spigotMaps = single("spigotMaps", "org.spigotmc:minecraft-server:$mc:maps-spigot@csrg")

    val jar = tasks.named<Jar>("jar") { archiveClassifier.set("mojang") }
    val obfJar = layout.buildDirectory.file("remap/obf.jar")
    val spigotJar = layout.buildDirectory.file("libs/$name-$version.jar")

    val remapObf = tasks.register<JavaExec>("remapObf") {
        inputs.file(jar.flatMap { it.archiveFile })
        outputs.file(obfJar)
        classpath(specialSource, mojangServer)
        mainClass.set("net.md_5.specialsource.SpecialSource")
        doFirst {
            args(
                "--live", "--reverse",
                "-i", jar.get().archiveFile.get().asFile, "-o", obfJar.get().asFile,
                "-m", mojangMaps.singleFile,
            )
        }
    }

    val remapSpigot = tasks.register<JavaExec>("remapSpigot") {
        inputs.files(remapObf)
        outputs.file(spigotJar)
        classpath(specialSource, obfServer)
        mainClass.set("net.md_5.specialsource.SpecialSource")
        doFirst {
            args("--live", "-i", obfJar.get().asFile, "-o", spigotJar.get().asFile, "-m", spigotMaps.singleFile)
        }
    }

    tasks.named("assemble") { dependsOn(remapSpigot) }

    configurations.named("runtimeElements") {
        outgoing.artifacts.clear()
        outgoing.artifact(spigotJar) { builtBy(remapSpigot) }
    }
}
