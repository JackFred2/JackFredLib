@file:Suppress("UnstableApiUsage")

import com.github.breadmoirai.githubreleaseplugin.GithubReleaseTask
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

tasks.withType<Javadoc>().configureEach {
	include("red/jackf/jackfredlib/api/**/*.java")
	include("red/jackf/jackfredlib/client/api/**/*.java")

	options.showFromPublic()

	(options as StandardJavadocDocletOptions).tags(
		"apiNote:a:API Note:"
	)
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

dependencies {
	modImplementation("net.fabricmc.fabric-api:fabric-api:${properties["fabric_api_version"]}")

	modRuntimeOnly("com.terraformersmc:modmenu:${properties["modmenu_version"]}")
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

val lastTag = properties["lastTag"]?.toString()
val newTag = properties["newTag"]?.toString()
if (lastTag != null && newTag != null) {
	val filePath = layout.buildDirectory.file("changelogs/$lastTag..$newTag.md")

	val changelogTask = task("generateChangelog") {
		val prefixList = properties["changelog_filter"]!!.toString().split(",")
		println("Writing changelog to ${filePath.get()}")
		outputs.file(filePath)

		doLast {
			val command = "git log --max-count=100 --pretty=format:\"%s\" $lastTag...$newTag"
			val proc = Runtime.getRuntime().exec(command)
			// println(command)
			proc.errorStream.bufferedReader().forEachLine { println(it) }
			val lines = mutableListOf(
				// "# ${properties["mod_name"]} $newTag",
				"Previous: $lastTag",
				""
			)
			properties["github_url"]?.toString()?.also {
				lines.add("Full changelog: ${it}/compare/$lastTag...$newTag")
				lines.add("")
			}
			proc.inputStream.bufferedReader().forEachLine {
				var str = it
				// it starts with quotes in github actions i guess https://www.youtube.com/watch?v=-O3ogWBfWI0
				if (str.startsWith("\"")) str = str.substring(1)
				if (str.endsWith("\"")) str = str.substring(0, str.length - 1)
				if (prefixList.any { prefix -> str.startsWith(prefix) })
					lines.add("  - $str")
			}
			proc.waitFor()
			val changelog = lines.joinToString("\n")
			filePath.get().asFile.writeText(changelog)
		}
	}

	tasks.named<GithubReleaseTask>("githubRelease") {
		dependsOn(changelogTask)
		mustRunAfter(changelogTask)
		inputs.file(filePath)

		authorization = System.getenv("GITHUB_TOKEN")?.let { "Bearer $it" }
		owner = properties["github_owner"]!!.toString()
		repo = properties["github_repo"]!!.toString()
		tagName = newTag
		releaseName = "${properties["mod_name"]} $newTag"
		targetCommitish = grgit.branch.current().name
		releaseAssets.from(
			tasks["remapJar"].outputs.files,
			tasks["remapSourcesJar"].outputs.files,
			tasks["javadocJar"].outputs.files,
		)
		body = provider {
			return@provider filePath.get().asFile.readText()
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