base {
    archivesName.set("jackfredlib-config")
}

dependencies {
    include(implementation("blue.endless:jankson:${properties["jankson_version"]}")!!)
}

@Suppress("UNCHECKED_CAST")
val moduleDependencies = rootProject.extra["moduleDependencies"] as (Project, List<String>) -> Unit

moduleDependencies(project, listOf("jackfredlib-base"))

