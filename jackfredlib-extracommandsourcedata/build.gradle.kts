@file:Suppress("UNCHECKED_CAST")

base {
    archivesName.set("jackfredlib-extracommandsourcedata")
}

val moduleDependencies = rootProject.extra["moduleDependencies"] as (Project, List<String>) -> Unit

moduleDependencies(project, listOf())
