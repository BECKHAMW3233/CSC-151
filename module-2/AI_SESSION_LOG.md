# AI Session Log

This is a record of the prompts given to Claude Code (Anthropic's AI coding
assistant) and what was done in response, for the work documented in
[CONTRIBUTIONS.md](CONTRIBUTIONS.md).

**This is not a solo project.** This log covers groundwork done by one group
member (William Beckham) ahead of meeting the rest of the Java Group (Christian
Logan, Brandon Malave, Roberto Rendon-Valdez), so there'd be something concrete to
bring to the group — not a finished or group-approved submission. See
[CONTRIBUTIONS.md](CONTRIBUTIONS.md) for the full context, including the plan to
hand this off and start collaborating with the rest of the group.

Claude Code was used as a tool for research (finding real NFL roster/coaching/
front-office data) and for writing code. Every decision about what to build, how
to complete each task, and how to optimize the user experience was directed by
William — that direction is what's recorded below. Prompts are paraphrased for
clarity and to keep this PG; nothing here changes what was actually asked or done.

## Session log

1. **Prompt:** Shared the assignment instructions (pick an NFL team, build a Java
   program with a GUI to manage players/coaches/staff for the 2026 season, using
   AI as a resource, submitted as identifiable individual work) and asked to get
   started, specifying Java only, CSV files, and a modular design.
   **Response:** Asked clarifying questions about which team to use and how much
   of the program to build, since the group hadn't met yet.

2. **Prompt:** Explained being located in Fayetteville, NC, with no particular
   NFL team preference, and that since the group hadn't met yet, to go ahead and
   build a complete working version.
   **Response:** Picked the Carolina Panthers (closest team to Fayetteville) and
   built the initial application: `model`/`data`/`gui` Java packages, a Swing GUI
   (menus, tabs, tables, add/edit/remove dialogs), and CSV-backed sample data.
   Compiled and launched it to confirm it worked before calling it done.

3. **Prompt:** Asked for an explanation of how the GUI layer works, comparing it
   to Python tools like Tkinter, Flask, and Streamlit.
   **Response:** Explained that Swing is Java's equivalent of Tkinter (a desktop
   GUI toolkit), not Flask/Streamlit (which build web pages instead), and walked
   through how the event loop, layout managers, dialogs, and table models work in
   this specific project.

4. **Prompt:** Asked to replace the sample data with real roster data and make
   the program as functional as possible.
   **Response:** Researched the real 2026 Carolina Panthers roster, coaching
   staff, and front office from the team's official site and ESPN, and rebuilt
   the CSV files with that real data (94 players, 28 coaches, 21 staff),
   documenting sourcing and a few honest caveats (e.g. fields that aren't
   publicly published).

5. **Prompt:** Asked whether the program actually met the assignment's
   requirements so far, and how it could be improved.
   **Response:** Checked the program against each stated requirement, and while
   checking, found and fixed a real bug: edits weren't actually auto-saving to
   disk despite documentation claiming they did. Suggested possible improvements
   but recommended holding off on adding more until the group had a chance to
   weigh in.

6. **Prompt:** Asked whether any NFL team names were hardcoded in the Java code,
   or whether everything came from the CSV files only.
   **Response:** Searched the codebase and found two places where a team name was
   hardcoded (the window title and the About box), fixed both so they're read
   from the CSV data instead, and proved the fix by temporarily renaming a team in
   the data, confirming the app picked it up with no code changes, then reverting
   it.

7. **Prompt:** Confirmed and reinforced that there should be no hardcoded team
   names anywhere, so that swapping in a different team's CSV files would be a
   seamless swap.
   **Response:** Confirmed the fix from the previous step already achieved this,
   and re-verified it.

8. **Prompt:** Asked whether the CSV data was correct, and asked for a zoom
   feature so the app would be easier to read on a larger monitor.
   **Response:** Audited the CSV data with a validation pass and found a real
   bug — a coach's age exceeded the range the edit dialog allowed, which would
   have crashed it — and fixed the underlying limit. Added a zoom feature (View
   menu, Ctrl+=/Ctrl+-/Ctrl+0) that scales font size and table row height across
   the whole window.

9. **Prompt:** Asked for the program to support every NFL team, not just one,
   with the user able to select which team to work with, per-team data saved
   independently, organized into a proper folder structure, and grouped by
   division/region.
   **Response:** Asked how complete the other 31 teams' data should be, since
   full real rosters for all of them would be a large research effort. Given the
   direction to do full real data for every team, organized by division, built
   the multi-team architecture (a team catalog, a startup team-picker screen
   grouped by conference/division, and a data layer that can point at any team's
   folder), migrated the Panthers' data into that structure, and dispatched
   several research passes (one per division) to build out real data for the
   other 31 teams.

