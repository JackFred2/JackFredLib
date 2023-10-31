base {
    archivesName.set("jackfredlib-config")
}

dependencies {
    include(implementation("blue.endless:jankson:${properties["jankson_version"]}")!!)
}

