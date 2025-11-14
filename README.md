# Waila

## Contribution

When forking this repository, make sure to uncheck the `Copy the master branch only` option.
This will copy all tags to your fork, so the build system can detect the latest mod version.
If you leave it checked, you won't be able to enter the world in a local client run because Waila [will think the mod is outdated](https://github.com/GTNewHorizons/waila/blob/master/src/main/java/mcp/mobius/waila/Waila.java#L133).

If you already forked the repository without tags, you can fetch them manually:
```
git remote add upstream https://github.com/GTNewHorizons/waila
git fetch upstream --tags
```
