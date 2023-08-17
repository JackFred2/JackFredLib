import net.fabricmc.loom.api.LoomGradleExtensionAPI
import red.jackf.UpdateDependenciesTask
import java.net.URI

plugins {
    id("fabric-loom") version "1.3-SNAPSHOT" apply false
    id("io.github.juuxel.loom-vineflower") version "1.11.0" apply false
}

subprojects {
    apply(plugin="fabric-loom")
    apply(plugin="io.github.juuxel.loom-vineflower")

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

    val loom = project.extensions.getByType(LoomGradleExtensionAPI::class)



    dependencies {
        add("minecraft", "com.mojang:minecraft:${properties["minecraft_version"]}")
        @Suppress("UnstableApiUsage")
        add("mappings", loom.layered {
            officialMojangMappings()
            parchment("org.parchmentmc.data:parchment-${properties["parchment_version"]}@zip")
        })
        add("modImplementation", "net.fabricmc:fabric-loader:${properties["loader_version"]}")
    }
}

tasks.register<UpdateDependenciesTask>("updateModDependencies") {
    mcVersion.set(properties["minecraft_version"]!!.toString())
    loader.set("fabric")
}