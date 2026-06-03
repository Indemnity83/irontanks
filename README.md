# Iron Tanks

**Tiered fluid tanks that obey Minecraft physics** — the better the material, the more a single
block holds, and obsidian-clad tanks shrug off explosions. Stack them and they merge into one tall
tank; upgrade them in place without spilling a drop.

[![Check Code](https://github.com/Indemnity83/irontanks/actions/workflows/check-code.yml/badge.svg?branch=mc/26.1)](https://github.com/Indemnity83/irontanks/actions/workflows/check-code.yml)
[![Modrinth](https://img.shields.io/modrinth/dt/iron-tanks?logo=modrinth&label=Modrinth)](https://modrinth.com/mod/iron-tanks)
[![CurseForge](https://img.shields.io/curseforge/dt/236226?logo=curseforge&label=CurseForge)](https://www.curseforge.com/minecraft/mc-mods/iron-tanks)
![Minecraft 26.1](https://img.shields.io/badge/Minecraft-26.1-brightgreen)
![Loaders: NeoForge | Fabric](https://img.shields.io/badge/loader-NeoForge%20%7C%20Fabric-blue)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue)](LICENSE.txt)

> [!IMPORTANT]
> **Iron Tanks is a storage mod, not a plumbing one.** On its own, the only way to move fluid in or
> out of a tank is by hand with a bucket. It's designed to pair with a **fluid-transport mod** — any
> pipes or pumps that speak the standard fluid API can fill and drain tanks directly. No specific mod
> is required, and there's no longer any BuildCraft dependency.

## Tanks

Each tier crams more fluid into the same one-block footprint:

| Tank | Capacity | Notes |
|---|---:|---|
| [Glass](https://github.com/Indemnity83/irontanks/wiki#glass-tank) | 16 buckets | The starting tank — craft it from glass |
| [Copper](https://github.com/Indemnity83/irontanks/wiki#copper-tank) | 27 buckets | |
| [Iron](https://github.com/Indemnity83/irontanks/wiki#iron-tank) | 32 buckets | |
| [Silver](https://github.com/Indemnity83/irontanks/wiki#silver-tank) | 43 buckets | Needs a mod that adds silver ingots |
| [Gold](https://github.com/Indemnity83/irontanks/wiki#gold-tank) | 48 buckets | |
| [Diamond](https://github.com/Indemnity83/irontanks/wiki#diamond-tank) | 64 buckets | |
| [Emerald](https://github.com/Indemnity83/irontanks/wiki#emerald-tank) | 96 buckets | |
| [Obsidian](https://github.com/Indemnity83/irontanks/wiki#obsidian-tank) | 64 buckets | Explosion-proof |
| **Void** | — | Destroys any fluid pumped into it |
| **Creative** | ∞ | Infinite source of whatever you put in it (creative/testing) |

- **Stack them.** Connected tanks share one fluid and render as a single continuous body — liquids
  settle to the bottom, gases rise to the top.
- **Upgrade in place.** [Upgrade items](https://github.com/Indemnity83/irontanks/wiki#upgrades)
  promote a placed tank to a higher tier without losing its contents.

## Download & Installation

Grab the latest build for your loader from
[Modrinth](https://modrinth.com/mod/iron-tanks) or
[CurseForge](https://www.curseforge.com/minecraft/mc-mods/iron-tanks) and drop it in your `mods` folder:

| Loader | File | Also needs |
|---|---|---|
| **NeoForge** | `irontanks-…+mc<version>.neoforge.jar` | nothing |
| **Fabric** | `irontanks-…+mc<version>.fabric.jar` | [Fabric API](https://modrinth.com/mod/fabric-api) |

> Minecraft 1.12.2 and earlier live on the `mc/*` branches and remain BuildCraft add-ons — see those
> releases.

## Contributing

You don't need to write Java to help:

1. **Spread the word** — every bit of support helps the mod keep improving.
2. **Translate it** — add or improve a language.
3. **[Report an issue](https://github.com/indemnity83/irontanks/issues)** if something's wrong or could be better.
4. **[Open a pull request](https://github.com/indemnity83/irontanks/pulls)** — see `CLAUDE.md` for the
   build setup (a `core` + `fabric` + `neoforge` multiloader Gradle project on JDK 25).

## License

Iron Tanks is open-source under the [MIT license](http://opensource.org/licenses/MIT) — free to use in
any modpack, for any purpose, no permission needed.
