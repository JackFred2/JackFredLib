@file:Suppress("UnstableApiUsage")

import com.github.breadmoirai.githubreleaseplugin.GithubReleaseTask
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask
import red.jackf.GenerateChangelogTask
import red.jackf.UpdateDependenciesTask
import java.net.URI

plugins {
    id("fabric-loom") version "1.2.7" apply false
    id("io.github.juuxel.loom-vineflower") version "1.11.0" apply false
    id("com.github.breadmoirai.github-release") version "2.4.1"
    id("org.ajoberstar.grgit") version "5.0.+"
    id("maven-publish")
}

///////////////////
// PROJECT UTILS //
///////////////////

fun Project.getSourceSet(name: String) = this.extensions.getByType(SourceSetContainer::class)[name]

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

    // TODO: publishing
}

// Shortcut for adding mixin extras as a dependency
extra["usesMixinExtras"] = fun(dependencies: DependencyHandler) {
    dependencies.add("include",
        dependencies.add("implementation",
            dependencies.add("annotationProcessor",
                "io.github.llamalad7:mixinextras-fabric:${properties["mixin_extras_version"]}")!!)!!)
}

version = properties["mod_version"]!!

subprojects {
    version = properties["${this.name}_version"] ?: properties["mod_version"]!!
}

allprojects {
    group = "red.jackf.jackfredlib"

    apply(plugin="fabric-loom")
    apply(plugin="io.github.juuxel.loom-vineflower")

    repositories {
        maven {
            name = "ParchmentMC"
            url = URI("https://maven.parchmentmc.org")
            content {
                includeGroup("org.parchmentmc.data")
            }
        }
        maven {
            name = "JitPack"
            url = URI("https://jitpack.io")
            content {
                includeGroupByRegex("com.github.llamalad7.*")
            }
        }
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

    val loom = project.extensions.getByType<LoomGradleExtensionAPI>()

    dependencies {
        add("minecraft", "com.mojang:minecraft:${properties["minecraft_version"]}")
        add("mappings", loom.layered {
            officialMojangMappings()
            parchment("org.parchmentmc.data:parchment-${properties["parchment_version"]}@zip")
        })
        add("modImplementation", "net.fabricmc:fabric-loader:${properties["loader_version"]}")
        add("modImplementation", "net.fabricmc.fabric-api:fabric-api:${properties["fabric-api_version"]}")
    }

    tasks.withType<ProcessResources>().configureEach {
        inputs.property("version", version)

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
    archiveClassifier = "fatjavadoc"
}

tasks.getByName("build").dependsOn(javadocJarTask)

////////////////
// PUBLISHING //
////////////////

fun setupRepositories(repos: RepositoryHandler) {
    repos.mavenLocal()
}

// Main
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(javadocJarTask)
            artifactId = "jackfredlib"
        }
    }

    setupRepositories(repositories)
}

// Modules
subprojects {
    if (name == "jackfredlib-testmod") return@subprojects

    apply(plugin = "maven-publish")

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                artifactId = this@subprojects.name
            }
        }

        setupRepositories(repositories)
    }
}

// Github Release
val lastTagVal = properties["lastTag"]?.toString()
val newTagVal = properties["newTag"]?.toString()
if (lastTagVal != null && newTagVal != null) {
    val generateChangelogTask = tasks.register<GenerateChangelogTask>("generateChangelog") {
        lastTag.set(lastTagVal)
        newTag.set(newTagVal)
        githubUrl.set(properties["github_url"]!!.toString())
        prefixFilters.set(properties["changelog_filter"]!!.toString().split(","))
    }

    tasks.named<GithubReleaseTask>("githubRelease") {
        dependsOn(generateChangelogTask)

        authorization = System.getenv("GITHUB_TOKEN")?.let { "Bearer $it" }
        owner = properties["github_owner"]!!.toString()
        repo = properties["github_repo"]!!.toString()
        tagName = newTagVal
        releaseName = "${properties["mod_name"]} $newTagVal"
        targetCommitish = grgit.branch.current().name
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