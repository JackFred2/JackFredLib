base {
    archivesName.set("jackfredlib-gps")
}

val moduleDependencies = rootProject.extra["moduleDependencies"] as (Project, List<String>) -> Unit

moduleDependencies(project, listOf("jackfredlib-base"))