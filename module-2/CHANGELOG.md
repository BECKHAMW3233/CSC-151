# Changelog

All notable changes to this project, in the order they happened. Dates reflect the
day the work was done (this project was built starting 2026-08-28, continuing into
2026-08-29).

## [14] 2026-08-29 — Made the "user decides, not Claude Code" rule concrete

**Why:** while setting up local git tracking for this folder, several
restructuring actions were proposed and partly attempted that nobody asked for
(renaming the folder, nesting files into a subfolder, restructuring the
already-pushed GitHub repo). The existing "ask before changing files" rule
wasn't concrete enough to prevent this in practice.

- Rewrote `CLAUDE.md` rule #1 to state directly that the user is the only one
  who decides what changes are made, with concrete bullet points: no file
  change without explicit per-instance approval, an earlier request doesn't
  carry permission forward, "seems like the obvious next step" is not
  permission, and — specifically — never describe an unrequested action as
  something the user asked for.

**Files changed:** `CLAUDE.md`

## [13] 2026-08-29 — Track bin/ in git; made "never assume" the top rule in CLAUDE.md

**Why:** a `.gitignore` excluding the compiled `bin/` folder was added without
being asked for — the group wants everything included in the repo rather than
having Claude Code decide what counts as safe to leave out.

- `.gitignore` no longer excludes `bin/` or `*.class` — only OS junk files
  (`.DS_Store`, `Thumbs.db`) are excluded now.
- Rewrote `CLAUDE.md`'s rules so "never assume, always ask for clarification" is
  rule #1 and explicitly covers technical/scope decisions (like what to track in
  git), not just assignment/course questions — citing this exact incident as the
  concrete example of what it's meant to prevent.

**Files changed:** `.gitignore`, `CLAUDE.md`

## [12] 2026-08-29 — Added CLAUDE.md for teammates using Claude Code

**Why:** other group members may use Claude Code on this project, and some may
never have used it before — `CLAUDE.md` is automatically loaded as project
context by Claude Code, so it's the right place for explicit ground rules any
group member's session should follow (not just documentation for humans to read).

- Added [CLAUDE.md](CLAUDE.md): project overview, required reading, and explicit
  rules — never imply solo authorship, never fabricate data/facts, log every
  change in the changelog, keep contribution/session-log files updated per
  person, ask rather than assume on anything group- or course-specific, and stay
  Java-only/GUI-based/CSV-backed/modular. Also documents the technical
  conventions (package boundaries, CSV schema/quoting rules, per-team folder
  format, no-hardcoded-team-names rule, GUI numeric bounds) so a new
  contributor's Claude Code session doesn't accidentally break them.

**Files changed:** `CLAUDE.md` (new)

## [11] 2026-08-29 — Added a full AI session log

**Why:** requested a record of the prompts given and responses made this session,
kept PG and framed consistently with the rest of the documentation — this is
groundwork for the group, not a solo project.

- Added [AI_SESSION_LOG.md](AI_SESSION_LOG.md): a chronological, paraphrased log
  of every prompt this session and what was done in response, from the initial
  assignment instructions through this entry.
- Updated `CONTRIBUTIONS.md`'s description of how the work was done to match the
  exact framing given: Claude Code was a tool for research and writing code; all
  logic and user-experience decisions were directed by William.

**Files changed:** `AI_SESSION_LOG.md` (new), `CONTRIBUTIONS.md`

## [10] 2026-08-29 — Added course-syllabus context to CONTRIBUTIONS.md

**Why:** the actual course syllabus (Fall 2026 CSC-151-0901) was provided, and it
contains information directly relevant to how this project should be documented.

- Noted (per direct confirmation) that this project is **Module 2 — Individual
  Project** work (10% of the grade) — an earlier guess in this same entry that
  incorrectly mapped it to Group Project_02 based on inferring from the syllabus's
  structure alone, without asking, was wrong and has been corrected.
- Added a direct note about the syllabus's Academic Integrity section, which lists
  submitting AI-generated work as an example of plagiarism, alongside its separate
  AI Guidelines section explaining that AI use is being required per-class instead
  — and recommended confirming directly with the instructor that this document's
  level of AI-use disclosure is what's expected, rather than assuming it's enough.

