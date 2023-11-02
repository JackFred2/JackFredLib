base {
    archivesName.set("jackfredlib-lying")
}

@Suppress("UNCHECKED_CAST")
val moduleDependencies = rootProject.extra["moduleDependencies"] as (Project, List<String>, Boolean) -> Unit

moduleDependencies(project, listOf("jackfredlib-base", "jackfredlib-colour"), false)

loom {
    accessWidenerPath.set(file("src/main/resources/jackfredlib-lying.accesswidener"))
}
