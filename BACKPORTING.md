# Backporting from `mc/1.12.2` to `mc/1.11.2`

`mc/1.12.2` is the `latest` development line. `mc/1.11.2` is kept structurally
aligned with it so that commits cherry-pick cleanly:

```sh
git checkout mc/1.11.2
git cherry-pick <sha-from-mc/1.12.2>
```

Most changes apply without conflict. The differences below are **forced by the
MC 1.11.2 / BuildCraft 7.99.7 APIs** and cannot be removed — a cherry-pick that
touches these areas will conflict and must be adapted by hand. This is expected.

## Permanent API differences (1.11.2 vs 1.12.2)

1. **Recipes.** 1.11.2 registers crafting recipes in code
   (`common/core/Recipes.java`, BuildCraft `RecipeBuilderShaped`). 1.12.2 uses
   the data-driven JSON recipe system (`assets/irontanks/recipes/*.json` +
   `_constants.json`), which does not exist in MC 1.11.2. Recipe changes must be
   ported from JSON into `Recipes.java`.

2. **Block tooltip hook.** 1.11.2 overrides
   `addInformation(ItemStack, EntityPlayer, List<String>, boolean)`; 1.12.2 uses
   `addInformation(ItemStack, @Nullable World, List<String>, ITooltipFlag)`.

3. **BuildCraft tank API.**
   - Stacking/connection: 1.11.2 checks `instanceof BlockTank`; 1.12.2 uses
     `ITankBlockConnector`.
   - BuildCraft tank block reference: `BCBlocks.FACTORY_TANK` (1.11.2) vs
     `BCFactoryBlocks.tank` (1.12.2).
   - Activation: `TileTank.onActivate(...)` (1.11.2) vs `onActivated(...)` (1.12.2).
   - `@Mod` dependency id: `BuildCraft|Factory` (1.11.2) vs `buildcraftfactory`
     (1.12.2).
   - **No `canConnectTo` hook.** BuildCraft 7.99.7's `TileTank` joins vertical
     tank stacks unconditionally (private `getTanks()`), so the Creative Tank
     cannot refuse to connect the way it does on 1.12.2. On 1.11.2 it dispenses
     endlessly standalone but will share fluid with a tank directly above/below.
     See `common/tiles/CreativeTankTile.java`.

4. **Build.** `gradle.properties` (MC/Forge/mappings/BuildCraft versions) and
   `build.gradle` (ForgeGradle 2.2 vs 2.3) are pinned per branch.

## Intentionally per-branch (do not cherry-pick)

`CHANGELOG.md`, `.release-please-manifest.json`, and the `component` /
`last-release-sha` fields in `release-please-config.json` reflect each branch's
own release identity and history.
