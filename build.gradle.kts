@file:Suppress("UnstableApiUsage", "RedundantNullableReturnType")

import com.github.breadmoirai.githubreleaseplugin.GithubReleaseTask
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask
import org.ajoberstar.grgit.Grgit
import red.jackf.GenerateChangelogTask
import red.jackf.UpdateDependenciesTask

plugins {
    id("fabric-loom") version "1.4-SNAPSHOT" apply false
    id("com.github.breadmoirai.github-release") version "2.4.1" apply false
    id("org.ajoberstar.grgit") version "5.0.+"
    id("maven-publish")
}

///////////////////
// PROJECT UTILS //
///////////////////

// make it nullable because it's able to be null(!)
val grgit: Grgit? = project.grgit

fun Project.getSourceSet(name: String) = this.extensions.getByType(SourceSetContainer::class)[name]

operator fun Any?.unaryPlus() = this!!.toString()

// Adapted from Fabric API; helper for depending on other modules
extra["moduleDependencies"] = fun(project: Project, depNames: List<String>) {
    val deps = depNames.map { project.dependencies.project(path = ":$it", configuration = "namedElements") }
    val clientOutputs =
        depNames.map { project(":$it").getSourceSet("client").output }

    project.dependencies {
        deps.forEach {
            add("api", it)
        }

        clientOutputs.forEach {
            add("clientImplementation", it)
        }
    }
}

////////////////////////////
// PROJECT CONFIGURATIONS //
////////////////////////////

val lastTagVal = properties["lastTag"]?.toString()
val newTagVal = properties["newTag"]?.toString()
val canPublish = lastTagVal != null && newTagVal != null && grgit != null

println("Can publish: $canPublish")

fun getVersionSuffix(): String {
    return grgit?.branch?.current()?.name ?: "nogit"
}

allprojects {
    version = "${+properties["module_version"]}+${getVersionSuffix()}"
    group = properties["maven_group"]!!

    apply(plugin="fabric-loom")

    tasks.withType<JavaCompile> {
        options.compilerArgs.add("-Xlint:unchecked")
    }

    project.extensions.configure<JavaPluginExtension> {
        withSourcesJar()
    }

    project.extensions.configure<LoomGradleExtensionAPI> {
        splitEnvironmentSourceSets()

        runConfigs.configureEach {
            this.isIdeConfigGenerated = false
        }

        mods {
            create(project.name) {
                sourceSet(project.getSourceSet("main"))
                sourceSet(project.getSourceSet("client"))
            }
        }
    }
    /////////////////////////
    // GLOBAL DEPENDENCIES //
    /////////////////////////

    repositories {
        maven {
            name = "ParchmentMC"
            url = uri("https://maven.parchmentmc.org")
            content {
                includeGroup("org.parchmentmc.data")
            }
        }
        mavenCentral()
    }

    val loom = project.extensions.getByType<LoomGradleExtensionAPI>()

    dependencies {
        add("minecraft", "com.mojang:minecraft:${properties["minecraft_version"]}")
        add("mappings", loom.layered {
            officialMojangMappings()
            parchment("org.parchmentmc.data:parchment-${properties["parchment_version"]}@zip")
        })
        add("modImplementation", "net.fabricmc:fabric-loader:${properties["loader_version"]}")
        add("modImplementation", "net.fabricmc.fabric-api:fabric-api:${properties["fabric-api_version"]}")

        // add mixin extras as a depdendency to all, but only bundle on root project
        val mixinExtrasDep = add("implementation",
            add("annotationProcessor",
                "io.github.llamalad7:mixinextras-fabric:${properties["mixin_extras_version"]}")!!)!!

        if (this@allprojects == rootProject)
            add("include", mixinExtrasDep)
    }

    ///////////////
    // PACKAGING //
    ///////////////

    tasks.withType<ProcessResources>().configureEach {
        inputs.property("module_version", version)
        inputs.property("module_name", +properties["module_name"])
        inputs.property("module_description", +properties["module_description"])

        inputs.property("root_module_name", +rootProject.properties["module_name"])
        inputs.property("root_module_description", +rootProject.properties["module_description"])

        filesMatching("fabric.mod.json") {
            expand(inputs.properties)
        }
    }

    tasks.named<Jar>("jar") {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_${this@allprojects.name}" }
        }
    }
}

////////////////////////
// PACKAGING MAIN JAR //
////////////////////////

dependencies {
    afterEvaluate {
        subprojects.forEach {
            if (it.name == "jackfredlib-testmod") return@forEach

            add("api", project(path=it.path, configuration = "namedElements"))
            add("clientImplementation", it.getSourceSet("client").output)
        }
    }
}

subprojects.forEach {
    if (it.name == "jackfredlib-testmod") return@forEach

    tasks.getByName<RemapJarTask>("remapJar").dependsOn("${it.path}:remapJar")

    it.tasks.getByName<ProcessResources>("processResources") {
        from(rootProject.file("src/main/resources/assets/jackfredlib/icon.png")) {
            into("assets/jackfredlib")
        }
    }
}