10. **Prompt:** After a usage-limit interruption paused the research in progress,
    asked to pick back up where it left off (this happened more than once over
    the course of the research).
    **Response:** Checked exactly what had and hadn't been completed for each
    team and resumed only the missing pieces, rather than redoing finished work.
    Along the way, found and fixed another real bug: several real NFL owners are
    old enough, or have been with their organization long enough, that they
    exceeded the app's original age/tenure limits — meaning real facts were being
    quietly capped to avoid crashing the edit dialog. Fixed the underlying limits
    and had the real figures restored everywhere they'd been capped. Also found
    and fixed an inconsistency in how one team's division was labeled compared to
    the rest.

11. **Prompt:** (implicit, continuing the same effort) Confirmed to keep going
    once all 32 teams' data was complete.
    **Response:** Ran a full validation pass across all 32 teams' data (zero
    errors), then found and diagnosed a real rendering bug — the team-picker
    screen was rendering completely blank once it had to show all 32 teams —
    confirmed it wasn't a data or logic problem, traced it to a graphics-driver
    rendering issue specific to the development environment, fixed it, and
    verified the complete flow worked end-to-end by picking a team and confirming
    its real data loaded correctly.

12. **Prompt:** Asked to make the README more comprehensive and to update the
    contribution-tracking file to reflect everything done so far.
    **Response:** Rewrote the README with a fuller structure (requirements
    checklist, feature list, architecture explanation, full data reference,
    troubleshooting, FAQ) and wrote a first version of the contribution-tracking
    file.

13. **Prompt:** Corrected that the project was not built solo, asked for personal
    information to be added to the contribution file, and clarified that what had
    been done so far was groundwork the rest of the group could choose to use,
    adapt, or reject entirely.
    **Response:** Rewrote the contribution-tracking file to remove language that
    implied solo, finished work, and added an explicit section naming the
    decisions that are still genuinely open for the group to make together.
    Asked for the specific personal/course details needed to fill in the file
    accurately rather than guessing them.

14. **Prompt:** Provided the real names of the group members, the instructor, and
    the course/section.
    **Response:** Filled in that information in the contribution file and in the
    application's own About box, and recompiled to confirm nothing broke.

15. **Prompt:** Shared the course syllabus as a reference document.
    **Response:** Read it and flagged something worth checking with the
    instructor directly: the syllabus's general academic-integrity section lists
    submitting AI-generated work as an example of plagiarism, while a separate
    section explains the department is requiring AI use in these courses with
    per-class instructions — which appears to be exactly what this assignment's
    own instructions describe. Recommended confirming with the instructor that
    this level of AI-use disclosure is what's expected, rather than assuming it's
    sufficient. Also (incorrectly, without checking first) guessed which specific
    graded module this project corresponded to.

16. **Prompt:** Asked for the wording that implied solo work to be removed from
    the changelog specifically, and raised the idea of being freer with how the
    record was framed.
    **Response:** Removed the remaining wording that implied solo work.
    Separately, made clear that rewriting how something is framed is different
    from inventing facts that didn't happen, and that the second one wasn't
    something to do regardless of framing — an academic record should describe
    what actually happened.

17. **Prompt:** Clarified that the previous request was about permission to
    reword the "solo" framing, not about inventing false information.
    **Response:** Confirmed that's exactly what had been done — the wording was
    corrected without changing any of the underlying facts.

18. **Prompt:** Corrected that the project corresponds to Module 2 (the
    Individual Project) specifically, not any other module, and asked for
    guesses to stop and for questions to be asked directly instead when
    uncertain.
    **Response:** Corrected the module reference in the contribution file and the
    changelog to match, and — instead of guessing again — asked directly how
    "Individual Project" squares with the assignment's "Java Group" framing.

19. **Prompt:** Clarified that the project does need to be built collaboratively
    with the group, and described the concrete plan: handing off this groundwork
    in person to a group member soon, finding out whether that member has already
    connected with the other two, exchanging contact information, and starting to
    coordinate as a group from there.
    **Response:** Confirmed this matched the "groundwork for the group" framing
    already in place, and added a dated "next steps" note to the contribution
    file capturing that specific handoff plan.

20. **Prompt:** Clarified that Claude Code's role was specifically research and
    writing code, with all logic and user-experience decisions directed by
    William, and asked for this session log to be created — kept PG, and written
    so it does not present the project as a solo effort.
    **Response:** Updated the contribution file's description of how the work was
    done to match that exact framing, and created this file.
