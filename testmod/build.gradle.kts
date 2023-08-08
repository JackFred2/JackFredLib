@file:Suppress("UnstableApiUsage")

import java.net.URI

plugins {
    id("io.github.juuxel.loom-vineflower") version "1.11.0"
}

version = properties["mod_version"]!!
group = properties["maven_group"]!!

base {
    archivesName.set("${properties["archives_base_name"]}-testmod")
}

repositories {
    maven {
        name = "ParchmentMC"
        url = URI("https://maven.parchmentmc.org")
        content {
            includeGroup("org.parchmentmc.data")
        }
    }
    maven {
        name = "TerraformersMC"
        url = URI("https://maven.terraformersmc.com/releases/")
        content {
            includeGroup("com.terraformersmc")
            includeGroup("dev.emi")
        }
    }
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register("jackfredlib-testmod") {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }

    runConfigs.register("testClient") {
        client()
        ideConfigGenerated(true)
        name = "Run Test Client"
    }

    runConfigs {
        configureEach {
            val path = rootProject.buildscript.sourceFile?.parentFile?.resolve("log4j2.xml")
            path?.let { property("log4j2.configurationFile", path.path) }
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${properties["minecraft_version"]}")
    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${properties["parchment_version"]}@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${properties["loader_version"]}")

    setOf(
        "fabric-api-base",
        "fabric-command-api-v2"
    ).forEach { modImplementation(fabricApi.module(it, properties["fabric_api_version"]!!.toString())) }

    modImplementation("com.terraformersmc:modmenu:${properties["modmenu_version"]}")

    api(project(path=":api", configuration = "namedElements"))
    configurations["clientImplementation"](project(":api").dependencyProject.sourceSets["client"].output)
}

tasks.withType<ProcessResources>().configureEach {
    inputs.property("version", version)
    inputs.property("mod_name", properties["mod_name"]!!)

    filesMatching("fabric.mod.json") {
        expand(inputs.properties)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
}

tasks.test {
    useJUnitPlatform()
}