@file:Suppress("UnstableApiUsage")

import com.github.breadmoirai.githubreleaseplugin.GithubReleaseTask
import java.net.URI

plugins {
	id("fabric-loom") version "1.3-SNAPSHOT"
	id("io.github.juuxel.loom-vineflower") version "1.11.0"
	id("com.github.breadmoirai.github-release") version "2.4.1"
	id("org.ajoberstar.grgit") version "5.0.+"
	id("maven-publish")
}

version = properties["mod_version"]!!
group = properties["maven_group"]!!

base {
	archivesName.set("${properties["archives_base_name"]}")
}

repositories {
	maven {
		name = "ParchmentMC"
		url = URI("https://maven.parchmentmc.org")
		content {
			includeGroup("org.parchmentmc.data")
		}
	}
	maven {
		name = "TerraformersMC"
		url = URI("https://maven.terraformersmc.com/releases/")
		content {
			includeGroup("com.terraformersmc")
			includeGroup("dev.emi")
		}
	}
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
}

loom {
    splitEnvironmentSourceSets()

	mods {
		create("jackfredlib") {
			sourceSet(sourceSets["main"])
			sourceSet(sourceSets["client"])
		}
	}

	runConfigs {
		configureEach {
			val path = buildscript.sourceFile?.parentFile?.resolve("log4j2.xml")
			path?.let { property("log4j2.configurationFile", path.path) }
		}
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${properties["minecraft_version"]}")
	@Suppress("UnstableApiUsage")
	mappings(loom.layered {
		officialMojangMappings()
		parchment("org.parchmentmc.data:parchment-${properties["parchment_version"]}@zip")
	})
	modImplementation("net.fabricmc:fabric-loader:${properties["loader_version"]}")

	setOf(
		"fabric-api-base",
		"fabric-command-api-v2"
	).forEach { modImplementation(fabricApi.module(it, properties["fabric_api_version"]!!.toString())) }

	modImplementation("com.terraformersmc:modmenu:${properties["modmenu_version"]}")
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
				if (prefixList.any { prefix -> it.startsWith(prefix) })
					lines.add("  - $it")
			}
			proc.waitFor()
			filePath.get().asFile.writeText(lines.joinToString("\n"))
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
			tasks.remapJar.get(),
			tasks.remapSourcesJar.get(),
			tasks.named("javadocJar").get()
		)
		body = provider {
			return@provider filePath.get().asFile.readText()
		}
	}
}

// configure the maven publication
publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"]!!)
		}
	}

	repositories {
		maven {
			name = "GitHubPackages"
			url = URI("https://maven.pkg.github.com/JackFred2/JackFredLib")
			credentials {
				username = System.getenv("GITHUB_ACTOR")
				password = System.getenv("GITHUB_TOKEN")
			}
		}
	}
}