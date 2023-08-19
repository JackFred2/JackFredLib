@file:Suppress("UnstableApiUsage")

version = properties["mod_version"]!!
group = properties["maven_group"]!!

base {
    archivesName.set("${properties["archives_base_name"]}-testmod")
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
        //name.set("Run Test Client")
    }

    runConfigs {
        configureEach {
            val path = rootProject.buildscript.sourceFile?.parentFile?.resolve("log4j2.xml")
            path?.let { property("log4j2.configurationFile", path.path) }
        }
    }
}

dependencies {
    modImplementation("net.fabricmc.fabric-api:fabric-api:${properties["fabric-api_version"]}")

    modRuntimeOnly("com.terraformersmc:modmenu:${properties["modmenu_version"]}")

    implementation(project(path=":api", configuration = "namedElements"))
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