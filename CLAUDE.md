# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What Iron Tanks Is

Iron Tanks is a **Minecraft Forge** mod and **BuildCraft add-on** that adds tiered-capacity and special-purpose fluid tanks. It follows "Minecraft physics": a single block can hold more fluid the better its material, and obsidian-clad tanks are explosion-proof.

- **Tiered tanks** (increasing capacity): Glass (16 buckets) → Copper (24) → Iron (32) → Silver (42) → Gold (48) → Diamond (64) → Emerald
- **In-place upgrade items** so a placed tank can be promoted to the next tier without losing contents
- **Obsidian tank** — explosion-proof
- **Void tank** — destroys any fluid that enters it
- **Creative tank** — infinite supply of whatever fluid is placed in it (for testing/creative)

BuildCraft (the `buildcraftfactory` module) is a hard dependency — the mod does nothing useful without it.

- **Mod ID:** `irontanks`
- **Java package:** `com.indemnity83.irontanks`
- **Maven group:** `com.indemnity83.irontanks`, artifact base name `irontanks`
- **CurseForge / Modrinth:** published under "Iron Tanks"

## Branch Strategy (IMPORTANT!)

**FIRST:** Always check your current branch with `git branch --show-current`.

This repo uses a **branch-per-Minecraft-version** strategy. Each `mc/*` branch is an independent, releasable line with its own build toolchain:

| Branch | Minecraft | Build toolchain | Notes |
|---|---|---|---|
| **`mc/26.1`** | 26.1.2 | Loom 1.16 + ModDevGradle 2.x, JDK 25, Gradle 9.x | Repo **default branch** and Git Town `main-branch`. A separate NeoForge + Fabric multiloader line — shares no code, toolchain, or git history with the Forge lines below |
| **`mc/1.12.2`** | 1.12.2 | ForgeGradle 2.3-SNAPSHOT, JDK 8, Gradle 4.10.3 | **Primary Forge-line development target** (newest of the `mc/1.x` Forge branches, current `2.x` line) |
| **`mc/1.11.2`** | 1.11.2 | ForgeGradle 2.2-SNAPSHOT, JDK 8 | Backport target; API is close to 1.12.2 |
| **`mc/1.7.10`** | 1.7.10 | RetroFuturaGradle 1.4.x, JDK 17–21, Gradle 8.x | Backport target; large API gap — usually manual |

The GitHub default branch is `mc/26.1`. There is no shared trunk — all work targets the appropriate `mc/*` branch. Among the Forge lines, `mc/1.12.2` is the newest and where Forge work starts.

### Worktree Layout

This repo is checked out as **git worktrees in sibling directories**, one per branch. The primary `.git` lives in `irontanks-assets/` (the `assets` branch — source art and recipe sources, no Gradle build); every other line is a linked worktree:

| Directory | Branch |
|---|---|
| `../irontanks-assets/` | `assets` — **holds the primary `.git`** |
| `../irontanks-mc-26.1/` | `mc/26.1` (repo default; multiloader line) |
| `../irontanks-mc-1.12.2/` | `mc/1.12.2` |
| `../irontanks-mc-1.11.2/` | `mc/1.11.2` |
| `../irontanks-mc-1.7.10/` | `mc/1.7.10` |

**The working-directory path tells you which line you're on** — `../irontanks-mc-1.7.10/` is the `mc/1.7.10` branch. Confirm with `git branch --show-current`; list them with `git worktree list`. Don't assume these paths exist — discover them.

Because the worktrees share one `.git`, a commit in one is immediately visible to the others. Work on another line via `git -C <path> ...` rather than switching branches in place — that keeps each directory's build outputs and dev-run state intact, which matters here because every line needs a different JDK.

⚠️ **`remote.origin.fetch` must be the full `+refs/heads/*:refs/remotes/origin/*`.** A narrowed refspec (pinned to a single branch) leaves the other `origin/mc/*` refs stale indefinitely — they never update, so `git log origin/mc/<version>` silently reports outdated remote state and `git worktree add --track` fails with "not a branch". Verify with `git config --get-all remote.origin.fetch` before trusting any cross-branch comparison.

