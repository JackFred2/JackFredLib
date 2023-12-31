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
include("jackfredlib-base")

include("jackfredlib-config")
include("jackfredlib-colour")
include("jackfredlib-extracommandsourcedata")
include("jackfredlib-gps")
include("jackfredlib-lying")
include("jackfredlib-toasts")


if (!System.getenv().containsKey("CI"))
	include("jackfredlib-testmod")
