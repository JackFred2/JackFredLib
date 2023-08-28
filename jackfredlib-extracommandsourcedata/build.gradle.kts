base {
    archivesName.set("jackfredlib-extracommandsourcedata")
}

@Suppress("UNCHECKED_CAST")
val moduleDependencies = rootProject.extra["moduleDependencies"] as (Project, List<String>) -> Unit

moduleDependencies(project, listOf("jackfredlib-base"))
