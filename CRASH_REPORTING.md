# Crash Reporting & Privacy

Iron Tanks can send **sanitized crash reports** to help us find and fix bugs you hit in the wild.
It is **opt-in and off by default** — nothing is ever sent until an operator turns it on.

## TL;DR

- **Off by default.** Fresh installs send nothing.
- **Opt-in per world/server**, via `/irontanks diagnostics enable`. Turn it back off any time with
  `/irontanks diagnostics disable`.
- **Only Iron Tanks' own errors** are captured — never another mod's or vanilla Minecraft's.
- **Personal info is stripped** before anything leaves your machine (see below).
- Reports go to [Sentry](https://sentry.io/), a standard crash-reporting service.

## How to turn it on or off

You need operator/gamemaster permission. In chat or the server console:

| Command | What it does |
|---|---|
| `/irontanks diagnostics` | Show whether reporting is currently ON or OFF |
| `/irontanks diagnostics enable` | Start sending sanitized crash reports |
| `/irontanks diagnostics disable` | Stop sending crash reports |
| `/irontanks diagnostics preview` | Print a sample of exactly what a report looks like — **nothing is sent** |
| `/irontanks diagnostics notify off` | Hide the join-time reminder for operators |
| `/irontanks diagnostics notify on` | Show the join-time reminder again |

Your choice is saved to `config/irontanks.json` and persists across restarts.

## What is collected (only when enabled)

- The error itself: exception type, message, and stack trace from Iron Tanks code.
- Versions: the Iron Tanks version, Minecraft version, loader (Fabric/NeoForge), and Java/OS family.

## What is **never** collected

- Player names, UUIDs, or IP addresses
- Server names/hostnames
- Chat messages, world data, coordinates, or builds
- The contents of your config files
- Anything that looks like a password, token, API key, or secret

## How sanitizing works

Before any report leaves your machine, the message and exception text are scrubbed — biased toward
over-redaction (when in doubt, it strips):

- Your home directory and username → `~` / `<user>`
- Generic `…/Users/<name>`, `…/home/<name>`, and `C:\Users\<name>` paths → redacted
- UUIDs → `<uuid>`
- IPv4 addresses → `<ip>`
- `password=…`, `token=…`, `secret=…`, `api_key=…`, `dsn=…` pairs → `<redacted>`

Stack frames carry only source file names, not full paths, so they need no scrubbing. Run
`/irontanks diagnostics preview` to see the result for yourself before opting in.

## Technical notes

- The DSN (the public ingest key) is embedded in the mod. DSNs are **write-only** and designed to
  ship in client software — it can submit reports but can't read anything back.
- Iron Tanks uses its own dedicated Sentry client and never touches the global Sentry SDK, so it
  can't interfere with any other mod that also uses Sentry.
- Self-hosting Sentry? Set `crashReporting.dsnOverride` in `config/irontanks.json` to your own DSN.
