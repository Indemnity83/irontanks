# AGENTS.md

This file provides guidance to coding agents (and is kept in sync with CLAUDE.md) when working in this repository.

> **This branch is `mc/26.1`** — a NeoForge **and** Fabric multiloader line for Minecraft 26.1.
> The `mc/1.x` branches are the older single-module Forge lines (see **Branch Strategy**). Always
> confirm your branch with `git branch --show-current` before starting.

## What Iron Tanks Is

Iron Tanks adds tiered-capacity and special-purpose fluid tanks. It follows "Minecraft physics": a
single block holds more fluid the better its material, and obsidian-clad tanks are explosion-proof.

- **Tiered tanks** (capacity in buckets): Glass 16 → Copper 27 → Iron 32 → Silver 43 → Gold 48 →
  Diamond 64 → Emerald 96. Obsidian (64) is explosion-proof.
- **Optional high tiers**, gated on conventional material tags that ship empty and light up in packs
  that add the metal: Aluminium 96 → Stainless Steel 128 → Titanium 256 → Tungstensteel 512.
- **In-place upgrade items** promote a placed tank along the upgrade graph without losing contents
  (`TankUpgrade` — a graph, not a chain: glass forks to copper/iron, and gold has two routes in).
- **Void tank** (8) — destroys the fluid it holds, a little each tick.
- **Creative tank** — infinite supply of whatever fluid is placed in it.
- **Vertical stacking** — connected tanks form a column that shares one fluid (liquids settle to the
  bottom, gases rise) and renders as one continuous body.
- **Potion storage** — a tank holds a potion deposited by bottle, and gives it back by bottle. Stored
  potions are *sealed* from the fluid API so pipes and buckets can't drain them into plain water.
- **Contents readout** — right-click empty-handed for a one-line description on the action bar; the
  same line drives the Jade HUD when Jade is installed.

On the `mc/26.1` line Iron Tanks is **standalone** — no BuildCraft dependency, and every integration
below is optional and soft. It interoperates with any mod through the platform fluid APIs (NeoForge
capabilities / Fabric Transfer API), so pipes and pumps can fill and drain tanks out of the box.

- **Logistics** (optional) — when the logistics mod is installed, iron tanks join a shared cross-mod
  fluid column driven through the logistics API. Compile-only; the integration package is the only
  code that names `com.logistics.*` types and is class-loaded behind a mod-present check.
- **Jade** (optional) — look-at HUD showing the column's contents. Compile-only, same pattern.
- **Crash reporting** (opt-in, default **OFF**) — sanitized, Iron-Tanks-only Sentry reporting, driven
  by `/irontanks diagnostics enable|disable`. See **Crash Reporting** below and `CRASH_REPORTING.md`.

- **Mod ID:** `irontanks`
- **Java package:** `com.indemnity83.irontanks` (loader-agnostic logic under `…irontanks.core`)
- **Maven group:** `com.indemnity83.irontanks`, artifact base name `irontanks`
- **CurseForge / Modrinth:** published under "Iron Tanks" (loaders: NeoForge, Fabric)

## Branch Strategy (IMPORTANT!)

**FIRST:** Always check your current branch with `git branch --show-current`.

This repo uses a **branch-per-Minecraft-version** strategy. Each `mc/*` branch is an independent,
releasable line with its own build toolchain:

| Branch | Minecraft | Loaders | Build toolchain |
|---|---|---|---|
| **`mc/26.2`** | 26.2 | **NeoForge + Fabric** (multiloader) | Loom 1.17 + ModDevGradle 2.x, **JDK 25**, Gradle 9.x |
| **`mc/26.1`** | 26.1.2 | **NeoForge + Fabric** (multiloader) | Loom 1.16 + ModDevGradle 2.x, **JDK 25**, Gradle 9.x |
| **`mc/1.12.2`** | 1.12.2 | Forge | ForgeGradle 2.3, JDK 8, Gradle 4.10.3 |
| **`mc/1.11.2`** | 1.11.2 | Forge | ForgeGradle 2.2, JDK 8 |
| **`mc/1.7.10`** | 1.7.10 | Forge | RetroFuturaGradle 1.4.x, JDK 17–21, Gradle 8.x |

