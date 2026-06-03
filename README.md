## Iron Tanks

Iron Tanks adds tiered-capacity and special-purpose fluid tanks to Minecraft. It follows strict
"Minecraft physics": a single block of space holds far more fluid the better the material you build it
from, and a tank clad in obsidian is explosion-proof. These tanks can add hours of fun to the game:

- Tiered-capacity tanks — [Glass](https://github.com/Indemnity83/irontanks/wiki#glass-tank) (16
  buckets), [Copper](https://github.com/Indemnity83/irontanks/wiki#copper-tank) (27),
  [Iron](https://github.com/Indemnity83/irontanks/wiki#iron-tank) (32),
  [Silver](https://github.com/Indemnity83/irontanks/wiki#silver-tank) (43),
  [Gold](https://github.com/Indemnity83/irontanks/wiki#gold-tank) (48),
  [Diamond](https://github.com/Indemnity83/irontanks/wiki#diamond-tank) (64), and
  [Emerald](https://github.com/Indemnity83/irontanks/wiki#emerald-tank) (96) — each cramming more into
  the same cube of space.
- Stack tanks vertically and they merge into one shared tank — liquids settle to the bottom, gases rise.
- In-place [upgrade items](https://github.com/Indemnity83/irontanks/wiki#upgrades) so you don't have to
  empty a tank to promote it to a higher tier.
- Explosion-proof [Obsidian](https://github.com/Indemnity83/irontanks/wiki#obsidian-tank) tanks.
- A **Void tank** that destroys fluids pumped into it.
- A **Creative tank** that provides an infinite supply of whatever fluid you put in it.

Tanks expose the standard platform fluid API, so any mod's pipes and pumps can fill and drain them —
or just right-click with a bucket.

## Download & Installation

Grab the latest release for your loader from
[Modrinth](https://modrinth.com/mod/iron-tanks) or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/iron-tanks)
and drop it in your `mods` folder:

- **NeoForge** — install the `…+mc<version>.neoforge.jar`. No other dependencies.
- **Fabric** — install the `…+mc<version>.fabric.jar` **plus** the
  [Fabric API](https://modrinth.com/mod/fabric-api).

Iron Tanks for this Minecraft version is **standalone** — it no longer requires BuildCraft (it works
with any mod through the platform fluid API).

> Older Minecraft versions (1.12.2 and earlier) live on the `mc/*` branches and remain BuildCraft
> add-ons; see those branches/releases.

## Contributing

Plenty to do even if you don't write Java:

1. [Spread the word](https://modrinth.com/mod/iron-tanks) — the more support, the more we can give back.
2. Add or improve a translation.
3. [Submit an issue](https://github.com/indemnity83/irontanks/issues) if something's wrong or could be better.
4. [Open a pull request](https://github.com/indemnity83/irontanks/pulls) — see `CONTRIBUTING`/`CLAUDE.md`
   for the build setup (multiloader Gradle project: `core` + `fabric` + `neoforge`, JDK 25).

Thank you!

## License

Iron Tanks is open-source software licensed under the [MIT license](http://opensource.org/licenses/MIT).
You're free to use it in any modpack for any purpose, no special permission needed.
