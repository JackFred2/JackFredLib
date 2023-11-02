base {
    archivesName.set("jackfredlib-config")
}

dependencies {
    include(implementation("blue.endless:jankson:${properties["jankson_version"]}")!!)

    testImplementation("net.fabricmc:fabric-loader-junit:${properties["loader_version"]}")
}

sourceSets {
    test {
        compileClasspath += client.get().compileClasspath
        runtimeClasspath += client.get().runtimeClasspath
        compileClasspath += client.get().output
        runtimeClasspath += client.get().output
    }
}

tasks.withType<Test> {
    this.useJUnitPlatform()
}

@Suppress("UNCHECKED_CAST")
val moduleDependencies = rootProject.extra["moduleDependencies"] as (Project, List<String>) -> Unit

moduleDependencies(project, listOf("jackfredlib-base"))

