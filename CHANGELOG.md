# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

*Versions prior to 1.1.16 were done on an automated build system which tagged a
new release for every commit (incrementing the patch each time). This was a bit
overeager to tag new versions so many of these versions had no actual changes to
the mod, and no files were actually released. These releases are marked
[SKIPPED] in the changelog.*

## [Unreleased]
### Changed
 - Normalize block and item names across versions
 - Versions use build numbers instead of git hashes
 - Update textures

## [1.1.16] - 2018-03-26
### Added
 - This CHANGELOG file

### Changed
 - Update buildcraft to 7.1.23
 - Update forge to 10.13.4.1558
 - Move builds to Jenkins

## [1.1.15] - 2017-07-20
### Change
 - Limit automated builds to release branches

## [1.1.14] - 2017-07-20
### Change
 - Update readme

## 1.1.13 - [SKIPPED]

## 1.1.12 - [SKIPPED]

## 1.1.11 - [SKIPPED]

## 1.1.10 - [SKIPPED]

## [1.1.9] - 2017-07-16
### Change
 - Prevent automated builds of 1.11.2 branch

## [1.1.8] - 2017-07-12
### Change
 - Use commit hash for local builds
 - Updated readme
 - Limit builds to 1.7.10 and 1.11.1 branches

## 1.1.7 - [SKIPPED]

## 1.1.6 - [SKIPPED]

## [1.1.5] - 2017-07-11
### Change
 - Include commit message in curse upload as changelog

### Fix
 - Tanks always retain fluid above 32 buckets on world reload

## 1.1.4 - [SKIPPED]

## [1.1.3] - 2017-07-11
### Added
 - Portuguese translation

### Fix
 - Fix build modifier in build scripts

### Change
 - Removed NEI and WAILA as dependencies

## 1.1.2 - [SKIPPED]

## [1.0.1] - 2016-10-07
### Fix
 - Stacked tank textures now update properly (6a21a07)
 - Add specific dependency requirement on BuildCraft|Factory v7 (9842144), closes #15

### Changed
 - Delocalize version information for build process
 - Major code cleanup
 - Update Readme with proper badges and links

## 1.0.0 - 2015-10-02
### Added
 - Add recipes for blocks and items (a273f1c)
 - Implemented basic items and blocks (570e191)
 - i18n: Add German translation (fb5550e)
 - i18n: Add Pirate translation (c031a12)
 - i18n: Add Russian (af55841)
 - i18n: Add Simplified Chinese (960e151)
 - i18n: Add Spanish translation (1bb4866)

### Fix
 - Localize the creative tab name (e90e00c)
 - Add missing silver to diamond tank upgrade item (9f1f8be)
 - Fluid in tanks now persists between game sessions (3858b6c)
 - Obsidian tanks are not destroyed by TNT or creeper blasts (f397f4e)
 - Tank upgrade items perform an in place upgrade of tank (a1adf5d)
 - gui: Remove extraneous GUI configuration (ed8b191)

[Unreleased]: https://github.com/indemnity83/irontanks/compare/v1.1.16...support/1.7.10
[1.1.16]: https://github.com/indemnity83/irontanks/compare/v1.1.15...v1.1.16
[1.1.15]: https://github.com/indemnity83/irontanks/compare/v1.1.14...v1.1.15
[1.1.14]: https://github.com/indemnity83/irontanks/compare/v1.1.9...v1.1.14
[1.1.9]: https://github.com/indemnity83/irontanks/compare/v1.1.8...v1.1.9
[1.1.8]: https://github.com/indemnity83/irontanks/compare/v1.1.5...v1.1.8
[1.1.5]: https://github.com/indemnity83/irontanks/compare/v1.1.3...v1.1.5
[1.1.3]: https://github.com/indemnity83/irontanks/compare/v1.0.1...v1.1.3
[1.0.1]: https://github.com/indemnity83/irontanks/compare/v1.0.0...v1.0.1
