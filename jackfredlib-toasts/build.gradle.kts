base {
    archivesName.set("jackfredlib-toasts")
}

@Suppress("UNCHECKED_CAST")
val moduleDependencies = rootProject.extra["moduleDependencies"] as (Project, List<String>, Boolean) -> Unit

moduleDependencies(project, listOf("jackfredlib-base"), false)