There is no shared trunk — all work targets the appropriate `mc/*` branch. The `mc/26.x` lines are a
ground-up multiloader rewrite; they do **not** share code or build setup with the `mc/1.x` Forge lines.
`mc/26.2` is a compatibility fork of `mc/26.1` — same feature set, different Minecraft (see
**Version Management**).

### Worktree Layout

This repo is checked out as **git worktrees in sibling directories**, one per branch. The primary
`.git` lives in `irontanks-assets/` (the `assets` branch); every other line is a linked worktree:

| Directory | Branch |
|---|---|
| `../irontanks-assets/` | `assets` — source art / recipe sources; **holds the primary `.git`** |
| `../irontanks-mc-26.2/` | `mc/26.2` (main) |
| `../irontanks-mc-26.1/` | `mc/26.1` |
| `../irontanks-mc-1.12.2/` | `mc/1.12.2` |
| `../irontanks-mc-1.11.2/` | `mc/1.11.2` |
| `../irontanks-mc-1.7.10/` | `mc/1.7.10` |

**The working-directory path tells you which line you're on** — `../irontanks-mc-1.7.10/` is the
`mc/1.7.10` branch. Confirm with `git branch --show-current`; list them with `git worktree list`.
Don't assume these paths exist — discover them, and note `assets` is content-only (no Gradle build).

Because the worktrees share one `.git`, a commit in one is immediately visible to the others. Work on
another line via `git -C <path> …` rather than switching branches in place — that keeps each
directory's build outputs and dev-run state intact.

⚠️ **`remote.origin.fetch` must be the full `+refs/heads/*:refs/remotes/origin/*`.** A narrowed
refspec (pinned to a single branch) leaves the other `origin/mc/*` refs stale indefinitely — they
never update, so `git log origin/mc/<version>` silently reports outdated remote state and
`git worktree add --track` fails with "not a branch". Verify with
`git config --get-all remote.origin.fetch` before trusting any cross-branch comparison.

### Git Town

Git Town is configured for this repo and used headlessly — no interactive prompts, safe to run from
scripts and agents:

| Setting | Value |
|---|---|
| `main-branch` | `mc/26.2` |
| `perennial-branches` | `assets` |
| `perennial-regex` | `^mc/` — every `mc/*` line is perennial (protected), including future ones |
| `observed-regex` | `^(release-please--\|l10n/)` — release-please and Crowdin translation branches |
| `forge-type` / `github-connector` | `github` / `gh` |
| `ship-strategy` | `api` |

Prefer these over plain `git` for the standard feature-branch flow:

| Instead of… | Use |
|---|---|
| `git checkout -b <branch>` (off main) | `git town hack <branch>` |
| `git checkout -b <branch>` (stacked on current) | `git town append <branch>` |
| `git pull` / merging main into a feature branch | `git town sync` |
| Opening a PR by hand | `git town propose` |
| Squash-merging a PR | `git town ship` |
| Deleting a merged/obsolete feature branch | `git town delete` |

**Exceptions that stay plain `git`:**
- **Work targeting a legacy line.** `git town hack` parents new branches to `main-branch`
  (`mc/26.2`), which is the wrong parent for a `mc/1.x` branch — and an unrelated history besides.
  Branch off the target line directly and open the PR with `gh pr create --base mc/<version>`
  (`gh` may also need an explicit `--head <branch>`).
- Read-only inspection (`git status`, `git log`, `git diff`, `git branch --show-current`).

### Branch Protection Rules (CRITICAL)