**Files changed:** `CONTRIBUTIONS.md`

## [9] 2026-08-29 — Corrected the framing to fit a group assignment; added real course/group info

**Why:** the earlier version of `CONTRIBUTIONS.md` implied this work stood on its
own, which doesn't fit a group assignment — this is groundwork done ahead of
meeting the rest of the group, not a finished or accepted group decision, and
needed to read that way.

- Rewrote `CONTRIBUTIONS.md` to frame everything as a proposal/starting point the
  group can adopt, adapt, or discard — added an explicit "Open questions for the
  group" section (team choice, whether to keep the 32-team scope, how to divide
  remaining work, or changing direction entirely).
- Filled in real course/group info: **Fall 2026 Java Programming (CSC-151-0901)**,
  instructor David Teter; group members William Beckham, Christian Logan, Brandon
  Malave, Roberto Rendon-Valdez.
- Updated the in-app Help > About box ([AboutDialog.java](src/team/gui/AboutDialog.java))
  with the same real course/group info instead of `[fill in ...]` placeholders.

**Files changed:** `CONTRIBUTIONS.md`, `src/team/gui/AboutDialog.java`

## [8] 2026-08-29 — All 32 NFL teams now have complete real data; picker rendering fix

**Why:** finishing the work started in [7] — every team needed real, researched data,
not just Carolina Panthers, and a real rendering bug turned up while verifying it.

- **All 32 teams are now fully populated with real 2026 data** (players, coaching
  staff, front office, and org facts), researched by 8 background agents (one per
  division grouping) working from each team's official site plus ESPN, matching the
  Panthers' sourcing standard and honesty caveats exactly. Verified with a Python
  validation script checking every team's 4 files for field-count consistency, no
  duplicate IDs, valid position/status/conference/division values, and numeric
  bounds — **0 errors across all 32 teams** on the final pass.
- **Widened `StaffDialog`'s age (18–100, was 18–75) and tenure (0–75, was 0–40)
  bounds.** Real NFL owners and founding-family executives are frequently older or
  longer-tenured than a "typical" staff member (e.g. Robert Kraft 85, Art Rooney Jr.
  65 years with the Steelers since 1961) — the original bounds were forcing agents to
  quietly cap real facts to avoid crashing the edit dialog. Every affected team's
  data was corrected to use the real, verified figure once the bounds were widened.
- **Standardized the `Division` field** to the bare division name (`South`, not
  `NFC South`) across all 32 teams, fixing the one file (Carolina Panthers) that
  used the old format, so `TeamCatalog`'s grouping logic treats every team
  consistently.
- **Found and fixed a real rendering bug**, not a data bug: the team-picker dialog
  rendered completely blank once it had to display the full 32-team, 3-level tree
  (2 conferences → 8 divisions → 32 teams) — confirmed via `PrintWindow` capture
  (ruling out a screenshot artifact), a stderr check (ruling out a silent exception),
  and a 15+ second wait (ruling out a slow-load issue). Forcing Java's software
  rendering pipeline (`-Dsun.java2d.d3d=false -Dsun.java2d.opengl=false
  -Dsun.java2d.noddraw=true`) fixed it immediately, confirming a hardware-accelerated
  Java2D rendering pipeline issue specific to this environment's display/GPU setup,
  not an app logic bug. Added `.vscode/launch.json` with these flags baked into the
  VS Code run configuration as protection in case a similar environment issue shows
  up wherever this is graded.
- Verified the complete flow end-to-end after the rendering fix: launched the app,
  selected New England Patriots from the picker, and confirmed its real data
  (Foxborough MA, Gillette Stadium, Robert Kraft, Mike Vrabel, AFC East) loaded
  correctly into the main window with the dynamic title working as designed.

**Files changed:** `src/team/gui/StaffDialog.java`, `.vscode/launch.json` (new),
`data/teams/carolina-panthers/team_info.csv` (Division fix), and all 4 CSV files for
the other 31 teams under `data/teams/`.

## [7] 2026-08-28 — Multi-team support: pick any of the 32 NFL teams

