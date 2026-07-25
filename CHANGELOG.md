# Changelog

## [2.4.0](https://github.com/Indemnity83/irontanks/compare/mc1.12.2-v2.3.0...mc1.12.2-v2.4.0) (2026-07-25)


### Added

* add Russian translations for extended tank tiers ([#173](https://github.com/Indemnity83/irontanks/issues/173)) ([d213e93](https://github.com/Indemnity83/irontanks/commit/d213e9345c0fc328b63e6a28047257f7393d75d8))


### Fixed

* correct Russian creative tank tooltip spelling ([#208](https://github.com/Indemnity83/irontanks/issues/208)) ([fbb4429](https://github.com/Indemnity83/irontanks/commit/fbb442940651face1f2c01a4653eebb97c03969c))

## [2.3.0](https://github.com/Indemnity83/irontanks/compare/mc1.12.2-v2.2.0...mc1.12.2-v2.3.0) (2026-06-19)


### Added

* add extended tank tiers ([#155](https://github.com/Indemnity83/irontanks/issues/155)) ([90ff0b5](https://github.com/Indemnity83/irontanks/commit/90ff0b5ce06bd7ec39f8233d9f642e24af2ae6a5))
* per-tier tank hardness and blast resistance ([#165](https://github.com/Indemnity83/irontanks/issues/165)) ([291977c](https://github.com/Indemnity83/irontanks/commit/291977c082ee0edb22c03b7f296df98fafdd7866))
* refresh metal tank textures ([#170](https://github.com/Indemnity83/irontanks/issues/170)) ([d1a26b2](https://github.com/Indemnity83/irontanks/commit/d1a26b25d4c5d19a4ab64b71d2162e53b103ce0a))

## [2.2.0](https://github.com/Indemnity83/irontanks/compare/mc1.12.2-v2.1.0...mc1.12.2-v2.2.0) (2026-06-01)


### Added

* add configuration for Creative Tank breakability settings ([#91](https://github.com/Indemnity83/irontanks/issues/91)) ([2a63b2b](https://github.com/Indemnity83/irontanks/commit/2a63b2b7db5d72c958ee3f99876e452d186d25ef))

## [2.1.0](https://github.com/Indemnity83/irontanks/compare/mc1.12.2-v1.1.16...mc1.12.2-v2.1.0) (2026-06-01)


Graduates the long-running 2.1.0 beta to a stable release for Minecraft 1.12.2. Built on the
BuildCraft 7.99 rewrite and carried forward from the 1.7.10 (1.1.16) line, with no functional
changes since the last beta.

### Added

* Rebuild against the BuildCraft 7.99 rewrite for Minecraft 1.12.2 ([ec52e1e](https://github.com/Indemnity83/irontanks/commit/ec52e1e))
* Add a creative tank ([ef8f646](https://github.com/Indemnity83/irontanks/commit/ef8f646)), closes [#55](https://github.com/Indemnity83/irontanks/issues/55)
* Add a void tank ([13ad662](https://github.com/Indemnity83/irontanks/commit/13ad662)), closes [#24](https://github.com/Indemnity83/irontanks/issues/24)
* Add an emerald tank ([a6ba4cf](https://github.com/Indemnity83/irontanks/commit/a6ba4cf))
* Add tank upgrade items and their recipes ([147ddee](https://github.com/Indemnity83/irontanks/commit/147ddee))
* Show tank capacity in the block tooltip ([3c19a96](https://github.com/Indemnity83/irontanks/commit/3c19a96)), closes [#51](https://github.com/Indemnity83/irontanks/issues/51)
* Drop Fragile Shards when a tank breaks ([4d7463f](https://github.com/Indemnity83/irontanks/commit/4d7463f))
* Warn the player when the required BuildCraft mod is missing ([ae4f96e](https://github.com/Indemnity83/irontanks/commit/ae4f96e))
* Refresh block and item textures ([c743d67](https://github.com/Indemnity83/irontanks/commit/c743d67))

### Fixed

* Persist tank fluid across game reloads and world saves ([a675d52](https://github.com/Indemnity83/irontanks/commit/a675d52)), closes [#1](https://github.com/Indemnity83/irontanks/issues/1)
* Upgrade tanks in place, transferring contents instead of spilling them ([52611b3](https://github.com/Indemnity83/irontanks/commit/52611b3)), closes [#3](https://github.com/Indemnity83/irontanks/issues/3)
* Reflow tank contents when expanding or upgrading stacks ([76c890d](https://github.com/Indemnity83/irontanks/commit/76c890d))
* Render stacked tanks correctly ([f991486](https://github.com/Indemnity83/irontanks/commit/f991486)), closes [#41](https://github.com/Indemnity83/irontanks/issues/41)
* Keep tanks transparent even when viewed up close ([1bd8c4c](https://github.com/Indemnity83/irontanks/commit/1bd8c4c)), closes [#49](https://github.com/Indemnity83/irontanks/issues/49)
* Make obsidian tanks withstand explosions again ([0cd6fb6](https://github.com/Indemnity83/irontanks/commit/0cd6fb6)), closes [#5](https://github.com/Indemnity83/irontanks/issues/5)
* Correct silver and diamond tank capacities ([6f9507b](https://github.com/Indemnity83/irontanks/commit/6f9507b))
* Align tank and upgrade recipes with the documentation and use glass panes for upgrades ([659a33f](https://github.com/Indemnity83/irontanks/commit/659a33f)), closes [#50](https://github.com/Indemnity83/irontanks/issues/50)
* Empty the void tank at the stack level ([7558b60](https://github.com/Indemnity83/irontanks/commit/7558b60))
* Display correct information in the in-game mods list ([8840e95](https://github.com/Indemnity83/irontanks/commit/8840e95)), closes [#45](https://github.com/Indemnity83/irontanks/issues/45)
* Modernize the build system and release tooling ([8f21f67](https://github.com/Indemnity83/irontanks/commit/8f21f67))
