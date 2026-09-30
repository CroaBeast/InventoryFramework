rootProject.name = "InventoryFramework"

include(
    "core",
    "adventure",
    "nms:abstraction",
)

// Every other folder under nms/ is one NMS module; its Spigot version lives in build.gradle.kts
file("nms").listFiles { f -> f.isDirectory && f.name[0].isDigit() }!!
    .forEach { include("nms:${it.name}") }