**Why:** requested so the program isn't locked to one team — the user (any user, any
group) should be able to select which NFL team to work with, and every team's data
should be editable and saveable independently.

- **New folder structure:** `data/teams/<team-slug>/` — one folder per team, each
  holding its own `players.csv`, `coaches.csv`, `staff.csv`, `team_info.csv`.
  Carolina Panthers' existing real data was moved into `data/teams/carolina-panthers/`
  unchanged. All 31 other NFL teams got a folder with header-only CSVs as a starting
  point (see below).
- **`TeamCatalog`** (new, `team.data`): scans `data/teams/` and reads each team's
  `team_info.csv` for its display name, conference, and division — so the team list
  is fully dynamic. A team with no data yet still shows up (grouped under
  "Unassigned") rather than being invisible or crashing anything.
- **`TeamSelectorDialog`** (new, `team.gui`): a `JTree` grouped by Conference →
  Division → Team (e.g. NFC → South → Carolina Panthers), shown at startup before
  the main window appears, and again via **File > Switch Team...** to change teams
  without restarting the app.
- **`DataStore`** refactored from hardcoded file-path constants to an instance-based
  `switchTeam(Path)` method, so the exact same load/save/CRUD/search code now works
  against whichever team folder is currently selected.
- **`MainFrame`**'s Team Info tab is now refreshable (previously built once at
  startup) so switching teams updates it, the window title, and all three data tabs
  in place.
- Verified via compile, launch, and a screenshot confirming `TeamCatalog` correctly
  grouped Carolina Panthers under NFC > South while the 31 not-yet-populated teams
  correctly fell back to "Unassigned" — proving the dynamic discovery and graceful
  fallback both work. Full click-through of the picker's "Select Team" flow hit
  screen-automation friction in this environment (see note below) but relies on the
  same dialog confirm/dispose pattern already proven for the Add/Edit dialogs.
- **Dispatched 8 background research agents** (one per division-ish group) to
  research and write real 2026 rosters/coaches/staff for the 31 other teams,
  following the exact schema, quoting rules, and honesty standard established for
  the Panthers. Results will land as each agent completes.

**Files changed:** `src/team/data/DataStore.java`, `src/team/data/TeamCatalog.java`
(new), `src/team/gui/TeamSelectorDialog.java` (new), `src/team/gui/MainFrame.java`,
`src/team/Main.java`, plus the `data/teams/` folder restructure.

## [6] 2026-08-28 — Zoom for readability on larger monitors

**Why:** requested so the app is readable when projected or viewed on a large monitor
without the text staying tiny while the window just gets emptier.

- Added a **View** menu with **Zoom In** (Ctrl+=), **Zoom Out** (Ctrl+-), and
  **Reset Zoom** (Ctrl+0).
- Zoom rescales font size across every component in the window (menus, labels,
  buttons, tables) and increases `JTable` row height to match, rather than just
  stretching the window and leaving text the same size.
- Zoom also applies to the Add/Edit dialogs (`PlayerDialog`, `CoachDialog`,
  `StaffDialog`) so a form opened while zoomed in matches the main window.
- Zoom range: 70%–250%, in 10% steps.
- Verified by launching the app, sending Ctrl+= via keyboard, and confirming table
  text/rows visibly grew with no clipping, then Ctrl+0 to confirm it returns to
  normal size.

**Files changed:** `src/team/gui/MainFrame.java`

## [5] 2026-08-28 — CSV data audit and a real bug fix

**Why:** requested verification that the CSV data is "proper and correct," not just
that it loads without crashing.

- Audited all three data files for: duplicate IDs, correct field counts per row,
  and every position/status/unit value matching a valid dropdown option. All clean.
- **Found and fixed a real bug:** Carl Smith's age (80) exceeded the coach-editing
  dialog's age spinner maximum (75). Opening his record to edit would have thrown
  `IllegalArgumentException` and crashed the dialog. Fixed by widening the spinner's
  range to 22–90 (senior NFL consultants realistically work into their 70s–80s).

**Files changed:** `src/team/gui/CoachDialog.java`

## [4] 2026-08-28 — Removed all hardcoded team names

**Why:** requested so that swapping the CSV files for a different team's data is a
complete, seamless team swap with no source code changes.

