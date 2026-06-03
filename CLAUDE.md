# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

> **This branch is `mc/26.1`** — a NeoForge **and** Fabric multiloader line for Minecraft 26.1.
> The `mc/1.x` branches are the older single-module Forge lines (see **Branch Strategy**). Always
> confirm your branch with `git branch --show-current` before starting.

## What Iron Tanks Is

Iron Tanks adds tiered-capacity and special-purpose fluid tanks. It follows "Minecraft physics": a
single block holds more fluid the better its material, and obsidian-clad tanks are explosion-proof.

- **Tiered tanks** (capacity in buckets): Glass 16 → Copper 27 → Iron 32 → Silver 43 → Gold 48 →
  Diamond 64 → Emerald 96. Obsidian (64) is explosion-proof.
- **In-place upgrade items** promote a placed tank to the next tier without losing contents.
- **Void tank** — destroys the fluid it holds, a little each tick.
- **Creative tank** — infinite supply of whatever fluid is placed in it.
- **Vertical stacking** — connected tanks form a column that shares one fluid (liquids settle to the
  bottom, gases rise) and renders as one continuous body.

On the `mc/26.1` line Iron Tanks is **standalone** — no BuildCraft dependency. It interoperates with
any mod through the platform fluid APIs (NeoForge capabilities / Fabric Transfer API), so pipes and
pumps can fill and drain tanks out of the box.

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
| **`mc/26.1`** | 26.1.2 | **NeoForge + Fabric** (multiloader) | Loom 1.16 + ModDevGradle 2.x, **JDK 25**, Gradle 9.x |
| **`mc/1.12.2`** | 1.12.2 | Forge | ForgeGradle 2.3, JDK 8, Gradle 4.10.3 |
| **`mc/1.11.2`** | 1.11.2 | Forge | ForgeGradle 2.2, JDK 8 |
| **`mc/1.7.10`** | 1.7.10 | Forge | RetroFuturaGradle 1.4.x, JDK 17–21, Gradle 8.x |

There is no shared trunk — all work targets the appropriate `mc/*` branch. The `mc/26.1` line is a
ground-up multiloader rewrite; it does **not** share code or build setup with the `mc/1.x` Forge lines.

### Branch Protection Rules (CRITICAL)

**Never push or commit directly to an `mc/*` branch.** These are protected. All work — including in
auto mode — goes through a feature branch and a PR into the matching `mc/*` branch.

1. Create a feature branch first: `git checkout -b descriptive-branch-name`
2. Make commits on the feature branch
3. Push the feature branch: `git push origin descriptive-branch-name`
4. Open a PR targeting the `mc/*` branch you started from

**In auto mode:** still pause and confirm before any `git push` when the current branch is `mc/*` or
when no feature branch has been created yet. A wrong push to a protected branch is hard to undo.

### Cross-Version Porting

The `mc/26.1` multiloader line and the `mc/1.x` Forge lines have completely different architecture and
toolchains, so changes do **not** cherry-pick between them — port by hand. Within the Forge lines,
`mc/1.12.2 ↔ mc/1.11.2` still cherry-pick cleanly; `mc/1.7.10` is usually manual.

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

Local/dev builds default the version to `0.0.0-dev` (NeoForge rejects a non-SemVer version like
`dev-local` at load time). CI injects the real version via the `MOD_VERSION` env var — never hard-code
a version in source. A dev client needs JDK 25 (the project toolchain).

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
- Git tag: `mc26.1-v{semver}` (e.g. `mc26.1-v2.2.0`)
- Artifacts / published version: `{semver}+mc{minecraft_version}.{loader}`
  (e.g. `2.2.0+mc26.1.2.fabric`, `2.2.0+mc26.1.2.neoforge`)
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

- `TankTier` — the capacity table (buckets + millibuckets). Canonical unit is **millibuckets** (mB),
  `BUCKET_VOLUME = 1000`.
- `FluidColumn` — the crown jewel: `settle` (distribute a total across a column, liquids bottom-up /
  gases top-down), `fillable`, `drainable`, `totalCapacity`. Pure arithmetic on `long` amounts.
- `VoidTank` — the 20 mB/tick self-destruction rate.
- `TankUpgrade` — the upgrade graph (which tier promotes to which).

`core` has JUnit tests and no MC dependency, so `./gradlew :core:test` runs instantly. It's the only
module with a test source set; **JaCoCo** measures its coverage (`:core:jacocoTestReport`, pinned to a
Java-25-capable tool version) and CI uploads the report to **Codecov** (see **CI / Automation**).

### Loader glue (mirrored in `neoforge/` and `fabric/`, package `…irontanks.<loader>`)

