import java.net.URI

plugins {
	id("fabric-loom") version "1.3-SNAPSHOT"
	id("io.github.juuxel.loom-vineflower") version "1.11.0"
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

	filesMatching("fabric.mod.json") {
		expand(inputs.properties)
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release.set(17)
}

java {
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

tasks.named<Jar>("sourcesJar") {
	dependsOn(tasks.classes)
	archiveClassifier.set("sources")
	from(sourceSets.main.get().allSource)
}

tasks.jar {
	from("LICENSE") {
		rename { "${it}_${properties["archivesBaseName"]}"}
	}
}

// configure the maven publication
publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifact(tasks.named("sourcesJar")) {
				builtBy(tasks.named("remapSourcesJar"))
			}
			afterEvaluate {
				artifact(tasks.named("remapJar"))
			}
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