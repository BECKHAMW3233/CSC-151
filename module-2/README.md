# NFL Team Management (Java/Swing)

A Java Swing desktop application for managing a professional football organization's
people — players, coaches, and support staff — for the 2026 season. Any of the 32 NFL
teams can be selected at startup (or switched later via **File > Switch Team**); data
for every team lives in its own set of plain CSV files so it's easy to inspect, edit
by hand, or hand off between teammates.

**All 32 NFL teams have complete, real 2026 data** — every team's players, coaching
staff, and front office were researched from that team's official site plus ESPN.
The Carolina Panthers (closest NFL team to Fayetteville, NC) were the first team
built and remain the most thoroughly cross-checked.

## Contents

1. [Assignment requirements checklist](#assignment-requirements-checklist)
2. [Features](#features)
3. [The 32 teams](#the-32-teams)
4. [Project structure and architecture](#project-structure-and-architecture)
5. [Data model reference](#data-model-reference)
6. [Zero hardcoded team names](#zero-hardcoded-team-names)
7. [About the data](#about-the-data)
8. [How to run it](#how-to-run-it)
9. [Using the app](#using-the-app)
10. [Keyboard shortcuts](#keyboard-shortcuts)
11. [Extending it](#extending-it)
12. [Known limitations](#known-limitations)
13. [How this was verified](#how-this-was-verified)
14. [FAQ](#faq)

## Assignment requirements checklist

| Requirement | How it's met |
|---|---|
| Pick 1 NFL team for the group | Carolina Panthers is the flagship team, but the app supports switching to any of the 32 |
| Java program, interact with the team using 2026 season info | Full Swing desktop app, real 2026 data |
| Covers Players | [Player.java](src/team/model/Player.java) + Players tab |
| Covers Coaches | [Coach.java](src/team/model/Coach.java) + Coaches tab |
| Covers Support Staff | [StaffMember.java](src/team/model/StaffMember.java) + Support Staff tab |
| "Other people the group decides to include" | Ownership and front-office executives folded into Support Staff |
| Completed in VS Code | `.vscode/settings.json` + `.vscode/launch.json` configure the Java extension |
| Java only, no other language | Entire project is plain Java, nothing else |
| GUI: dialog boxes, input boxes, other controls | `JOptionPane`, `JDialog` forms, `JTextField`, `JComboBox`, `JSpinner`, `JTable`, `JTree`, `JTabbedPane`, `JMenuBar` |
| AI used as a resource | Built with Claude (Anthropic) as a coding assistant — see [CONTRIBUTIONS.md](CONTRIBUTIONS.md) and the full [AI_SESSION_LOG.md](AI_SESSION_LOG.md) for how |
| CSV files | Every entity is CSV-backed, per-team |
| Modular | `model` / `data` / `gui` packages, described below |
| Individual work identified for Canvas | See [CONTRIBUTIONS.md](CONTRIBUTIONS.md) |

## Features

- **32-team picker** — a `JTree` grouped by Conference then Division (e.g. NFC >
  South > Carolina Panthers), shown at startup and reachable anytime via **File >
  Switch Team**.
- **Four tabs per team**: Team Info (read-only org facts), Players, Coaches, Support
  Staff.
- **Search + filter** on every roster tab: a live text search box plus a
  category dropdown (position / unit / department).
- **Add / Edit / Remove** for every person type, each backed by a modal form with
  text fields, spinners (bounded to sane ranges), and dropdowns.
- **CSV persistence**: every change auto-saves immediately, plus explicit **File >
  Save All** / **File > Reload From Files**.
- **Zoom**: **View > Zoom In/Out/Reset** (Ctrl+= / Ctrl+- / Ctrl+0) scales font size
  and table row height across the whole window, including open dialogs — useful on
  a large monitor or projector.
- **Per-team color theme**: the whole window (panels, tabs, tables, buttons,
  dialogs) re-colors to match whichever team is selected, driven entirely by the
  `Primary Colors` field already in that team's `team_info.csv` (see
  [TeamTheme.java](src/team/gui/TeamTheme.java)). No logo images are used —
  real team logos were considered and skipped after finding no freely-licensed
  source of official NFL team logos exists for use in a public repository.
- **Zero hardcoded team data** — swapping a team's 4 CSV files is a complete team
  swap with no code changes (see below).

## The 32 teams

| AFC East | AFC North | AFC South | AFC West |
|---|---|---|---|
| Buffalo Bills | Baltimore Ravens | Houston Texans | Denver Broncos |
| Miami Dolphins | Cincinnati Bengals | Indianapolis Colts | Kansas City Chiefs |
| New England Patriots | Cleveland Browns | Jacksonville Jaguars | Las Vegas Raiders |
| New York Jets | Pittsburgh Steelers | Tennessee Titans | Los Angeles Chargers |

| NFC East | NFC North | NFC South | NFC West |
|---|---|---|---|
| Dallas Cowboys | Chicago Bears | Atlanta Falcons | Arizona Cardinals |
| New York Giants | Detroit Lions | **Carolina Panthers** | Los Angeles Rams |
| Philadelphia Eagles | Green Bay Packers | New Orleans Saints | San Francisco 49ers |
| Washington Commanders | Minnesota Vikings | Tampa Bay Buccaneers | Seattle Seahawks |

Each team's folder slug under `data/teams/` is its name lowercased and
hyphenated (e.g. `kansas-city-chiefs`, `san-francisco-49ers`).

## Project structure and architecture

```
src/team/
  Main.java                  entry point — shows the team picker, loads data, shows the main window
  model/
    Person.java               abstract base class (id, name, age, hometown)
    Player.java                player-specific fields (jersey #, position, status, ...)
    Coach.java                 coach-specific fields (title, unit, years with team, ...)
    StaffMember.java           support-staff fields (department, title, ...)
  data/
    CsvUtil.java                generic CSV read/write (quote-aware)
    TeamCatalog.java            scans data/teams/ and groups teams by conference/division
    DataStore.java              in-memory lists + CRUD + search/filter + CSV load/save,
                                 pointed at whichever team folder is currently selected
  gui/
    MainFrame.java               main window: menu bar, tabs, tables, status bar, zoom
    TeamSelectorDialog.java      startup/Switch-Team picker (JTree grouped by division)
    PlayerDialog.java           add/edit form for a player
    CoachDialog.java            add/edit form for a coach
    StaffDialog.java            add/edit form for a staff member
    AboutDialog.java            Help > About box
    TeamTheme.java              per-team color theming (Primary Colors -> Nimbus L&F colors)
    PlayerTableModel.java       table models backing each JTable
    CoachTableModel.java
    StaffTableModel.java
data/
  teams/
    carolina-panthers/          players.csv, coaches.csv, staff.csv, team_info.csv
    buffalo-bills/               same 4 files, one folder per NFL team
    ... (32 team folders total)
.vscode/
  settings.json                tells the Java extension where src/bin are
  launch.json                  Run configuration (includes a display-driver safety flag, see Troubleshooting)
```

**Why three packages?** Each one is a self-contained layer with a single job:

- **`model/`** — plain data classes only (`Person` → `Player`/`Coach`/`StaffMember`).
  This is the OOP layer: inheritance (shared fields live in `Person`), encapsulation
  (private fields + getters/setters), and each subclass owns its own `CSV_HEADER`
  and `toCsvRow()`/parsing logic.
- **`data/`** — all file I/O and persistence (`CsvUtil`, `TeamCatalog`, `DataStore`).
  The GUI never touches a file directly; it only calls `DataStore` methods.
- **`gui/`** — all Swing code (windows, dialogs, table models, event handling). No
  class in this package reads or writes a file directly.

None of these layers know the internal details of the others beyond their public
methods, so each one compiles and can be understood on its own — which is also why
this splits cleanly for a group: one person could own `model/`, another `data/`,
another `gui/`, and each of you could point at your own package as your individually
identifiable contribution.

**Why per-team folders instead of one shared set of CSVs?** So that switching teams
is just pointing `DataStore` at a different folder (`DataStore.switchTeam(Path)`) —
no merging, no team-ID column needed in every row, and a teammate can safely add or
edit one team's files without any risk of touching another team's data.

## Data model reference

Every person in the app is one of three subclasses of `Person`:

| Field (on `Person`) | Type | Notes |
|---|---|---|
| `id` | String | e.g. `P001`, `C001`, `S001` — unique within that team's file, not globally |
| `firstName` / `lastName` | String | |
| `age` | int | |
| `hometown` | String | `N/A` for almost everyone — not published by the data sources used |

| `Player` adds | Type | Allowed/bounded values |
|---|---|---|
| `jerseyNumber` | int | 0–99 |
| `position` | String | `QB RB FB WR TE OT OG C DE DT LB CB S K P LS` |
| `college` | String | |
| `yearsExperience` | int | 0–25 |
| `status` | String | `Active`, `Injured Reserve`, `Non-Football Illness`, `Practice Squad`, `Physically Unable to Perform`, `Suspended`, `Free Agent` |
| `height` | String | hyphenated format, e.g. `6-2` (not `6'2"` — see below) |
| `weightLbs` | int | 150–400 |

| `Coach` adds | Type | Allowed/bounded values |
|---|---|---|
| `title` | String | free text (e.g. "Defensive Coordinator") |
| `unit` | String | `Administration`, `Offense`, `Defense`, `Special Teams`, `Strength & Conditioning` |
| `yearsWithTeam` | int | 0–40 |
| `yearsExperienceTotal` | int | 0–50 |

| `StaffMember` adds | Type | Allowed/bounded values |
|---|---|---|
| `department` | String | free text, common values: `Ownership`, `Front Office/Executive`, `Football Operations`, `Medical/Athletic Training`, `Equipment`, `Scouting`, `Analytics`, `Strength & Conditioning`, `Media Relations`, `Marketing`, `Business Operations`, `Human Resources`, `Stadium Operations`, `Security` |
| `title` | String | free text |
| `yearsWithOrganization` | int | 0–75 |

The numeric ranges above are enforced by `JSpinner` bounds in each dialog — they're
wide enough for real NFL figures (e.g. a founding-family executive with a
65-year tenure, or an owner in their 80s), widened once during development after
real data exposed the original, tighter bounds (see [CHANGELOG.md](CHANGELOG.md) [8]).

**Why hyphenated height (`6-2`) instead of `6'2"`?** The CSV files are parsed by a
small hand-written, quote-aware parser ([CsvUtil.java](src/team/data/CsvUtil.java)).
A literal `"` character inside an unquoted field breaks it. Hyphenated height avoids
the problem entirely rather than relying on correct quoting in every hand-edited row.

## Zero hardcoded team names

Nothing in the Java source names any specific team anywhere. The window title and
the Help > About box both read the "Team Name" value out of the currently-selected
team's `team_info.csv` at startup (see `buildTitle()` and `teamName()` in
[MainFrame.java](src/team/gui/MainFrame.java)), and `TeamCatalog` reads every team's
conference/division the same way to group them in the picker. Adding a 33rd or
fictional team is a folder change under `data/teams/` — no code changes or
recompiling required. (This was actually tested: the Panthers' "Team Name" value was
temporarily changed to a fake team name, the app was relaunched with no recompile,
and the window title changed to match — then it was reverted.)

## About the data

Each folder under `data/teams/` holds one team's 4 CSV files:
`players.csv`, `coaches.csv`, `staff.csv`, `team_info.csv`. All 32 are complete —
each with a full real roster (80–99 players covering active/PUP/non-football
illness/injured reserve statuses), a real coaching staff (25–38 people), and a real
front office (10–28 people), pulled from that team's official site plus ESPN as of
**August 28–29, 2026** (see each team's own "Data Last Verified" row). Every file
was checked with a validation script for field-count consistency, duplicate IDs,
valid enum values, and numeric bounds — zero errors across all 32 teams.

**Honest caveats, so nothing here is overstated as fact:**
- NFL rosters change constantly (trades, cuts, injuries, practice-squad moves).
  This is a snapshot, not a live feed — re-check the team's official site before a
  graded submission if exact accuracy matters.
- `hometown` isn't published by either source for most people, so it's stored as
  `N/A` rather than guessed.
- `age` for players is real (ESPN publishes it directly). For coaches and staff,
  well-known public figures (head coach, owner, GM, team president, coordinators)
  have real, verified ages; less-public assistant coaches and lower-level staff have
  a reasonable estimate by role/seniority instead, since that isn't published
  anywhere. Names, titles, and departments for every coach/staff member are real and
  sourced either way.
- A handful of teams' `team_info.csv` omits a "President" row where no one
  currently holds that specific title publicly (e.g. an owner also serves as
  president, or the role is vacant/restructured) — this was a deliberate choice to
  avoid inventing a title, not an oversight.
- A few source disagreements (e.g. two players sharing a jersey number across
  ESPN vs. the official site during roster-cutdown week) were left as-sourced
  rather than silently "corrected," since the schema doesn't require jersey numbers
  to be unique — only `id` must be.

You can freely edit any of these CSVs by hand, or use the app's own Add/Edit/Remove
buttons (which rewrite the CSV files for you via **File > Save All**) to keep a
roster current as the season progresses.

## How to run it

### In VS Code

1. Install the **Extension Pack for Java** (Microsoft) if you don't already have it
   — it bundles language support, debugging, and test running.
2. Open this folder (`java football project`) in VS Code: **File > Open Folder...**.
3. Open `src/team/Main.java` and click **Run** (the ▷ button above the `main`
   method), or use **Run > Run Without Debugging**.
   - `.vscode/settings.json` tells the Java extension that `src` is the source
     folder and `bin` is where compiled `.class` files go.
   - `.vscode/launch.json` supplies the run configuration, including a JVM flag
     that avoids a known display-driver rendering issue (see Troubleshooting).
   - **Important:** run it from the project root folder so the app can find the
     `data/` folder using its relative path. The VS Code Run button already does
     this correctly.

### From a terminal instead

From the project root (`E:\java football project`):

```bash
javac -d bin $(find src -name "*.java")
```

If you're in PowerShell instead of Git Bash, use:

```powershell
Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName } | Out-File sources.txt -Encoding utf8
javac -d bin "@sources.txt"
```

Then run it (from the project root, so `data/` resolves correctly):

```bash
java -cp bin team.Main
```

### Troubleshooting: blank/white team-picker window

On some Windows machines (certain GPU/display-driver combinations), Java's
hardware-accelerated rendering pipeline can render the team-picker window
completely blank once it has enough content (32 teams across 8 divisions). This was
actually hit and diagnosed during development — confirmed as a rendering issue (not
an app bug) by capturing the window with `PrintWindow`, checking for a silent
exception, and ruling out a slow-load delay. If it happens, run with software
rendering forced instead:

```bash
java -Dsun.java2d.d3d=false -Dsun.java2d.opengl=false -Dsun.java2d.noddraw=true -cp bin team.Main
```

The VS Code Run button already includes these flags via `.vscode/launch.json`, so
this is only needed if running from a plain terminal on an affected machine.

### Troubleshooting: won't run on another computer (or throws a compilation-problems error)

This app uses Java 11+ features (e.g. `String.isBlank()`). If it runs fine on one
computer but fails on another — especially with an error like
`java.lang.Error: Unresolved compilation problems` — the second machine almost
certainly only has an old Java runtime (e.g. Java 8) or no JDK at all, not a real
JDK 11 or newer. This was actually hit and diagnosed during development: a laptop
with only a Java 8 JRE installed threw exactly this error the instant it hit
`isBlank()`, because VS Code's bundled compiler allows building with unresolved
errors baked in as runtime stubs rather than failing the build outright.

Fix: install a real JDK 11 or newer (JDK 25 is recommended, to match this course's
requirement) — a full JDK, not just a JRE, since you need `javac` too. In VS Code,
the easiest way is **Ctrl+Shift+P > "Java: Install New JDK"**, which installs to
your own user profile with no admin rights needed, then **"Java: Configure Java
Runtime"** to point this project at it.

## Using the app

- **On launch**, a "Select a Team" window shows every NFL team grouped by
  Conference and Division (e.g. NFC > South > Carolina Panthers). Pick one and
  click **Select Team** (or double-click it) to open the main window with that
  team's data loaded.
- **File > Switch Team...** reopens that picker at any time to load a different
  team without restarting the app.
- **Team Info tab** — read-only facts about the organization (city, stadium,
  founding year, conference/division, owner, president, GM, head coach, colors,
  and when the data was last verified).
- **Players / Coaches / Support Staff tabs** — each has:
  - a live search box (filters as you type, matching name and other relevant
    fields like position/college/status),
  - a dropdown filter (by position / unit / department),
  - **Add**, **Edit Selected** (or double-click a row), and **Remove/Release
    Selected** buttons, each backed by an input dialog with text fields, spinners,
    and dropdowns.
- **File > Save All** writes every in-memory change back to the CSV files.
- **File > Reload From Files** discards unsaved changes and re-reads the CSV files.
- Every Add/Edit/Remove auto-saves to disk immediately, so you won't lose work by
  closing the window. Save All is mostly useful after editing the CSV files by hand.
- **View > Zoom In / Zoom Out / Reset Zoom** (or Ctrl+= / Ctrl+- / Ctrl+0) scales the
  font size and table row height across the whole window — handy on a large monitor
  where the default text is too small to read comfortably. It applies to every tab
  and to the Add/Edit dialogs, and persists as you switch tabs.
- **Help > About** shows the project name (for the currently loaded team) plus
  blanks for course/group/individual-contribution info.

## Keyboard shortcuts

| Shortcut | Action |
|---|---|
| Ctrl+= | Zoom in |
| Ctrl+- | Zoom out |
| Ctrl+0 | Reset zoom to 100% |
| Enter (in a dialog) | Activates the default button (OK / Select Team) |
| Double-click a table row | Opens Edit for that row |
| Double-click a team in the picker | Selects that team |

## Extending it

- **New fields**: add them to the relevant class in `model/`, update its
  `CSV_HEADER`, `toCsvRow()`, the matching row-parsing method in `DataStore`, and
  the corresponding dialog/table model in `gui/`.
- **New entity type** (e.g., "Broadcast/Media" personnel): follow the same
  four-piece pattern used for `StaffMember` (model class, CSV file, table model,
  dialog), then wire it into `MainFrame` as a new tab.
- **New team**: create a folder under `data/teams/` with the same 4 CSV files
  (matching headers) — it'll show up in the picker automatically, grouped correctly
  once its `team_info.csv` has real `Conference`/`Division` values.

## Known limitations

These are honest gaps, not hidden ones — worth knowing before a grader finds them:

- No automated unit tests (not required by the assignment, but a natural next step
  if your group wants to demonstrate testing).
- Jersey numbers aren't required to be unique within a team (only `id` is) — a few
  real teams' data has genuine jersey-number overlaps from source disagreements
  during roster cutdowns.
- The About box's course/group/contribution fields are blank placeholders — fill
  them in, or point people to [CONTRIBUTIONS.md](CONTRIBUTIONS.md) instead.
- Data is a point-in-time snapshot (dated per team in `team_info.csv`), not a live
  feed — NFL rosters change constantly.

## How this was verified

Rather than assuming the code works because it compiles, each stage of this project
was actually run and checked:

- **Compiles clean** with `javac`, recompiled after every change.
- **Launches and loads data** — confirmed via screenshots showing real data
  rendered in the GUI, not just "no crash."
- **Data integrity** — a Python validation script checks every team's 4 files for
  correct field counts, no duplicate IDs, valid enum values, and numeric bounds;
  last full run: 0 errors across all 32 teams.
- **End-to-end flow** — launched the app, picked a team from the picker (New
  England Patriots), and confirmed its real data (city, stadium, owner, head coach)
  loaded correctly into the main window with the dynamic, non-hardcoded title.
- **Zoom** — confirmed via keyboard shortcut that font/row size actually changes
  and resets correctly, not just that the menu item exists.
- **Team-swap independence** — confirmed by temporarily renaming a team and
  checking the window title updated with zero code changes, then reverting it.
- **Cross-machine JDK requirement** — reproduced the exact failure on a second
  computer (Java 8 only, no JDK), fixed it by installing JDK 25, then
  recompiled and relaunched successfully from that same machine.
- **Table display fixes** — recompiled clean and relaunched after adding
  column auto-sizing and Note-field text wrapping; visually confirmed by the
  user that long values are no longer clipped.
- **Per-team theming** — recompiled clean and relaunched after adding
  `TeamTheme`/Nimbus theming; visually confirmed by the user that the whole
  window re-colors per team, including after switching teams.

Full history of what changed and why, in order, is in [CHANGELOG.md](CHANGELOG.md).

## FAQ

**Do I need the internet to run this?** No — all data is local CSV files. Internet
was only used during development to research real roster/coaching/front-office data.

**Can I use this with a made-up/fictional team?** Yes — add a folder under
`data/teams/` with the 4 CSV files in the same format as any real team, and it'll
appear in the picker.

**What happens if I edit a CSV file by hand and it's malformed?** The app will fail
to start (or throw an error) rather than silently showing wrong data — see
[CsvUtil.java](src/team/data/CsvUtil.java) for the exact quoting rules (wrap a field
in `"..."` if it contains a comma or a quote character).

**Why doesn't the roster exactly match today's real depth chart?** See "About the
data" above — this is a dated snapshot, and NFL rosters change constantly.

**Is this real AI-assisted work allowed for the assignment?** Yes — the assignment
prompt explicitly states "The Project is expected to be completed using AI as a
resource." See [CONTRIBUTIONS.md](CONTRIBUTIONS.md) for how AI was used here.
