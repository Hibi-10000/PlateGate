/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "1.9.24"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

group = "com.github.hibi_10000.plugins"
version = "1.2.0-SNAPSHOT"
description = "PlateGate plugin"
kotlin.compilerOptions.jvmTarget.set(JvmTarget.JVM_17)

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("org.spigotmc", "spigot-api", "1.20.2-R0.1-SNAPSHOT")
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from("LICENSE.txt", "README.md")
    dependsOn(configurations.runtimeClasspath)
    from(configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) })
}

bukkit {
    author = "Hibi_10000"
    website = "https://github.com/Hibi-10000/PlateGate"
    main = "com.github.hibi_10000.plugins.plategate.Main"
    apiVersion = "1.13"
    foliaSupported = true
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
                "plategate.command.help",
                "plategate.command.link",
                "plategate.command.list",
                "plategate.command.move",
                "plategate.command.remove",
                "plategate.command.rename",
                "plategate.command.unlink",
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
        register("plategate.command.move") {
            description = "\"/plategate move\" Command Use Permission"
            children = listOf("plategate.command")
        }
        register("plategate.command.remove") {
            description = "\"/plategate remove\" Command Use Permission"
            children = listOf("plategate.command")
        }
        register("plategate.command.rename") {
            description = "\"/plategate rename\" Command Use Permission"
            children = listOf("plategate.command")
        }
        register("plategate.command.unlink") {
            description = "\"/plategate unlink\" Command Use Permission"
            children = listOf("plategate.command")
        }
    }
}