tasks.getByName<RemapJarTask>("remapJar") {
    afterEvaluate {
        subprojects.forEach {
            if (it.name == "jackfredlib-testmod") return@forEach

            nestedJars.from(it.tasks.getByName("remapJar"))
        }
    }
}

/////////////
// JAVADOC //
/////////////

tasks.withType<Javadoc>().configureEach {
    options.showFromPublic()

    subprojects.forEach {
        if (it.name == "jackfredlib-testmod") return@forEach

        source(it.getSourceSet("main").allJava)
        source(it.getSourceSet("client").allJava)
    }

    include("red/jackf/jackfredlib/api/**/*.java")
    include("red/jackf/jackfredlib/client/api/**/*.java")

    classpath = files(getSourceSet("main").compileClasspath, getSourceSet("client").compileClasspath)

    (options as StandardJavadocDocletOptions).tags(
        "apiNote:a:API Note:",
        "implNote:a:Implementation Note:"
    )
}

val javadocJarTask = tasks.register<Jar>("javadocJar") {
    dependsOn("javadoc")
    from(tasks.getByName<Javadoc>("javadoc").destinationDir)
    archiveClassifier = "javadoc"
}

tasks.getByName("build").dependsOn(javadocJarTask)

////////////////
// PUBLISHING //
////////////////

fun setupRepositories(repos: RepositoryHandler) {
    repos.mavenLocal()
    /*
    repos.maven {
        name = "GitHubPackages_JackFredLib"
        url = uri("https://maven.pkg.github.com/JackFred2/JackFredLib")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }*/
    repos.maven {
        name = "JackFred-Maven"
        url = uri("https://maven.jackf.red/releases")
        credentials {
            username = System.getenv("JACKFRED_MAVEN_USER")
            password = System.getenv("JACKFRED_MAVEN_PASS")
        }
    }
}

// Modules
allprojects {
    if (!canPublish) return@allprojects
    if (name == "jackfredlib-testmod") return@allprojects

    apply(plugin = "maven-publish")

    val propertiesHandle = properties

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifactId = this@allprojects.name

                from(components["java"])
                if (this@allprojects.parent == null) artifact(javadocJarTask)

                pom {
                    name = +propertiesHandle["module_name"]
                    description = +propertiesHandle["module_description"]
                    url = +propertiesHandle["github_url"]
                    licenses {
                        license {
                            name = "LGPL-3.0"
                            url = "https://opensource.org/license/lgpl-3-0/"
                        }
                    }
                    developers {
                        developer {
                            url = "https://jackf.red"
                            name = "JackFred2"
                        }
                    }
                    scm {
                        connection = "scm:git:git://github.com/JackFred2/JackFredLib.git"
                        developerConnection = "scm:git:git://github.com/JackFred2/JackFredLib.git"
                        url = +propertiesHandle["github_url"]
                    }
                }
            }
        }

        setupRepositories(repositories)
    }
}

// Github Release
if (canPublish) {
    apply(plugin = "com.github.breadmoirai.github-release")

    val generateChangelogTask = tasks.register<GenerateChangelogTask>("generateChangelog") {
        lastTag.set(lastTagVal)
        newTag.set(newTagVal)
        githubUrl.set(properties["github_url"]!!.toString())
        prefixFilters.set(properties["changelog_filter"]!!.toString().split(","))

        // Add a bundled block for each module version
        prologue.set("""
			|Bundled:
			|  - Mixin Extras: ${properties["mixin_extras_version"]}
			${
            subprojects.filter { it.name != "jackfredlib-testmod" }
                .joinToString(separator = "\n") { "|  - ${it.properties["module_name"]}: ${it.properties["module_version"]}+${it.properties["minecraft_version"]}" }
        }
			""".trimMargin())
    }

    tasks.named<GithubReleaseTask>("githubRelease") {
        dependsOn(generateChangelogTask)

        authorization = System.getenv("GITHUB_TOKEN")?.let { "Bearer $it" }
        owner = properties["github_owner"]!!.toString()
        repo = properties["github_repo"]!!.toString()
        tagName = newTagVal
        releaseName = "${properties["mod_name"]} $newTagVal"
        targetCommitish = grgit!!.branch.current().name
        releaseAssets.from(
            tasks["remapJar"].outputs.files,
            tasks["remapSourcesJar"].outputs.files,
            tasks["javadocJar"].outputs.files,
        )
        body = provider {
            return@provider generateChangelogTask.get().changelogFile.get().asFile.readText()
        }
    }
}

//////////
// MISC //
//////////

tasks.register<UpdateDependenciesTask>("updateModDependencies") {
    mcVersion.set(properties["minecraft_version"]!!.toString())
    loader.set("fabric")
}