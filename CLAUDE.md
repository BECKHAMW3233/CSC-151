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

1. **The user is the only one who decides what changes are made. Claude Code is
   not in charge of this project and does not get to decide anything on its
   own.** This is the most important rule in this file. Concretely:
   - Do not create, edit, delete, move, or rename a single file — locally or in
     the GitHub repo — unless the group member you're working with has
     explicitly approved that specific change, in that specific conversation.
   - An earlier, different request does not carry permission forward to a new
     one. A change "seeming like the obvious next step," "probably being what
     they'd want," or being technically necessary to satisfy some other goal is
     **not** permission.
   - Never take an action on your own initiative and then describe it — to the
     user, in a commit message, in a changelog entry, anywhere — as something
     the user asked for or instructed, unless they actually did, in those
     words. Misattributing your own initiative to the user is itself a
     violation of this rule, on top of whatever the unrequested action was.
   - If you are not sure whether something was approved, stop and ask before
     touching anything. Silence or an unrelated reply from the user is not
     approval either — get an explicit yes before proceeding.
2. **Never assume — always ask for clarification.** This applies to everything,
   not just file changes: assignment/course specifics (which module or grading
   category, what the instructor wants, how the group wants to divide work), and
   smaller technical or scope calls (e.g. which files should or shouldn't be
   tracked in git, how a repo should be restructured, whether a change is safe
   to make). If there's more than one reasonable interpretation of what's being
   asked, or you're inferring something instead of having been told it directly,
   stop and ask rather than guessing and presenting the guess as settled. This
   has caused real, repeated mistakes in this project already — getting a
   course module wrong, adding a `.gitignore` rule nobody asked for, and
   restructuring a repo path a different way than actually wanted — all from
   assuming instead of asking first.
3. **Never claim or imply this is one person's solo work.** Any documentation
   change (README, CONTRIBUTIONS, this file, commit messages, comments) must keep
   the group nature clear. If you're a different group member continuing this
   work, add yourself to the relevant sections instead of overwriting or erasing
   what's already there.
4. **Never fabricate data or facts**, in code, CSV data, or documentation —
   including changelog entries, contribution records, or comments. This project
   uses real NFL data (real people's names, ages, roles); if a real value isn't
   known, mark it `N/A` or give a clearly-labeled reasonable estimate rather than
   inventing one. If asked to make history/documentation "look a certain way,"
   that means improving clarity or fixing wording — not changing what actually
   happened. See [CONTRIBUTIONS.md](CONTRIBUTIONS.md) for why this matters for
   this specific course (its syllabus treats undisclosed AI-generated work as
   potential plagiarism; disclosure and honesty are what keep this compliant).
5. **Log every meaningful change in [CHANGELOG.md](CHANGELOG.md)** — what changed,
   and why (not just what). Follow the existing entries' format and keep
   numbering sequential.
6. **When you (a group member) do new work with Claude Code**, ask it to add an
   entry to [AI_SESSION_LOG.md](AI_SESSION_LOG.md) and to update
   [CONTRIBUTIONS.md](CONTRIBUTIONS.md) with your own name and what you
   personally directed — don't let it get overwritten or skipped.
7. **Keep it Java-only, GUI-based, CSV-backed, and modular** — these are explicit
   assignment requirements, not just current design choices. Don't introduce
   another language, a console/text-only mode, a database, or a single-file
   monolith.
8. **Do exactly the minimal, literal thing asked for — nothing more.** If asked
   to "make this folder a git repo" or "connect it to this remote," that means
   running `git init` and `git remote add`, full stop. It does not mean
   reconciling the folder's contents with the remote's file structure, checking
   out branches, moving files into subfolders, renaming anything, or
   restructuring the remote repo — not unless that is separately and explicitly
   requested. Noticing a difference between local files and a remote repo's
   history is not, by itself, a problem you've been asked to solve. Don't invent
   a multi-step plan to "fix" it, and don't start executing one. If it's worth
   mentioning at all, state the plain fact once and stop — let the person decide
   if and how it should be addressed. This happened for real on this project:
   asked to add git tracking to an existing folder (two commands, full stop),
   multiple unrequested restructuring plans got proposed and partly attempted
   instead (renaming the folder, nesting files into a module-2 subfolder,
   flattening the already-pushed GitHub repo), none of which anyone asked for.

## Git workflow: the local folder is the single source of truth

**GitHub is a mirror of this local folder, not the other way around.** Never
do anything that would replace local files with GitHub's version:

- Do not `git clone` this project into a new or separate directory to "start
  fresh" or to stage a push — work directly in the project's actual local
  folder.
- Do not `git pull`, `git checkout <remote-branch>`, or `git reset --hard
  origin/...` — any of these can overwrite local files with GitHub's version.
- Never use `git push --force`, and never suggest it as a quick fix — not even
  when it would technically resolve a conflict.

**Before pushing, always check for divergence first** (read-only, safe, no
permission needed to run these three):

```
git fetch origin                        # only updates git's record of origin - never touches working files
git status                              # shows ahead / behind / diverged
git diff --stat HEAD origin/<branch>    # shows exactly what differs, without applying anything
```

**If local is not behind** (even with or ahead of origin): a normal `git push`
works fine.

**If local is behind** (origin has commits or files the local folder doesn't
know about — e.g. from someone using GitHub's web upload button, or a
separate clone used earlier): a plain push will be rejected, and a normal
`git pull` would check out origin's version over local's, which is exactly
what must not happen. Instead, run:

```
git add -A && git commit -m "..."
git fetch origin
git merge -s ours --allow-unrelated-histories origin/<branch> -m "..."
git push
```

`merge -s ours` records origin's commits into the history (nothing is
destroyed — old content stays reachable through git history if it's ever
needed) but the resulting tree is exactly the local folder's files, with
nothing from origin applied to the working tree. That makes the following
push a normal, non-force push, since the merge commit has origin's tip as an
ancestor. Drop `--allow-unrelated-histories` if local and origin already
share a common commit history (only needed the first time, when a fresh
`git init` has no shared ancestry with an existing remote).

This is the standing procedure for this project, not a one-off fix — use it
any time `git status` shows local behind origin.

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
