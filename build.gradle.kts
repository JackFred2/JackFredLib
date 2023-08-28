import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask
import red.jackf.UpdateDependenciesTask
import java.net.URI

plugins {
    id("fabric-loom") version "1.2.7" apply false
    id("io.github.juuxel.loom-vineflower") version "1.11.0" apply false
}

fun Project.getSourceSet(name: String) = this.extensions.getByType(SourceSetContainer::class)[name]

// Adapted from Fabric API
extra["moduleDependencies"] = fun(project: Project, depNames: List<String>) {
    val deps = depNames.map { project.dependencies.project(path = ":$it", configuration = "namedElements") }
    val clientOutputs =
        depNames.map { project(":$it").getSourceSet("client").output }

    project.dependencies {
        deps.forEach {
            add("api", it)
        }

        clientOutputs.forEach {
            add("clientImplementation", it)
        }
    }

    // TODO: publishing
}

version = properties["mod_version"]!!

subprojects {
    version = properties["${this.name}_version"] ?: properties["mod_version"]!!
}

allprojects {
    group = "red.jackf.jackfredlib"

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
        repositories {
            maven {
                name = "JitPack"
                url = URI("https://jitpack.io")
                content {
                    includeGroupByRegex("com.github.llamalad7.*")
                }
            }
        }
    }

    val loom = project.extensions.getByType(LoomGradleExtensionAPI::class)

    loom.splitEnvironmentSourceSets()

    loom.runConfigs {

    }

    loom.mods {
        create(project.name) {
            sourceSet(project.getSourceSet("main"))
            sourceSet(project.getSourceSet("client"))
        }
    }

    dependencies {
        add("minecraft", "com.mojang:minecraft:${properties["minecraft_version"]}")
        @Suppress("UnstableApiUsage")
        add("mappings", loom.layered {
            officialMojangMappings()
            parchment("org.parchmentmc.data:parchment-${properties["parchment_version"]}@zip")
        })
        add("modImplementation", "net.fabricmc:fabric-loader:${properties["loader_version"]}")
        add("modImplementation", "net.fabricmc.fabric-api:fabric-api:${properties["fabric-api_version"]}")

        add("include",
            add("implementation",
                add("annotationProcessor", "io.github.llamalad7:mixinextras-fabric:${properties["mixin_extras_version"]}")!!)!!)
    }

    tasks.withType<ProcessResources>().configureEach {
        inputs.property("version", version)

        filesMatching("fabric.mod.json") {
            expand(inputs.properties)
        }
    }
}

dependencies {
    afterEvaluate {
        subprojects.forEach {
            if (it.name == "jackfredlib-testmod") return@forEach

            add("api", project(path=it.path, configuration = "namedElements"))
            add("clientImplementation", it.getSourceSet("client").output)
        }
    }
}

subprojects.forEach {
    if (it.name == "jackfredlib-testmod") return@forEach

    tasks.getByName<RemapJarTask>("remapJar").dependsOn("${it.path}:remapJar")
}

tasks.getByName<RemapJarTask>("remapJar") {
    afterEvaluate {
        subprojects.forEach {
            if (it.name == "jackfredlib-testmod") return@forEach

            nestedJars.from(it.tasks.getByName("remapJar"))
        }
    }
}

tasks.register<UpdateDependenciesTask>("updateModDependencies") {
    mcVersion.set(properties["minecraft_version"]!!.toString())
    loader.set("fabric")
}