base {
    archivesName.set("jackfredlib-lying")
}

repositories {
    maven {
        url = uri("https://jitpack.io")
        content {
            includeGroupAndSubgroups("com.github.iPortalTeam.ImmersivePortalsMod")
            includeGroupAndSubgroups("com.github.qouteall.ImmersivePortalsMod")
        }
    }

    maven {
        url = uri("https://maven.shedaniel.me/")
        content {
            includeGroupAndSubgroups("me.shedaniel")
        }
    }
}

@Suppress("UNCHECKED_CAST")
val moduleDependencies = rootProject.extra["moduleDependencies"] as (Project, List<String>, Boolean) -> Unit

moduleDependencies(project, listOf("jackfredlib-base", "jackfredlib-colour"), false)

dependencies {
    listOf("imm_ptl_core", "q_misc_util", "build").forEach {
        // ImmPtl compat
        modCompileOnly("com.github.qouteall.ImmersivePortalsMod:$it:${properties["imm_ptl_version"]}")
    }
}

loom {
    accessWidenerPath.set(file("src/main/resources/jackfredlib-lying.accesswidener"))
}
