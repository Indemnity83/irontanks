# Changelog

## [3.3.2](https://github.com/Indemnity83/irontanks/compare/mc26.2-v3.3.1...mc26.2-v3.3.2) (2026-09-02)


### Fixed

* make upgrade items work on the logistics glass tank ([#281](https://github.com/Indemnity83/irontanks/issues/281)) ([307e744](https://github.com/Indemnity83/irontanks/commit/307e744e5898c18175874536d9cbebc31d9c02a1))
* point the crash-reporting privacy link at the current docs ([#277](https://github.com/Indemnity83/irontanks/issues/277)) ([cc9aed4](https://github.com/Indemnity83/irontanks/commit/cc9aed47f519df15ce5d91036253724e278631c5))
* prevent a server crash when a tank holds more than its capacity ([#271](https://github.com/Indemnity83/irontanks/issues/271)) ([7ec0800](https://github.com/Indemnity83/irontanks/commit/7ec0800f2d7a77766f5a45655c544c4305928bfb))
* show how full a gas tank really is ([#284](https://github.com/Indemnity83/irontanks/issues/284)) ([7cdf2c8](https://github.com/Indemnity83/irontanks/commit/7cdf2c857663680d97b23ab28aa62a9bff0746ba))
* stop players drinking a potion the tank refused ([#273](https://github.com/Indemnity83/irontanks/issues/273)) ([f2a973a](https://github.com/Indemnity83/irontanks/commit/f2a973a42a75e28892a7a42c67c8d69737cb9960))
* stop sending other mods' errors to the Iron Tanks crash reporter ([#276](https://github.com/Indemnity83/irontanks/issues/276)) ([7516cae](https://github.com/Indemnity83/irontanks/commit/7516cae5d3d098fb2fe3bba9240dcc2f74c99a5c))
* stop tanks advertising fluid they will never transfer ([#287](https://github.com/Indemnity83/irontanks/issues/287)) ([c866818](https://github.com/Indemnity83/irontanks/commit/c8668187b37285f3392a6aeb63bf91e82c2b7c12))
* stop tanks keeping a phantom volume when their fluid fails to load ([#270](https://github.com/Indemnity83/irontanks/issues/270)) ([6b6efec](https://github.com/Indemnity83/irontanks/commit/6b6efec3d9547f5c85fca53ac32a9edfe02c3c88))
* stop void tanks from draining the whole stack ([#272](https://github.com/Indemnity83/irontanks/issues/272)) ([0641436](https://github.com/Indemnity83/irontanks/commit/0641436ba144e3ff3725813112938748c9b19437))

## [3.3.1](https://github.com/Indemnity83/irontanks/compare/mc26.1-v3.3.0...mc26.1-v3.3.1) (2026-09-01)


### Fixed

* drop the tank block when a placed tank is mined ([#233](https://github.com/Indemnity83/irontanks/issues/233)) ([4a13dd0](https://github.com/Indemnity83/irontanks/commit/4a13dd00e4487fdc90698c5bf6b84553aa7cca80))

## [3.3.0](https://github.com/Indemnity83/irontanks/compare/mc26.1-v3.2.0...mc26.1-v3.3.0) (2026-06-19)


### Added

* add extended tank tiers ([#157](https://github.com/Indemnity83/irontanks/issues/157)) ([4ff26e9](https://github.com/Indemnity83/irontanks/commit/4ff26e927425c17af8e0dce8f1ceaf9b17c88356))
* per-tier tank hardness and blast resistance ([#164](https://github.com/Indemnity83/irontanks/issues/164)) ([f79fcd3](https://github.com/Indemnity83/irontanks/commit/f79fcd37bab13190deb27e1fe597e8f04a009621))
* refresh metal tank textures ([#169](https://github.com/Indemnity83/irontanks/issues/169)) ([7073c4a](https://github.com/Indemnity83/irontanks/commit/7073c4a6a5c8d228f0bdd5a10ae80457ca4379e9))

## [3.2.0](https://github.com/Indemnity83/irontanks/compare/mc26.1-v3.1.1...mc26.1-v3.2.0) (2026-06-18)


### Added

* stack and share fluid with Logistics tanks ([#147](https://github.com/Indemnity83/irontanks/issues/147)) ([fa9aceb](https://github.com/Indemnity83/irontanks/commit/fa9aceb6c7e4e0c82dc92cac851f5eab4897fb8c))

## [3.1.1](https://github.com/Indemnity83/irontanks/compare/mc26.1-v3.1.0...mc26.1-v3.1.1) (2026-06-05)


### Fixed

* restore German, Spanish, Portuguese, Russian, Chinese and Pirate translations ([#133](https://github.com/Indemnity83/irontanks/issues/133)) ([0dbd844](https://github.com/Indemnity83/irontanks/commit/0dbd844db4b37ea2aa6c450af6797b4823b516cb))

## [3.1.0](https://github.com/Indemnity83/irontanks/compare/mc26.1-v3.0.0...mc26.1-v3.1.0) (2026-06-04)


### Added

* show what a tank is holding ([#127](https://github.com/Indemnity83/irontanks/issues/127)) ([45c5f30](https://github.com/Indemnity83/irontanks/commit/45c5f30f8d4b38789e087c25614ac3bc1ad72ff9))
* store potions in tanks with bottles ([#126](https://github.com/Indemnity83/irontanks/issues/126)) ([769785e](https://github.com/Indemnity83/irontanks/commit/769785e32c2de309a0dba099dce71c7e5d63b2a0))


### Fixed

* tank fluid level no longer dips when topped off ([#128](https://github.com/Indemnity83/irontanks/issues/128)) ([522bb78](https://github.com/Indemnity83/irontanks/commit/522bb78fe192f82815b71c56ee48576f18002b01))

## [3.0.0](https://github.com/Indemnity83/irontanks/compare/mc26.1-v2.1.0...mc26.1-v3.0.0) (2026-06-03)


### ⚠ BREAKING CHANGES

* rebuild as a NeoForge + Fabric multiloader for Minecraft 26.1 ([#104](https://github.com/Indemnity83/irontanks/issues/104))

### Added

* add opt-in crash reporting ([#109](https://github.com/Indemnity83/irontanks/issues/109)) ([14d558d](https://github.com/Indemnity83/irontanks/commit/14d558d592904eee9b28fdb325ff1b583ec8d44a))
* rebuild as a NeoForge + Fabric multiloader for Minecraft 26.1 ([#104](https://github.com/Indemnity83/irontanks/issues/104)) ([108ff7e](https://github.com/Indemnity83/irontanks/commit/108ff7e78fbd902ec1f178658b6a565455c34138))


### Fixed

* prevent fluid amount overflow in the NeoForge tank adapter ([#118](https://github.com/Indemnity83/irontanks/issues/118)) ([8b05cbc](https://github.com/Indemnity83/irontanks/commit/8b05cbc29e1e3d9c7a8d6adb256000691b31dc1c))

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
