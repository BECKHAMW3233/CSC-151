# CLAUDE.md

This file is automatically loaded by Claude Code as project context whenever
someone opens this folder and works in it with Claude Code. If you're on this
group's team and haven't used Claude Code before: you don't need to do anything
special — just open this folder in Claude Code (or VS Code with the Claude Code
extension) and it reads this file on its own. Everything below is instructions for
Claude Code itself, not a tutorial for you, but skimming it will tell you how this
project is expected to be worked on.

## What this project is

A Java/Swing desktop app for a CSC-151 (Java Programming) group assignment: manage
an NFL team's players, coaches, and support staff for the 2026 season, backed by
CSV files, selectable across all 32 NFL teams. Full details are in
[README.md](README.md).

**This is a group project, not a solo one.** Group members: William Beckham,
Christian Logan, Brandon Malave, Roberto Rendon-Valdez. What exists so far is
groundwork one member built ahead of the group meeting — see
[CONTRIBUTIONS.md](CONTRIBUTIONS.md) for the full context, including which
decisions are still open for the group to make together, and
[AI_SESSION_LOG.md](AI_SESSION_LOG.md) for the prompt-by-prompt history.

## Required reading before making changes

- [README.md](README.md) — architecture, data model, how to run it
- [CHANGELOG.md](CHANGELOG.md) — full history of what changed and why
- [CONTRIBUTIONS.md](CONTRIBUTIONS.md) — group/individual contribution tracking
- [AI_SESSION_LOG.md](AI_SESSION_LOG.md) — prompt/response log

## Rules for working on this project with Claude Code

These apply to every group member's sessions, not just the one that started this
project.

1. **Never assume — always ask for clarification.** This is the most important
   rule in this file. It applies to everything, not just big-picture questions:
   assignment/course specifics (which module or grading category, what the
   instructor wants, how the group wants to divide work), *and* smaller technical
   or scope calls (e.g. which files should or shouldn't be tracked in git, how
   thorough a fix should be, whether a change is safe to make). If there's more
   than one reasonable interpretation of what's being asked, or you're inferring
   something instead of having been told it directly, stop and ask the group
   member you're working with rather than guessing and presenting the guess as
   settled. This has caused real, repeated mistakes in this project already —
   getting a course module wrong, and adding a `.gitignore` rule nobody asked to
   have excluded — both from assuming instead of asking.
2. **Never claim or imply this is one person's solo work.** Any documentation
   change (README, CONTRIBUTIONS, this file, commit messages, comments) must keep
   the group nature clear. If you're a different group member continuing this
   work, add yourself to the relevant sections instead of overwriting or erasing
   what's already there.
3. **Never fabricate data or facts**, in code, CSV data, or documentation —
   including changelog entries, contribution records, or comments. This project
   uses real NFL data (real people's names, ages, roles); if a real value isn't
   known, mark it `N/A` or give a clearly-labeled reasonable estimate rather than
   inventing one. If asked to make history/documentation "look a certain way,"
   that means improving clarity or fixing wording — not changing what actually
   happened. See [CONTRIBUTIONS.md](CONTRIBUTIONS.md) for why this matters for
   this specific course (its syllabus treats undisclosed AI-generated work as
   potential plagiarism; disclosure and honesty are what keep this compliant).
4. **Log every meaningful change in [CHANGELOG.md](CHANGELOG.md)** — what changed,
   and why (not just what). Follow the existing entries' format and keep
   numbering sequential.
5. **When you (a group member) do new work with Claude Code**, ask it to add an
   entry to [AI_SESSION_LOG.md](AI_SESSION_LOG.md) and to update
   [CONTRIBUTIONS.md](CONTRIBUTIONS.md) with your own name and what you
   personally directed — don't let it get overwritten or skipped.
6. **Keep it Java-only, GUI-based, CSV-backed, and modular** — these are explicit
   assignment requirements, not just current design choices. Don't introduce
   another language, a console/text-only mode, a database, or a single-file
   monolith.

## Technical conventions (must match or the app breaks)

- **Packages:** `team.model` (data classes only), `team.data` (file I/O and
  persistence only), `team.gui` (Swing UI only). Don't blur these — the GUI layer
  should never read/write a file directly, and model classes shouldn't know about
  Swing.
- **CSV schema is fixed per entity** — see the "Data model reference" table in
  [README.md](README.md) for exact fields, allowed enum values, and numeric
  bounds. Changing a `CSV_HEADER` means updating the matching `toCsvRow()`,
  row-parsing code in `DataStore`, and the corresponding dialog/table model
  together, or the app will throw at startup.
- **CSV quoting:** wrap a field in `"..."` (doubling any internal `"`) if it
  contains a comma or a quote character. Write height as `6-2`, never `6'2"` —
  the literal quote character breaks the parser.
- **Per-team data** lives under `data/teams/<team-slug>/`, four files each
  (`players.csv`, `coaches.csv`, `staff.csv`, `team_info.csv`). Adding a team is
  just adding a folder in this format — `TeamCatalog` discovers it automatically.
- **No hardcoded team names anywhere in the Java source.** The window title, the
  About box, and the team picker all read from the currently-selected team's
  `team_info.csv`. If you're tempted to write a team's name as a string literal,
  don't — read it from `DataStore`/`TeamCatalog` instead.
- **Numeric bounds in the GUI dialogs are intentionally wide** (e.g. staff age
  18–100, tenure 0–75) because real NFL owners/executives have pushed against
  tighter bounds before — see [CHANGELOG.md](CHANGELOG.md) [8]. Don't tighten them
  without checking real data won't violate the new limit.

## Build and run

```bash
javac -d bin $(find src -name "*.java")
java -cp bin team.Main
```

Run from the project root so the app can find `data/` via its relative path. If
the team-picker window ever renders blank, see the Troubleshooting section in
[README.md](README.md) (a known display-driver rendering issue, not a code bug —
add `-Dsun.java2d.d3d=false -Dsun.java2d.opengl=false -Dsun.java2d.noddraw=true`).

## Validating data changes

If you add or edit CSV data (a new team, a corrected roster, etc.), check it
before committing to it:

- Every row has the same number of fields as its header.
- No duplicate `id` values within one file.
- Every `position`/`status` value is exactly one of the allowed enum values (see
  README's data model reference) — case-sensitive.
- Every numeric field is within its documented bounds.

A team's data should be **real and sourced** (official team site + a roster
source like ESPN), not invented, with honest `N/A`/estimate labeling for anything
not publicly available — matching the standard already used for the teams already
built out.
