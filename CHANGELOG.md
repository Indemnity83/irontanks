# Changelog

## [2.0.0](https://github.com/Indemnity83/irontanks/compare/mc1.11.2-v1.1.16...mc1.11.2-v2.0.0) (2026-06-01)


Graduates the long-running 2.0.0 beta to a stable release for Minecraft 1.11.2. This is the first
release built on the BuildCraft 7.99 rewrite — a breaking change from the 1.x line for Minecraft
1.7.10 — with no functional changes since the last beta.

### Added

* Rebuild against the BuildCraft 7.99 rewrite for Minecraft 1.11.2 ([0968b86](https://github.com/Indemnity83/irontanks/commit/0968b86))
* Add a creative tank ([ef8f646](https://github.com/Indemnity83/irontanks/commit/ef8f646)), closes [#55](https://github.com/Indemnity83/irontanks/issues/55)
* Add a void tank ([13ad662](https://github.com/Indemnity83/irontanks/commit/13ad662)), closes [#24](https://github.com/Indemnity83/irontanks/issues/24)
* Add an emerald tank ([fab2c9c](https://github.com/Indemnity83/irontanks/commit/fab2c9c))
* Add tank upgrade items and their recipes ([147ddee](https://github.com/Indemnity83/irontanks/commit/147ddee))
* Show tank capacity in the block tooltip ([3c19a96](https://github.com/Indemnity83/irontanks/commit/3c19a96)), closes [#51](https://github.com/Indemnity83/irontanks/issues/51)
* Drop Fragile Shards when a tank breaks ([b7674ef](https://github.com/Indemnity83/irontanks/commit/b7674ef))
* Warn the player when the required BuildCraft mod is missing ([d082849](https://github.com/Indemnity83/irontanks/commit/d082849))
* Refresh block and item textures ([1500ad6](https://github.com/Indemnity83/irontanks/commit/1500ad6))

### Fixed

* Persist tank fluid across game reloads and world saves ([51867a3](https://github.com/Indemnity83/irontanks/commit/51867a3)), closes [#1](https://github.com/Indemnity83/irontanks/issues/1)
* Upgrade tanks in place, transferring contents instead of spilling them ([eb90d33](https://github.com/Indemnity83/irontanks/commit/eb90d33)), closes [#3](https://github.com/Indemnity83/irontanks/issues/3)
* Reflow tank contents when expanding or upgrading stacks ([a06fdd6](https://github.com/Indemnity83/irontanks/commit/a06fdd6))
* Render stacked tanks correctly ([0498896](https://github.com/Indemnity83/irontanks/commit/0498896)), closes [#41](https://github.com/Indemnity83/irontanks/issues/41)
* Keep tanks transparent even when viewed up close ([1bd8c4c](https://github.com/Indemnity83/irontanks/commit/1bd8c4c)), closes [#49](https://github.com/Indemnity83/irontanks/issues/49)
* Make obsidian tanks withstand explosions again ([36eded0](https://github.com/Indemnity83/irontanks/commit/36eded0)), closes [#5](https://github.com/Indemnity83/irontanks/issues/5)
* Correct silver and diamond tank capacities ([6f9507b](https://github.com/Indemnity83/irontanks/commit/6f9507b))
* Align tank and upgrade recipes with the documentation and use glass panes for upgrades ([ae9d68e](https://github.com/Indemnity83/irontanks/commit/ae9d68e)), closes [#50](https://github.com/Indemnity83/irontanks/issues/50)
* Display correct information in the in-game mods list ([8840e95](https://github.com/Indemnity83/irontanks/commit/8840e95)), closes [#45](https://github.com/Indemnity83/irontanks/issues/45)
* Modernize the build system and release tooling ([dd6fcd7](https://github.com/Indemnity83/irontanks/commit/dd6fcd7))
