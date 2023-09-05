@file:Suppress("UNCHECKED_CAST")

base {
    archivesName.set("jackfredlib-extracommandsourcedata")
}

val moduleDependencies = rootProject.extra["moduleDependencies"] as (Project, List<String>) -> Unit
val usesMixinExtras = rootProject.extra["usesMixinExtras"] as (DependencyHandler) -> Unit

moduleDependencies(project, listOf())
usesMixinExtras(dependencies)
