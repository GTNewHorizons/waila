# Waila

## Contribution

When forking this repository, make sure to uncheck the "Copy the default branch only" option.
This will copy all tags to your fork, so the build system can detect the latest mod version.
If you leave it checked, you won't be able to enter the world in a local client run because Waila [will think the mod is outdated](https://github.com/GTNewHorizons/waila/blob/master/src/main/java/mcp/mobius/waila/Waila.java#L133).
