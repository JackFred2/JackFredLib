pluginManagement {
	repositories {
		maven {
			name = "Fabric"
			url = java.net.URI("https://maven.fabricmc.net/")
		}
		mavenCentral()
		gradlePluginPortal()
	}
}

rootProject.name = "jackfredlib"
include("api")
if (!System.getenv().containsKey("CI"))
	include("testmod")