**Never push or commit directly to an `mc/*` branch.** These are protected (and configured as
Git Town perennial branches via `^mc/` — see [Git Town](#git-town) above). All work — including in
auto mode — goes through a feature branch and a PR into the matching `mc/*` branch.

1. Create a feature branch with **Git Town** so it is parented correctly:
   `git town hack descriptive-branch-name` (Git Town's `main-branch` is `mc/26.2`, so the new branch
   is parented to it automatically — don't use a bare `git checkout -b`)
2. Make commits on the feature branch
3. Push the feature branch: `git push -u origin descriptive-branch-name` (or `git town propose`)
4. Open a PR targeting the `mc/*` branch you started from

**In auto mode:** still pause and confirm before any `git push` when the current branch is `mc/*` or
when no feature branch has been created yet. A wrong push to a protected branch is hard to undo.

### Cross-Version Porting

Whether a change ports mechanically depends on which pair of lines you are crossing. **Check for a
merge base before assuming** — `git merge-base <a> <b>` answers it in one command.

| Pair | Shared history? | How to port |
|---|---|---|
| `mc/26.1 ↔ mc/26.2` | **Yes** — `mc/26.2` was forked from `mc/26.1` | `git cherry-pick` (usually clean) |
| `mc/1.12.2 ↔ mc/1.11.2` | Yes | `git cherry-pick` (usually clean) |
| `mc/1.7.10 ↔ other Forge lines` | Yes, distant | Cherry-pick often conflicts; usually manual |
| **`mc/26.x ↔ mc/1.x`** | **No common ancestor** | **Hand port only** |

The `mc/26.x` multiloader lines and the `mc/1.x` Forge lines have completely different architecture
and toolchains *and* unrelated git histories, so `git merge` between them refuses outright
(`refusing to merge unrelated histories`), there is no shared base to diff against, and cherry-pick
is not an option. Those are hand ports.

Between the two 26.x lines the opposite is true: they share the fork point and the same file layout,
so a fix normally cherry-picks with the same commit message on both sides. The `backport` skill
handles this flow, and `converge-branches` shrinks the standing diff between them.

## Build Commands

```bash
./gradlew build                 # Build everything: core (+ unit tests), fabric, neoforge
./gradlew :core:test            # Run the pure-logic unit tests (no Minecraft, fast)
./gradlew :core:jacocoTestReport # Run core tests + write the JaCoCo coverage report (XML for Codecov)
./gradlew :neoforge:runClient   # Launch the NeoForge dev client
./gradlew :fabric:runClient     # Launch the Fabric dev client
./gradlew :neoforge:jar         # Build just the NeoForge mod jar
./gradlew :fabric:jar           # Build just the Fabric mod jar
./gradlew spotlessCheck         # Lint: verify formatting (the CI gate)
./gradlew spotlessApply         # Auto-fix formatting (Java + JSON)
```

**Requirements:** **JDK 25** and Gradle 9.x (via the wrapper). Versions live in `gradle.properties`
(`minecraft_version`, `neoforge_version`, `fabric_loader_version`, `fabric_version`, `loom_version`,
`moddev_version`, `java_version`). Keep the NeoForge/Fabric versions in sync with `minecraft_version`
(see the recommended pairing comment in `gradle.properties`).

**Build output:**
- `neoforge/build/libs/irontanks-<version>.jar`
- `fabric/build/libs/irontanks-<version>.jar`

Local/dev builds default the version to `0.0.0-dev-local` (NeoForge's FML rejects a version without a
leading numeric component at load time — a bare `dev-local` throws `InvalidModFileException`). The
fallback is defined once in the root `allprojects` block and inherited by both loaders. CI injects the
real version via the `MOD_VERSION` env var — never hard-code a version in source. A dev client needs
JDK 25 (the project toolchain).

### Version Management

The `mc/26.1` line uses **release-please** (`release-type: simple`, component `mc26.1`) with **SemVer
build metadata**, publishing **two artifacts per release** (one per loader).

**How it works:**
1. Create a feature/fix branch; commit with single-line imperative subjects (see **Commit Messages**)
2. Open a PR with a conventional-commit **title, no scope** (`fix: …`, `feat: …`), release-notes body
3. Squash-merge into `mc/26.1` using the conventional-commit title
4. `prepare-release.yml` runs release-please, opening/updating the release PR for the branch
5. Merge the release PR → release-please tags and creates a GitHub Release
6. `build-release.yml` builds both loader jars and publishes them to Modrinth/CurseForge

**Version bumps (Iron Tanks is post-1.0, standard SemVer):** `fix:` → patch, `feat:` → minor,
`feat!:`/`BREAKING CHANGE:` → major.

**Naming conventions** (component-based, two artifacts):
- Git tag: `mc26.1-v{semver}` (e.g. `mc26.1-v3.3.1`)
- Artifacts / published version: `{semver}+mc{minecraft_version}.{loader}`
  (e.g. `3.3.1+mc26.1.2.fabric`, `3.3.1+mc26.1.2.neoforge`)
- Display name: `Iron Tanks v{semver} for {loader} {minecraft_version}`

**Do NOT manually edit version numbers.** Let release-please manage `.release-please-manifest.json`;
only edit it to recover from a bad state (e.g. after a hotfix).

### Snapshot / Pre-release builds

`build-snapshot.yml` (manual) publishes a weekly Minecraft-style snapshot (`26w23a+mc….<loader>`,
version-type `alpha`). `build-prerelease.yml` (manual) publishes `…-pre.N` betas. Both run per loader.

## Architecture

The `mc/26.1` line is a **multiloader** Gradle project with three subprojects:

```
core/        Pure Java — NO Minecraft on its classpath. Fully unit-tested.
fabric/      Fabric loader glue (Loom). Depends on :core, embeds it in the jar.
neoforge/    NeoForge loader glue (ModDevGradle). Depends on :core, embeds it in the jar.
resources/   Shared assets + data (textures, models, recipes, lang) — used by BOTH loaders.
```

**Why this shape:** the loader-independent game logic lives once in `core`; each loader writes only
thin Minecraft glue. There is **no shared Minecraft-typed module and no ServiceLoader seam** — `core`
touches no MC types, so it just compiles into each loader jar directly. (This is deliberately simpler
than the source-injection / Architectury approaches; Iron Tanks is the testbed for it.)

### `core` — loader-agnostic logic (`com.indemnity83.irontanks.core`)

**Units — read this first.** The canonical fluid unit in `core` is the **droplet**
(`TankTier.DROPLETS_PER_BUCKET = 81_000`, matching Fabric's `FluidConstants.BUCKET`). Droplets are
used because a bottle is exactly one third of a bucket (`DROPLETS_PER_BOTTLE = 27_000`), which is
*not* a whole number of millibuckets — in droplets every bucket/bottle operation is exact integer
arithmetic. Fabric is droplet-native and needs no conversion; **NeoForge speaks millibuckets and
converts at its adapter boundary** using `DROPLETS_PER_MB` (81). Nothing inside `core` is in mB.

- `TankTier` — the capacity table: buckets, hardness, blast resistance, and the droplet constants
  above. Includes the special `VOID` and `CREATIVE` tiers and the optional high tiers.
- `FluidColumn` — pure distribution math on `long` droplet amounts: `settle` (distribute a total
  across a column, liquids bottom-up / gases top-down), `fillable`, `drainable`, `totalCapacity`.
- `TankColumn<F>` — the whole column *algorithm*, owned once here instead of copied into each
  loader's transfer-API adapter: `insert`/`extract`, `depositBottle`/`extractBottle`, `rebalance`,
  and the aggregation helpers (`total`, `capacity`, `room`, `shared`, `mixed`). Its rules:
  - **Mixed columns** (two distinct fluids joined together) are never aggregated — every operation
    refuses, mirroring `rebalance()` leaving them unsettled.
  - **Potions** are sealed from the fluid path: `insert`/`extract` reject them, so they move only
    through `depositBottle`/`extractBottle`.
  - **Creative** tanks are an endless source/sink and never join a column.
  - A `quantum` parameter floors the moved amount for loaders coarser than droplets (Fabric passes
    1; NeoForge passes `DROPLETS_PER_MB`), so a sub-quantum remainder is never partially filled.
  - An `onMutate` hook runs exactly once immediately before the first write, so a loader captures
    its transaction snapshot only when the column is actually about to change.
- `TankCell<F>` / `FluidKind<F>` — the two seams that let `TankColumn` work without Minecraft types.
  Each loader's `TankBlockEntity` implements `TankCell` directly; `FluidKind` supplies the handful of
  facts about the loader's fluid type (`empty`, `isEmpty`, `isGas`, `isPotion`).
- `VoidTank` — the 20 mB/tick self-destruction rate (`RATE`, expressed in droplets).
- `TankUpgrade` — the upgrade graph (which tier promotes to which).
- `crash/` — the opt-in crash reporter, also pure Java (see **Crash Reporting**).

`core` has JUnit tests and no MC dependency, so `./gradlew :core:test` runs instantly. It's the only
module with a test source set; **JaCoCo** measures its coverage (`:core:jacocoTestReport`, pinned to a
Java-25-capable tool version) and CI uploads the report to **Codecov** (see **CI / Automation**).

### Loader glue (mirrored in `neoforge/` and `fabric/`, package `…irontanks.<loader>`)

Each loader has near-identical, mostly-vanilla classes plus a loader-specific fluid adapter:

- `content/TankBlock` — `BaseEntityBlock`; `joined_below` blockstate property (stacked side texture) +
  `skipRendering` (seamless seam) + `useItemOn` (bucket and bottle interaction) + `useWithoutItem`
  (the action-bar readout) + ticker.
- `content/TankBlockEntity` — stores one fluid + amount **in droplets**; implements `core`'s
  `TankCell`; column traversal + balance via `core`; void/creative behavior; NBT (`ValueInput/Output`,
  persisted as whole mB in `Amount` plus a droplet remainder in `Rem`) + client sync.
- `content/TankBlockItem` — `appendHoverText` shows the capacity / void / creative tooltips.
- `content/UpgradeItem` — `useOn` swaps the tank block in place, preserving fluid.
- `content/IronTanksContent` — registers blocks, items, the shared `BlockEntityType`, and the tab.
  **All tank blocks share one `BlockEntityType`.**
- `content/TankReadout` — the one-line contents description used by both the empty-hand readout and
  the Jade HUD; renders a stored potion as its effect line (amplifier + duration).
- `content/{FluidResourceKind,FluidVariantKind}` — the loader's `FluidKind` implementation.
- Fluid adapter — the only genuinely loader-specific logic:
  - NeoForge: `content/TankFluidHandler` implements `ResourceHandler<FluidResource>`, registered via
    `Capabilities.Fluid.BLOCK` (`IronTanksCapabilities`). **Speaks millibuckets**, so it converts
    mB↔droplets (×81) at its boundary and passes `quantum = DROPLETS_PER_MB` into `core`.
  - Fabric: `content/TankFluidStorage` implements `Storage<FluidVariant>`, registered via
    `FluidStorage.SIDED`. Droplet-native, so no conversion and `quantum = 1`.
- `client/TankBlockEntityRenderer` (+ `TankRenderState`) — draws the fluid level; resolves the fluid's
  sprite/tint through `ModelManager.getFluidStateModelSet()` → `FluidModel`. Registered per loader
  (NeoForge `EntityRenderersEvent`, Fabric `ClientModInitializer` + `BlockEntityRenderers.register`).
- `compat/` — the optional integrations (see below).
- `crash/` — `CrashReportingBootstrap` (wires `core`'s reporter to the loader), `CrashReportNotifier`
  (the once-per-session operator notice), `IronTanksDiagnosticsCommand`, and the loader's
  `PlatformInfo`.
- Entry points — NeoForge `@Mod IronTanksNeoForge` (registers during `RegisterEvent`); Fabric
  `IronTanksFabric` (`ModInitializer`) + `IronTanksFabricClient` (`ClientModInitializer`).

The fluid adapters expose the **whole vertical column** as one logical tank, so a pipe/bucket filling
any tank in a stack fills the column (then `core` re-settles it).

> **Note on Fabric's layout:** the Fabric module has a separate `src/client` source set, so its
> client-only classes live under `fabric/src/client/java/…/fabric/client/` rather than beside the
> rest. NeoForge keeps everything in `src/main`.

### Optional integrations (`compat/`)

Both are **compile-only** and must never be on the runtime critical path:

- **Logistics** — `compat/LogisticsTanks` is a neutral seam holding no `com.logistics.*` references,
  so the content classes that call it link cleanly whether or not logistics is installed. The
  `compat/logistics/` package is the *only* code that names logistics types and is class-loaded
  behind a mod-present check, so its absence can never cause a `NoClassDefFoundError`.
  `LogisticsTankCell` adapts a `TankBlockEntity` to the logistics cell contract (converting
  droplets↔mB) so a cross-mod column settles as one shared body.
- **Jade** — `compat/JadeTankPlugin` renders `TankReadout` on the look-at HUD. Same pattern.

**When adding an integration, follow this shape:** a neutral seam in the loader package, all
third-party types quarantined in one sub-package, and class-loading gated on a mod-present check.

### Crash Reporting

Opt-in (**default OFF**), Iron-Tanks-only, sanitized Sentry reporting. It lives in `core/crash/`
because it needs no Minecraft:

- `CrashReporting` — the orchestrator. Builds a **dedicated** `SentryClient` and never calls
  `Sentry.init()`, so it cannot clobber a global Sentry SDK another mod might bundle.
- `IronTanksConfig` / `CrashReportingConfig` — `config/irontanks.json`, read/written with Gson.
  Best-effort: a missing or corrupt file yields defaults rather than throwing into mod load.
- `Log4j2Bridge` — attaches a forwarding appender to the Log4j2 **root** logger and filters by logger
  name so only Iron Tanks' own `ERROR` events with a throwable are captured.
- `LogScrubber` — strips identifying substrings before anything leaves the process; deliberately
  biased toward over-redaction.
- `PlatformInfo` — the loader/environment facts `core` cannot discover on its own.

Each loader calls `CrashReporting.bootstrap` once at init and exposes
`/irontanks diagnostics enable|disable`, which keep the persisted config and the live Sentry client
in lock-step. Sentry, Gson and Log4j2 are `compileOnly` in `core` (Minecraft supplies the latter two
at runtime; Sentry ships jar-in-jar). Player-facing documentation is `CRASH_REPORTING.md`.

### Resources

Shared assets and data live **once** in the top-level `resources/` directory. Each loader folds it
into its own resource source set (`sourceSets.main.resources.srcDir(rootProject.file('resources'))`),
so the files reach both the mod jar and the dev resource root — no per-loader duplication.
- `resources/assets/irontanks/{blockstates,models/{block,item},items,textures/{block,item},lang}` —
  block/item models, the `items/*.json` 26.1 item-model definitions, textures, `en_us.json`.
- `resources/data/irontanks/recipe/` — `minecraft:crafting_shaped` recipes using conventional `c:` tags
  (`#c:ingots/iron`, `#c:glass_blocks/colorless`, …) — the modern "ore dictionary".
- `resources/data/irontanks/loot_table/blocks/` — every tank drops itself when mined.
- `resources/data/c/tags/item/ingots/…` — declares optional convention tags we reference but don't
  populate (an empty `c:ingots/silver`, and the same for the optional high-tier metals) so those
  recipes load without errors and light up automatically if another mod adds the material.
- `resources/data/minecraft/tags/block/mineable/` — tool tags, so tanks break with the right tool.
- Only loader-specific metadata stays per-module: `neoforge/.../META-INF/neoforge.mods.toml` and
  `fabric/.../fabric.mod.json`.

**Adding a tank tier is a checklist, not architecture:** a `TankTier` entry, `TankUpgrade` paths,
per-loader registration, blockstate + block/item models + `items/*.json` + textures, a lang key, a
recipe, a loot table, and the `mineable` tag. The `release-qa` skill audits exactly this list.

### Conventions

- **Put real behavior in `core`** and keep the loader glue thin; share the math, not the MC code.
- **Bug fixes are test-driven:** first write a failing `core` test that reproduces the bug and watch it
  fail, then make the fix that turns it green. Push the buggy logic into `core` if it isn't there yet so
  it can be covered — keep loader glue too thin to need its own tests.
- **Tiers are data-driven:** adding a tier is a `TankTier` entry + per-loader registration + its asset
  set + recipes, not new architecture.
- Special tanks (void/creative) are tier-driven branches in the shared `TankBlockEntity`, not subclasses.
- **Everything in `core` is droplets.** Converting to or from millibuckets is the NeoForge adapter's
  job and happens only at its boundary. A `long` amount crossing into `core` is always droplets — if
  you find yourself writing `1000` to mean a bucket, you are 81× off.
- **Optional integrations stay soft:** compile-only, third-party types quarantined in one package,
  class-loading behind a mod-present check. Iron Tanks must run with none of them installed.
- **Both loaders stay in parity.** The `neoforge/` and `fabric/` glue classes are deliberate mirrors —
  a fix to one is almost always a fix to the other. Check the twin before opening a PR.

## Code Style

- Formatting is enforced by **Spotless** (configured once in the root `build.gradle`):
  **palantir-java-format** for Java (4-space indent), **gson** for JSON resources. Base whitespace
  rules (UTF-8, LF, final newline, trim trailing whitespace) still live in **`.editorconfig`**.
  Run `./gradlew spotlessApply` to fix and `./gradlew spotlessCheck` to verify.
- Single-line `if`/`for` allowed; prefer braces for multi-line bodies; keep nesting ≤ 3 levels.
- **CI** (`check-code.yml`, JDK 25, on PRs into `mc/**`) splits into three jobs: `lint`
  (`spotlessCheck`), `test` (`:core:jacocoTestReport`, which also uploads coverage to Codecov), and a
  `build` matrix that smoke-builds each loader jar once lint + test pass. Formatting is now a hard CI
  gate, not just the PR-title check.

## Commit Messages

Output a SINGLE-LINE commit subject only:
- No conventional-commit prefix (no `feat:`, `fix:`, etc.)
- No body, no co-author trailer
- Imperative mood ("Add", "Fix", "Refactor"); aim for ≤ 72 characters; be specific.

The **PR title** carries the conventional-commit type that release-please consumes; individual commits
stay plain. This keeps history readable and squash-merges clean.

## Pull Requests

Use conventional-commit format for PR titles, **without a scope** (`<type>: <description>`):
- **No scope** — Iron Tanks is small and focused (`requireScope: false` in `check-pr.yml`).
- The subject must **start with a lowercase letter** (enforced by `check-pr.yml`).

Examples: `feat: add emerald tank`, `fix: prevent tanks from losing fluid on save`,
`build: bump fabric-api to the latest 26.1.2 release`.

**PR body should read like release notes:** WHAT changed and WHY it matters to players; short
Summary / Changes / Notes sections; bullet points; minimal implementation detail.

### Release notes and PR title strategy

Release notes are for **players**. Changelog-visible types (see `release-please-config.json`):

| Type | Section | Use for |
|---|---|---|
| `feat` | Added | New player-visible behavior |
| `fix` | Fixed | Player-visible bug fixes |
| `perf` | Improved | Player-visible performance improvements |

Internal types kept out of the changelog: `refactor`, `test`, `build`, `ci`, `chore`, `docs`, `revert`.
(`check-pr.yml` accepts: `feat`, `fix`, `docs`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`,
`revert`.) Prefer player-facing wording for `feat`/`fix`/`perf`; use internal types for non-player work.

## CI / Automation

GitHub Actions in `.github/workflows/`:

- **`check-pr.yml`** — validates the PR title (no-scope conventional commit, lowercase subject).
- **`check-code.yml`** — JDK 25 on push/PR into `mc/**`; three jobs: `lint` (`spotlessCheck`), `test`
  (`:core:jacocoTestReport`), and a `build` matrix (`[fabric, neoforge]`) that smoke-builds each loader
  jar and runs only after lint + test pass. JDK/Gradle setup is shared via the `setup-build` composite
  action. The `test` job uploads JaCoCo coverage to **Codecov** via `codecov/codecov-action` (needs the
  `CODECOV_TOKEN` secret); `codecov.yml` scopes the `project`/`patch` `auto` status checks to `core/`
  and ignores the (test-free) loader modules.
- **`prepare-release.yml`** — runs release-please on pushes to `mc/*`.
- **`build-release.yml`** — on a published release (or manual `workflow_dispatch`), builds **both** loader
  jars (matrix `[fabric, neoforge]`) and, when publishing, uploads to Modrinth/CurseForge via `mc-publish`
  with `loaders: <loader>` (no BuildCraft dependency).
- **`build-snapshot.yml`** / **`build-prerelease.yml`** — manual alpha/beta builds, per loader.
- **`sync_translations.yml`** — Crowdin round-trip: uploads `en_us.json` on push and opens a
  translation PR from the long-lived `l10n/crowdin` branch on a daily cron. Runs with
  `contents: write` + `pull-requests: write` and the `PERSONAL_TOKEN` PAT (so its PRs trigger the
  check gates). The `schedule:`/`workflow_dispatch:` triggers only ever fire from the **default
  branch** and its push filter is branch-pinned, so the workflow serves exactly one `mc/*` line —
  and on this branch both point at `mc/26.2`, so the copy here never runs. Translation sync happens
  on the 26.2 line; bring new languages to `mc/26.1` by porting the lang files.

Repo **secrets**: `MODRINTH_TOKEN`, `CURSEFORGE_TOKEN`, `GRADLE_ENCRYPTION_KEY`, `PERSONAL_TOKEN`
(publishing and release automation); `CODECOV_TOKEN` (coverage upload); `CROWDIN_PROJECT_ID`,
`CROWDIN_PERSONAL_TOKEN` (translation sync); `SENTRY_AUTH_TOKEN` (uploads source context for the
crash reporter — the build skips that step when it is absent). Repo **variables**:
`MODRINTH_PROJECT_ID`, `CURSEFORGE_PROJECT_ID`.

The Sentry **DSN is not a secret** — it is a compiled-in constant (`CrashReporting.DEFAULT_DSN`),
since DSNs are write-only and designed to be embedded in shipped clients. Operators can point it
elsewhere with `crashReporting.dsnOverride` in `config/irontanks.json`.

**Dependabot** (`.github/dependabot.yml`) covers each `mc/*` branch with `chore`-prefixed commits; the
`mc/26.1` block pins Loom/ModDevGradle major versions and keeps the Gradle wrapper on 9.x.

## Documentation

- `CLAUDE.md` — primary development guidance for Claude Code (keep in sync with this file)
- `AGENTS.md` (this file) — the same guidance for other coding agents
- `README.md` — user-facing project overview
- `CRASH_REPORTING.md` — the player/operator-facing privacy document for the opt-in crash reporter;
  linked from the in-game notice, so keep it accurate
- `CHANGELOG.md` — auto-generated release notes
- [Iron Tanks wiki](https://github.com/Indemnity83/irontanks/wiki) — tank tiers, capacities, recipes

**Release configuration:** `.release-please-manifest.json` (current version), `release-please-config.json`
(component `mc26.1`, changelog sections), and the workflows above.