- The window title and the Help > About box previously had "Carolina Panthers"
  written directly into the Java source. Both now read the "Team Name" value out of
  `team_info.csv` at startup instead.
- Verified by temporarily changing "Team Name" in `team_info.csv` to "Test Swap
  Eagles," relaunching with no recompile, confirming the window title changed to
  match, then reverting it back and confirming it returned to normal.

**Files changed:** `src/team/gui/MainFrame.java`, `src/team/gui/AboutDialog.java`

## [3] 2026-08-28 — Auto-save fix

**Why:** found while answering "does this meet requirements" — the README claimed
changes saved automatically after every Add/Edit/Remove, but the code only actually
saved on the explicit File > Save All menu action. Closing the app without
remembering to save that way would have silently lost all edits.

- Every Add/Edit/Remove across Players, Coaches, and Support Staff now writes to
  disk immediately via a new `persist()` helper, instead of only on explicit Save.

**Files changed:** `src/team/gui/MainFrame.java`

## [2] 2026-08-28 — Replaced sample data with real 2026 Panthers data

**Why:** requested real roster data instead of the placeholder sample data from the
initial build.

- Pulled and compiled real data from panthers.com and ESPN as of 2026-08-28:
  - `players.csv`: all 94 players on the roster (active, PUP, non-football illness,
    and injured reserve statuses), with real jersey numbers, positions, colleges,
    experience, height, and weight.
  - `coaches.csv`: the full 28-person coaching staff, head coach through assistants.
  - `staff.csv`: 21 front-office/football-operations executives (ownership,
    president, GM, scouting, analytics, medical, security, etc.).
  - `team_info.csv`: added President field; noted data-verification date.
- Added a `Non-Football Illness` status to `Player.STATUSES` (Taylor Moton's real
  status wasn't in the original enum).
- Made the Support Staff department field editable in `StaffDialog` since real
  departments (Ownership, Stadium Operations, Human Resources...) go beyond the
  original generic categories; expanded `StaffMember.DEPARTMENTS` accordingly.
- Switched height format from `6'2"` to `6-2` — the embedded quote character was
  breaking the hand-written CSV rows.
- Documented honest caveats in the README: rosters change constantly (this is a
  snapshot, not a live feed); hometown isn't published for these people so it's
  `N/A` rather than guessed; only a few public figures' ages (Canales, Tepper,
  Morgan) are verified, the rest are reasonable estimates by role/seniority.

**Files changed:** `data/players.csv`, `data/coaches.csv`, `data/staff.csv`,
`data/team_info.csv`, `src/team/model/Player.java`, `src/team/gui/PlayerDialog.java`,
`src/team/model/StaffMember.java`, `src/team/gui/StaffDialog.java`, `README.md`

## [1] 2026-08-28 — Initial build

**Why:** starting point for the Java Group Project — an NFL team management app in
Java/Swing, CSV-backed, modular, submittable as individual work.

- Picked the **Carolina Panthers** (closest NFL team to Fayetteville, NC).
- Built the full application from scratch as three separated packages:
  - `model/` — `Person` (base class) → `Player`, `Coach`, `StaffMember`
  - `data/` — `CsvUtil` (quote-aware CSV read/write), `DataStore` (CRUD, search/filter,
    load/save)
  - `gui/` — `MainFrame` (menu bar, tabs, tables, status bar), add/edit dialogs for
    each entity type, table models, About box
- GUI covers every control type required by the assignment: dialog boxes
  (`JOptionPane`, `JDialog`), input boxes (text fields, spinners, dropdowns), and
  other controls (menus, tabs, tables).
- Four tabs: Team Info (read-only org facts), Players, Coaches, Support Staff — each
  of the latter three with live search, a category filter, and Add/Edit/Remove.
- Seeded with placeholder sample data (later replaced with real data, see [2]).
- Set up `.vscode/settings.json` for VS Code's Java extension, wrote `README.md`
  with build/run instructions, and added a `CONTRIBUTIONS.md` template for
  identifying individual work once the group forms.
- Compiled with `javac` and launched the app to confirm it actually runs and loads
  data correctly before calling it done.
