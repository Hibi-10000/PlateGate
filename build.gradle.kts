import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    kotlin("jvm") version "1.9.10"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

group = "com.github.hibi_10000.plugins"
version = "1.2.0"
description = "PlateGate plugin"

repositories {
    mavenCentral()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/groups/public/") }
}

dependencies {
    compileOnly("org.spigotmc", "spigot-api", "1.19.4-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin","kotlin-stdlib")
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<Jar> {
    // To avoid the duplicate handling strategy error
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

bukkit {
    author = "Hibi_10000"
    website = "https://github.com/Hibi-10000/PlateGate"
    main = "com.github.hibi_10000.plugins.plategate.PlateGate"
    apiVersion = "1.13"

    // Mark plugin for supporting Folia
    foliaSupported = true

    //prefix = "PG"
    defaultPermission = BukkitPluginDescription.Permission.Default.FALSE

    commands {
        register("plategate") {
            description = "PlateGate Main Command"
            aliases = listOf("pg")
            permission = "plategate.command"
            permissionMessage = "Unknown command. Type \"/help\" for help."
        }
    }

    permissions {
        register("plategate") {
            description = "PlateGate Admin Permission"
            default = BukkitPluginDescription.Permission.Default.OP
            children = listOf("plategate.admin")
            // You can also specify the values of the permissions
            //childrenMap = mapOf("plategate.admin" to true)
        }
        register("plategate.admin") {
            description = "PlateGate Admin Permission"
            default = BukkitPluginDescription.Permission.Default.OP
            children = listOf(
                "plategate.user",
                "plategate.command.jump",
            )
        }
        register("plategate.user") {
            description = "PlateGate User Permission"
            default = BukkitPluginDescription.Permission.Default.TRUE
            children = listOf(
                "plategate.command",
                "plategate.command.create",
                "plategate.command.delete",
                "plategate.command.help",
                "plategate.command.link",
                "plategate.command.list",
                "plategate.command.modify",
                "plategate.command.move",
                "plategate.info",
                "plategate.use",
            )
        }
        register("plategate.use") {
            description = "PlateGate Use Permission"
        }
        register("plategate.info") {
            description = "Get PlateGate Info Permission"
        }
        register("plategate.command") {
            description = "\"/plategate\" Command Use Permission"
        }
        register("plategate.command.create") {
            description = "\"/plategate create\" Command Use Permission"
            children = listOf("plategate.command")
        }
        register("plategate.command.delete") {
            description = "\"/plategate delete\" Command Use Permission"
            children = listOf("plategate.command")
        }
        register("plategate.command.help") {
            description = "\"/plategate help\" Command Use Permission"
            children = listOf("plategate.command")
        }
        register("plategate.command.jump") {
            description = "\"/plategate jump\" Command Use Permission"
            children = listOf("plategate.command")
        }
        register("plategate.command.link") {
            description = "\"/plategate link\" Command Use Permission"
            children = listOf("plategate.command")
        }
        register("plategate.command.list") {
            description = "\"/plategate list\" Command Use Permission"
            children = listOf("plategate.command")
        }
        register("plategate.command.modify") {
            description = "\"/plategate modify\" Command Use Permission"
            children = listOf("plategate.command")
        }
        register("plategate.command.move") {
            description = "\"/plategate move\" Command Use Permission"
            children = listOf("plategate.command")
        }
    }
}