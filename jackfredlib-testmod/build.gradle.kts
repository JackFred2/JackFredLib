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

@Suppress("UNCHECKED_CAST")
val moduleDependencies = rootProject.extra["moduleDependencies"] as (Project, List<String>) -> Unit

moduleDependencies(project, listOf("jackfredlib-base", "jackfredlib-colour", "jackfredlib-lying", "jackfredlib-toasts", "jackfredlib-extracommandsourcedata"))


dependencies {
    modRuntimeOnly("com.terraformersmc:modmenu:${properties["modmenu_version"]}")
    modRuntimeOnly("com.github.llamalad7.mixinextras:mixinextras-fabric:${properties["mixin_extras_version"]}")
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