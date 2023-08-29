@file:Suppress("UNCHECKED_CAST")

base {
    archivesName.set("jackfredlib-extracommandsourcedata")
}

(rootProject.extra["moduleDependencies"] as (Project, List<String>) -> Unit)(project, listOf("jackfredlib-base"))
(rootProject.extra["usesMixinExtras"] as ((DependencyHandler) -> Unit))(dependencies)