Each loader has near-identical, mostly-vanilla classes plus a loader-specific fluid adapter:

- `content/TankBlock` — `BaseEntityBlock`; `joined_below` blockstate property (stacked side texture) +
  `skipRendering` (seamless seam) + `useItemOn` (bucket interaction) + ticker.
- `content/TankBlockEntity` — stores one fluid + amount (mB); column traversal + balance via `core`;
  void/creative behavior; NBT (`ValueInput/Output`) + client sync.
- `content/TankBlockItem` — `appendHoverText` shows the capacity / void / creative tooltips.
- `content/UpgradeItem` — `useOn` swaps the tank block in place, preserving fluid.
- `content/IronTanksContent` — registers blocks, items, the shared `BlockEntityType`, and the tab.
- Fluid adapter — the only genuinely loader-specific logic:
  - NeoForge: `content/TankFluidHandler` implements `ResourceHandler<FluidResource>`, registered via
    `Capabilities.Fluid.BLOCK` (`IronTanksCapabilities`). Works in mB.
  - Fabric: `content/TankFluidStorage` implements `Storage<FluidVariant>`, registered via
    `FluidStorage.SIDED`. **Fabric measures fluid in droplets** (`FluidConstants.BUCKET` = 81000); this
    adapter converts mB↔droplets (×81) at the boundary so `core` and the tile stay in mB.
- `client/TankBlockEntityRenderer` (+ `TankRenderState`) — draws the fluid level; resolves the fluid's
  sprite/tint through `ModelManager.getFluidStateModelSet()` → `FluidModel`. Registered per loader
  (NeoForge `EntityRenderersEvent`, Fabric `ClientModInitializer` + `BlockEntityRenderers.register`).
- Entry points — NeoForge `@Mod IronTanksNeoForge` (registers during `RegisterEvent`); Fabric
  `IronTanksFabric` (`ModInitializer`) + `IronTanksFabricClient` (`ClientModInitializer`).

The fluid adapters expose the **whole vertical column** as one logical tank, so a pipe/bucket filling
any tank in a stack fills the column (then `core` re-settles it).

### Resources

Shared assets and data live **once** in the top-level `resources/` directory. Each loader folds it
into its own resource source set (`sourceSets.main.resources.srcDir(rootProject.file('resources'))`),
so the files reach both the mod jar and the dev resource root — no per-loader duplication.
- `resources/assets/irontanks/{blockstates,models/{block,item},items,textures/{block,item},lang}` —
  block/item models, the `items/*.json` 26.1 item-model definitions, textures, `en_us.json`.
- `resources/data/irontanks/recipe/` — `minecraft:crafting_shaped` recipes using conventional `c:` tags
  (`#c:ingots/iron`, `#c:glass_blocks/colorless`, …) — the modern "ore dictionary".
- `resources/data/c/tags/item/…` — declares optional convention tags we reference but don't populate
  (an empty `c:ingots/silver`) so the silver recipes load without errors and light up automatically if
  another mod adds silver.
- Only loader-specific metadata stays per-module: `neoforge/.../META-INF/neoforge.mods.toml` and
  `fabric/.../fabric.mod.json`.

### Conventions

- **Put real behavior in `core`** and keep the loader glue thin; share the math, not the MC code.
- **Tiers are data-driven:** adding a tier is a `TankTier` entry + per-loader registration + its asset
  set + recipes, not new architecture.
- Special tanks (void/creative) are tier-driven branches in the shared `TankBlockEntity`, not subclasses.

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

Publishing needs repo **secrets** `MODRINTH_TOKEN`, `CURSEFORGE_TOKEN`, `GRADLE_ENCRYPTION_KEY`,
`PERSONAL_TOKEN` and **variables** `MODRINTH_PROJECT_ID`, `CURSEFORGE_PROJECT_ID`.

**Dependabot** (`.github/dependabot.yml`) covers each `mc/*` branch with `chore`-prefixed commits; the
`mc/26.1` block pins Loom/ModDevGradle major versions and keeps the Gradle wrapper on 9.x.

## Documentation

- `CLAUDE.md` (this file) — primary development guidance for Claude Code
- `AGENTS.md` — the same guidance for other coding agents (keep in sync with this file)
- `README.md` — user-facing project overview
- `CHANGELOG.md` — auto-generated release notes
- [Iron Tanks wiki](https://github.com/Indemnity83/irontanks/wiki) — tank tiers, capacities, recipes

**Release configuration:** `.release-please-manifest.json` (current version), `release-please-config.json`
(component `mc26.1`, changelog sections), and the workflows above.
