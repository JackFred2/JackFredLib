@file:Suppress("UnstableApiUsage")

import java.net.URI

base {
    archivesName.set("${properties["archives_base_name"]}-testmod")
}

loom {
    runConfigs.forEach {
        it.isIdeConfigGenerated = false
    }

    runConfigs.register("testClient") {
        client()
        ideConfigGenerated(true)
        name("Test Mod Client")
    }

    log4jConfigs.from(rootProject.file("log4j2.xml"))
}

repositories {
    maven {
        name = "TerraformersMC"
        url = URI("https://maven.terraformersmc.com/releases/")
        content {
            includeGroup("com.terraformersmc")
            includeGroup("dev.emi")
        }
    }
}

dependencies {
    add("api", project(path=rootProject.path, configuration = "namedElements"))
    rootProject.allprojects.forEach {
        if (it.name == "jackfredlib-testmod") return@forEach
        add("clientImplementation", it.extensions.getByType(SourceSetContainer::class)["client"].output)
    }

    modRuntimeOnly("com.terraformersmc:modmenu:${properties["modmenu_version"]}")
    modRuntimeOnly("com.github.llamalad7.mixinextras:mixinextras-fabric:${properties["mixin_extras_version"]}")
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
}

tasks.test {
    useJUnitPlatform()
}