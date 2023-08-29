base {
    archivesName.set("jackfredlib-lying")
}

@Suppress("UNCHECKED_CAST")
val moduleDependencies = rootProject.extra["moduleDependencies"] as (Project, List<String>) -> Unit

moduleDependencies(project, listOf("jackfredlib-base", "jackfredlib-colour"))