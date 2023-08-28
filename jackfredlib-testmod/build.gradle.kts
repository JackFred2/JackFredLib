@file:Suppress("UnstableApiUsage")

version = properties["mod_version"]!!
group = properties["maven_group"]!!

base {
    archivesName.set("${properties["archives_base_name"]}-testmod")
}

loom {
    runConfigs.register("testClient") {
        client()
        ideConfigGenerated(true)
        //name.set("Run Test Client")
    }

    log4jConfigs.from(rootProject.file("log4j2.xml"))
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