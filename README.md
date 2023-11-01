# JackFredLib

Collection of common utilities for my Minecraft mods, and where I'm figuring out gradle/CI. Currently includes:

- Tri-state value holder for Fabric API's events.
- Gradient/colour toolkit
- Lying API to fake entities and coloured entity outlines to the client.
  - Planned: block & particles
- Easy creation of custom [toasts](https://en.wikipedia.org/wiki/Pop-up_notification), with progress bars and images.
- Custom Data for Command Source Stacks, allowing for repeatable arguments. Helpers for this are planned.
- GPS module for client-only mods to determine where they are without server support - think minimap data, or for a 
  practical example [Chest Tracker](https://modrinth.com/mod/chest-tracker).
- Simple config module, oriented for server side but works on both

Feel free to use yourself (see wiki for instructions and a basic overview).

## Usage:

See the [maven](https://maven.jackf.red/#/releases) as well as the [javadoc](https://maven.jackf.red/javadoc/releases/red/jackf/jackfredlib/jackfredlib/latest).

## IntelliJ Import Error

If you get the error `module name <name> should be available`, open the root module settings 
(right click `JackFredLib` > `Open Module Settings`), then make sure all names for the projects are lower case.

I think this is an IntelliJ bug.
