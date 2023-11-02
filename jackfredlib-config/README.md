# JackFredLib: Config

A small config library designed to be `include`d in mods, optimized for server side mods. Uses Jankson as a base, and
contains a hot-reload listener (TODO), migration rules, and utilities for making commands for the configuration.

## TODO

- Hot Reload listener
- Version migration
  - Helper implementations: JSON Patch
- Annotation-based verifiers, mainly for ints
- Autopopulation of `Range`, `Valid Options`, and `Default` comments.