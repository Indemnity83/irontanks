# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
 - This CHANGELOG
 - Warn the user if they are missing the required Buildcraft mod

### Changed
 - Build process is now normalized across supported versions
 - Updated Chinese translation (@DYColdWind)
 - Versions use build numbers instead of git hashes

## [2.1.0-beta.5] - 2018-02-17
### Added
 - Tank block items now have a tooltip with capacity
 - Check out the new creative and void tanks!

### Changed
 - Bump buildcraft to 7.99.13
 - Many of the recipes have changed for both tanks and upgrades. Upgrades now use glass panes instead of glass blocks and some of the tanks require less glass blocks and have re-arranged items. Check out the [Tank](https://github.com/Indemnity83/irontanks/wiki/Tanks) and [Upgrade](https://github.com/Indemnity83/irontanks/wiki/Upgrades) wikis for current recipes.

## [2.1.0-beta.4] - 2017-12.22
### Fixed
 -  Tanks remain transparent even when you press your face against the glass
 -  Tanks no longer lose capacity between world saves

## [2.1.0-beta.3] - 2017-12-19
### Changed
 - Recipes now match documentation

## [2.1.0-beta.2] - 2017-12-19
### Changed
 - Balanced tank and upgrade recipes
 - Bump forge version to 14.23.1.2555

### Fixed
 - Upgrade items correctly upgrade empty tanks
 - Tank contents reflow properly when expanding or upgrading a tank stack

## [2.1.0-beta.1] - 2017-12-17
### Added
 - Broken tanks drop Fragile Shards (careful, they're sharp)

### Fixed
 -  Stacked tanks now render properly
 -  Fluid remains in the tank when performing an upgrade, this is quite a bit less messy than spilling the contents all over the world
 - Obsidian tanks withstand explosions again

## [2.1.0-alpha.2] - 2017-12-15
### Fixed
- Tanks now persist fluid levels between game sessions
- Tank upgrade items now function properly

## [2.1.0-alpha.1] - 2017-12-09
### Changed
 - Updated to minecraft 1.12.2


[Unreleased]: https://github.com/indemnity83/irontanks/compare/v2.1.0-beta.5...develop
[2.1.0-beta.5]: https://github.com/indemnity83/irontanks/compare/v2.1.0-beta.4...v2.1.0-beta.5
[2.1.0-beta.4]: https://github.com/indemnity83/irontanks/compare/v2.1.0-beta.3...v2.1.0-beta.4
[2.1.0-beta.3]: https://github.com/indemnity83/irontanks/compare/v2.1.0-beta.2...v2.1.0-beta.3
[2.1.0-beta.2]: https://github.com/indemnity83/irontanks/compare/v2.1.0-beta.1...v2.1.0-beta.2
[2.1.0-beta.1]: https://github.com/indemnity83/irontanks/compare/v2.1.0-alpha.2...v2.1.0-beta.1
[2.1.0-alpha.2]: https://github.com/indemnity83/irontanks/compare/v2.1.0-alpha.1...v2.1.0-alpha.2
[2.1.0-alpha.1]: https://github.com/indemnity83/irontanks/compare/v2.0.0-beta.2...v2.1.0-alpha.1
