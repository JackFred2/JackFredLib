# JackFredLib

Collection of common utilities for my Minecraft mods, and where I'm figuring out gradle/CI. Currently includes:

- Gradient/colour toolkit
- Tri-state value holder for Fabric API's events.

Feel free to use yourself (via GitHub Package Registry).

## IntelliJ Import Error

If you get the error `module name <name> should be available`, open the root module settings 
(right click `JackFredLib` > `Open Module Settings`), then make sure all names for the projects are lower case.

I think this is an IntelliJ bug.