### Git Town

Git Town is configured repo-wide, and that config is shared by every worktree: `main-branch` is `mc/26.1`, `perennial-regex` is `^mc/` (so every `mc/*` line is perennial/protected), `perennial-branches` is `assets`, and `observed-regex` is `^release-please--`.

⚠️ **Do not use `git town hack` on this branch.** It parents new branches to `main-branch` (`mc/26.1`), which is the wrong parent for a Forge line — and an unrelated history besides. Git Town's `sync` / `propose` / `ship` are oriented at the `mc/26.1` main line for the same reason. On the `mc/1.x` lines use plain `git` and `gh`, as described in [Branch Protection Rules](#branch-protection-rules-critical) below.

### Branch Protection Rules (CRITICAL)

**Never push or commit directly to an `mc/*` branch.** These are protected. All work — including in auto mode — goes through a feature branch and a PR into the matching `mc/*` branch.

**Required workflow for any new work:**
1. Create a feature branch first: `git checkout -b descriptive-branch-name` (plain `git` — deliberately **not** `git town hack`, which would parent the branch to `mc/26.1`; see [Git Town](#git-town) above)
2. Make commits on the feature branch
3. Push the feature branch: `git push origin descriptive-branch-name`
4. Open a PR targeting the `mc/*` branch you started from: `gh pr create --base mc/<version> --head descriptive-branch-name`. **Pass `--base` explicitly** — `gh` otherwise defaults to the repo default branch (`mc/26.1`), an unrelated history.

**The only exception** is cherry-picking already-merged commits between `mc/*` branches for porting. Even then, confirm with the user before pushing.

**In auto mode:** Still pause and confirm before any `git push` when the current branch is `mc/*` or when no feature branch has been created yet. A wrong push to a protected branch is very hard to undo cleanly.

### Cross-Version Porting

Versions diverge mainly in their Minecraft API and Gradle toolchain, not in the mod's design. Port with cherry-pick where it works, fall back to manual where it doesn't:

- **`mc/1.12.2` ↔ `mc/1.11.2`** — APIs are close. `git cherry-pick` usually applies cleanly or with minor conflicts.
- **`mc/1.7.10`** — the API gap is large (pre-`ResourceLocation`/blockstate-JSON era) and it builds on a different toolchain (RetroFuturaGradle, JDK 17–21). Expect to **reimplement the change by hand** rather than cherry-pick.
- **`mc/26.1`** — a ground-up NeoForge + Fabric multiloader rewrite with **no shared git history** with the Forge lines: `git merge` between them refuses outright (`refusing to merge unrelated histories`) and there is no common base to diff against. Nothing cherry-picks in either direction — port by hand, or not at all.

**When fixing a bug:**
1. Fix on the branch where it was reported (usually `mc/1.12.2`).
2. Check whether the bug exists on the other branches.
3. Cherry-pick to `mc/1.11.2`; reimplement on `mc/1.7.10` if affected.
4. Build/test on each target branch after porting.

**When adding a feature:** develop on `mc/1.12.2` first, then port down as above. Keep changes minimal and isolate version-specific Minecraft API calls so the 1.11.2 cherry-pick stays clean.

## Build Commands

```bash
./gradlew build              # Build the mod JAR
./gradlew runClient          # Launch the Minecraft client for testing
./gradlew runServer          # Launch a dedicated server
./gradlew setupDecompWorkspace   # One-time: set up the decompiled MC workspace (ForgeGradle 2)
```

**Requirements:** see `gradle.properties` (`minecraft_version`, `forge_version`, `mappings`, `java_version`, `buildcraft_version`). On `mc/1.12.2` / `mc/1.11.2` this is **JDK 8**; `mc/1.7.10` builds on **JDK 17–21** under RetroFuturaGradle.

> There is no `remapJar` task (that's a Fabric concept) and no Spotless — this is a ForgeGradle/RetroFuturaGradle project.

**Build output:** `build/libs/irontanks-{version}.jar`
- Local/dev: `irontanks-dev-local.jar` (version defaults to `dev-local`)
- CI: `irontanks-2.1.0+mc1.12.2.jar` (SemVer build-metadata format)

The build `version` is injected by CI via the `MOD_VERSION` env var (e.g. `2.2.0+mc1.12.2`). It also flows into `mcmod.info` and `IronTanks.VERSION` through Gradle's `replace`/`expand` token substitution — never hard-code a version in source.

### Version Management

All `mc/*` branches use **release-please** (`release-type: simple`) for automated, per-branch versioning with **SemVer build metadata**.

**How it works:**
1. Create a feature/fix branch (short, meaningful name — no required format)
2. Make commits using a single-line imperative subject (see **Commit Messages**)
3. Work freely — squash, force-push, iterate
4. Open a PR with:
   - **Title:** conventional-commit format, **no scope** (`fix: …`, `feat: …`)
   - **Body:** release-notes style
5. PR is squash-merged into the target `mc/*` branch using the conventional-commit title
6. `prepare-release.yml` runs release-please, which opens a release PR for that branch
7. Merge the release PR → release-please tags and creates a GitHub Release
8. `build-release.yml` builds on the published release and publishes to Modrinth/CurseForge

**Version bumps (Iron Tanks is post-1.0, standard SemVer):**
- `fix:` → patch (2.1.0 → 2.1.1)
- `feat:` → minor (2.1.0 → 2.2.0)
- `feat!:` / `BREAKING CHANGE:` → major (2.1.0 → 3.0.0)

**Naming conventions** (component-based tags, one component per branch):
- Git tags: `mc{version}-v{semver}` (e.g. `mc1.12.2-v2.1.0`)
- Artifacts: `irontanks-{semver}+mc{version}.jar` (e.g. `irontanks-2.1.0+mc1.12.2.jar`)
- Published version: `{semver}+mc{version}` (e.g. `2.1.0+mc1.12.2`)
- Display name: `Iron Tanks v{semver} for {version}` (e.g. `Iron Tanks v2.1.0 for 1.12.2`)

**Do NOT manually edit version numbers.** Let release-please manage it. The current version per branch lives in `.release-please-manifest.json`; only edit that file directly to recover from a bad state (e.g. after a hotfix).

### Hotfix Workflow

When a critical bug needs a patch release on a branch *after* feature work has already merged there (so the next release-please bump would be a minor), bypass release-please and publish a clean hotfix.

**Steps (example uses `mc/1.12.2`):**
1. Branch from the last release tag:
   ```bash
   git checkout -b hotfix/X.Y.Z mc{version}-vX.Y.Z
   # e.g. git checkout -b hotfix/2.1.1 mc1.12.2-v2.1.0
   ```
2. Apply the fix and commit (single-line imperative subject, no prefix)
3. Tag the hotfix:
   ```bash
   git tag mc{version}-vX.Y.Z      # e.g. git tag mc1.12.2-v2.1.1
   ```
4. Push the branch and tag:
   ```bash
   git push origin hotfix/X.Y.Z
   git push origin mc{version}-vX.Y.Z
   ```
5. Trigger the build manually on GitHub:
   - **Actions → Build (Release) → Run workflow**
   - Set `tag` = `mc1.12.2-v2.1.1`, `publish` = `true`
6. Cherry-pick the fix back onto the branch:
   ```bash
   git checkout <feature-branch off mc/1.12.2>
   git cherry-pick <fix-commit-sha>   # then PR into mc/1.12.2
   ```
7. **Bump the manifest** so release-please resumes from the right base:
   - Edit `.release-please-manifest.json`: update the version to `X.Y.Z`
   - Commit: `Bump release-please manifest to X.Y.Z after hotfix`
8. Delete the hotfix branch:
   ```bash
   git push origin --delete hotfix/X.Y.Z
   git branch -d hotfix/X.Y.Z
   ```

**Why step 7 matters:** release-please reads `.release-please-manifest.json` to determine the current version. Skipping it makes release-please try to create a release PR for the already-shipped hotfix version, producing a duplicate-tag conflict.

## Architecture

Iron Tanks is a small, single-loader **Forge** mod. It uses the classic Forge **SidedProxy** pattern rather than a multiloader/domain architecture — keep it simple and idiomatic for ForgeGradle-era Forge.

### Entry Point & Proxies

- `IronTanks` (`com.indemnity83.irontanks.IronTanks`) — the `@Mod` class. Holds `MODID`/`MODNAME`/`VERSION`, the `@Instance`, and the `@SidedProxy`. It forwards `preInit` / `init` / `postInit` to the proxy.
  - `acceptedMinecraftVersions`, the Forge version, and the BuildCraft version are filled in at build time via Gradle `replace` tokens (`(gradle_replace_mcversion,)`, etc.) — don't replace those literals by hand.
- `common/CommonProxy` — server/common lifecycle (registration, config, recipes).
- `client/ClientProxy extends CommonProxy` — adds client-only setup (model/renderer registration).

### Source Layout

```
src/main/java/com/indemnity83/irontanks/
├── IronTanks.java              # @Mod entry point + SidedProxy wiring
├── common/
│   ├── CommonProxy.java        # common preInit/init/postInit
│   ├── core/
│   │   ├── IronTanksConfig.java  # Forge config (e.g. creative-tank breakability)
│   │   ├── Blocks.java           # block registration/holders
│   │   └── Items.java            # item (upgrade) registration/holders
│   ├── blocks/
│   │   ├── TankBlock.java
│   │   ├── StackableTankBlock.java   # "Minecraft physics" capacity stacking
│   │   ├── VoidTankBlock.java
│   │   └── CreativeTankBlock.java
│   ├── tiles/
│   │   ├── TankTile.java
│   │   ├── VoidTankTile.java
│   │   └── CreativeTankTile.java
│   └── items/
│       └── UpgradeItem.java     # in-place tier upgrades
└── client/
    └── ClientProxy.java

src/main/resources/
├── mcmod.info                  # mod metadata (version expanded at build time)
└── assets/irontanks/
    ├── lang/                   # translations (en_US plus community languages)
    ├── blockstates/            # one per tank tier + special tanks
    ├── models/{block,item}/    # block + upgrade-item models
    ├── textures/{blocks,items}/
    └── recipes/                # tier-up and upgrade-item crafting recipes
```

### Conventions

- **Tiers are data-driven where possible:** each tank tier has its own blockstate, model, textures, and crafting recipes under `assets/irontanks/`. Adding a tier generally means a block/tile + its asset set + recipes, not new architecture.
- **Special tanks** (`Void`, `Creative`) get their own `Block`/`Tile` subclasses because their fluid behavior differs from the plain tiered tanks.
- **Keep Minecraft-version-specific API usage isolated** so `mc/1.11.2` cherry-picks stay clean (see **Cross-Version Porting**).
- **Config** lives in `IronTanksConfig`; gate behavior changes behind config when it affects servers/packs.

## Code Style

- Formatting is governed by **`.editorconfig`** (there is no Spotless/auto-formatter):
  - UTF-8, LF line endings, final newline, trim trailing whitespace
  - Java: 4-space indentation
- Single-line `if`/`for` is allowed; prefer braces for multi-line bodies.
- Keep nesting depth reasonable (prefer max 3 levels).
- **CI** (`check-code.yml`) runs `./gradlew build` on PRs into `mc/**`; there is no separate format/lint gate beyond the PR-title check.

## Commit Messages

Output a SINGLE-LINE commit subject only:
- No conventional-commit prefix (no `feat:`, `fix:`, etc.)
- No body, no co-author trailer
- Imperative mood ("Add", "Fix", "Refactor")
- Aim for ≤ 72 characters
- Be specific about what changed

**Note:** individual commits stay plain; the **PR title** carries the conventional-commit type that release-please consumes. This keeps history readable and squash-merges clean.

## Pull Requests

Use conventional-commit format for PR titles, **without a scope**:

```text
<type>: <description>
```

- **No scope.** Iron Tanks is small and focused — scopes add noise without value. (`requireScope: false` is set in `check-pr.yml`; a scope is tolerated but should not be added.)
- The subject must **start with a lowercase letter** (enforced by `check-pr.yml`).

Examples:

```text
feat: add emerald tank
fix: prevent tanks from losing fluid on save
perf: reduce creative-tank tick cost
chore: bump gradle wrapper to 4.10.3
```

**PR body should read like release notes:**
- Focus on WHAT changed and WHY it matters to players
- Short sections: Summary / Changes / Notes
- Bullet points, grouped and scannable
- No low-level implementation detail unless it affects behavior or compatibility

### Release notes and PR title strategy

Release notes are for **players**, not developers. Prefer wording that describes the player-visible effect of the change.

**Changelog-visible types** (the only ones that appear in player-facing release notes — see `release-please-config.json`):

| Type | Changelog section | Use for |
|---|---|---|
| `feat` | Added | New player-visible behavior |
| `fix` | Fixed | Player-visible bug fixes |
| `perf` | Improved | Player-visible performance improvements |

**Internal types** (allowed in PR titles, but kept out of the changelog):

| Type | Use for |
|---|---|
| `refactor` | Code cleanup without player-visible behavior changes |
| `test` | Test coverage |
| `build` | Gradle / build-system changes |
| `ci` | GitHub Actions / automation changes |
| `chore` | Maintenance work (incl. dependency bumps) |
| `docs` | Documentation-only changes |
| `revert` | Revert a previous change |

(`check-pr.yml` accepts exactly: `feat`, `fix`, `docs`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`, `revert`.)

**Guidance for agents creating/modifying PRs:**
- Use a no-scope conventional-commit title.
- Think about the generated changelog before choosing the title — prefer player-facing wording for `feat`/`fix`/`perf`.
- Use internal types (`refactor`/`test`/`build`/`ci`/`chore`) for non-player-facing work so they stay out of the changelog.

## CI / Automation

GitHub Actions in `.github/workflows/`:

- **`check-pr.yml`** — validates the PR title (no-scope conventional commit, lowercase subject) on PRs into `mc/**`.
- **`check-code.yml`** — runs `./gradlew build` on PRs into `mc/**` (JDK 8).
- **`prepare-release.yml`** — runs release-please on pushes to `mc/*`, opening/updating the per-branch release PR.
- **`build-release.yml`** — on a published release (or manual `workflow_dispatch` with a `tag`), builds the JAR and, when `publish` is true, uploads to Modrinth and CurseForge via `mc-publish` (`loaders: forge`, BuildCraft listed as a required dependency).

**Dependabot** (`.github/dependabot.yml`) fans out across all three `mc/*` branches with `chore`-prefixed commits, and pins each branch's toolchain (ForgeGradle 2.x and Gradle <5 on 1.11.2/1.12.2; RetroFuturaGradle 1.4.x and Gradle <9 on 1.7.10) so incompatible major bumps aren't proposed.

## Documentation

- `CLAUDE.md` (this file) — primary development guidance for Claude Code
- `AGENTS.md` — the same guidance for other coding agents (keep in sync with this file)
- `README.md` — user-facing project overview
- `CHANGELOG.md` — auto-generated release notes
- [Iron Tanks wiki](https://github.com/Indemnity83/irontanks/wiki) — tank tiers, capacities, and recipes

**Release configuration:**
- `.release-please-manifest.json` — current version (per branch)
- `release-please-config.json` — release-please configuration (simple release-type, changelog sections)
- `.github/workflows/prepare-release.yml` / `build-release.yml` — release automation
