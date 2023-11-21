@file:Suppress("UnstableApiUsage")

import java.net.URI

base {
    archivesName.set("${properties["archives_base_name"]}-testmod")
}

loom {
    log4jConfigs.from(rootProject.file("log4j2.xml"))

    runConfigs.forEach {
        it.isIdeConfigGenerated = false
    }

    runConfigs.register("testClient") {
        client()
        ideConfigGenerated(true)
        name("Test Mod Client")
    }
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
    maven {
        name = "Modrinth"
        url = URI("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }

    mavenCentral()
}

dependencies {
    add("api", project(path=rootProject.path, configuration = "namedElements"))
    rootProject.allprojects.forEach {
        if (it.name == "jackfredlib-testmod") return@forEach
        add("clientImplementation", it.extensions.getByType(SourceSetContainer::class)["client"].output)
    }

    implementation("blue.endless:jankson:${properties["jankson_version"]}")
    implementation("commons-io:commons-io:${properties["commons_io_version"]}")

    //modRuntimeOnly("com.terraformersmc:modmenu:${properties["modmenu_version"]}")
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
}

tasks.test {
    useJUnitPlatform()
}