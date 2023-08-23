@file:Suppress("UnstableApiUsage")

import com.github.breadmoirai.githubreleaseplugin.GithubReleaseTask
import red.jackf.GenerateChangelogTask
import java.net.URI

plugins {
	id("com.github.breadmoirai.github-release") version "2.4.1"
	id("org.ajoberstar.grgit") version "5.0.+"
	id("maven-publish")
}

version = properties["mod_version"]!!
group = properties["maven_group"]!!

base {
	archivesName.set("${properties["archives_base_name"]}")
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17

	withSourcesJar()
	withJavadocJar()
}

loom {
    splitEnvironmentSourceSets()

	mods {
		create("jackfredlib") {
			sourceSet(sourceSets["main"])
			sourceSet(sourceSets["client"])
		}
	}

	accessWidenerPath.set(file("src/main/resources/jackfredlib.accesswidener"))
}

tasks.withType<Javadoc>().configureEach {
	options.showFromPublic()

	source += sourceSets["client"]!!.allJava
	classpath += sourceSets["client"]!!.compileClasspath
	classpath += sourceSets["client"]!!.output

	include("red/jackf/jackfredlib/api/**/*.java")
	include("red/jackf/jackfredlib/client/api/**/*.java")

	(options as StandardJavadocDocletOptions).tags(
		"apiNote:a:API Note:",
		"implNote:a:Implementation Note:"
	)
}

dependencies {
	modImplementation("net.fabricmc.fabric-api:fabric-api:${properties["fabric-api_version"]}")

	modLocalRuntime("com.terraformersmc:modmenu:${properties["modmenu_version"]}")

	// different configurations from the mixin extras README to remove from maven pom
	include(implementation(annotationProcessor("io.github.llamalad7:mixinextras-fabric:${properties["mixin_extras_version"]}")!!)!!)
}


tasks.withType<ProcessResources>().configureEach {
	inputs.property("version", version)
	inputs.property("mod_name", properties["mod_name"]!!)

	filesMatching("fabric.mod.json") {
		expand(inputs.properties)
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release.set(17)
}

tasks.jar {
	from("LICENSE") {
		rename { "${it}_${properties["archivesBaseName"]}"}
	}
}

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

publishing {
	repositories {
		mavenLocal()
		maven {
			name = "GitHubPackages"
			url = URI("https://maven.pkg.github.com/JackFred2/JackFredLib")
			credentials {
				username = System.getenv("GITHUB_ACTOR")
				password = System.getenv("GITHUB_TOKEN")
			}
		}
	}

	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"]!!)
			artifactId = "jackfredlib"
		}
	}
}