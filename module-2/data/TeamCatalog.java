package team.data;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Discovers the NFL teams available under {@code data/teams/} by scanning for
 * subfolders, so adding, removing, or renaming a team is just a folder change -
 * nothing in the Java code needs to know the roster of teams in advance.
 */
public final class TeamCatalog {

    /** One team's folder plus the display metadata read from its team_info.csv. */
    public static final class TeamEntry {
        public final String slug;
        public final Path directory;
        public final String displayName;
        public final String conference;
        public final String division;

        TeamEntry(String slug, Path directory, String displayName, String conference, String division) {
            this.slug = slug;
            this.directory = directory;
            this.displayName = displayName;
            this.conference = conference;
            this.division = division;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private TeamCatalog() {
    }

    /** Scans {@code teamsRoot} for team folders, sorted by conference, division, then name. */
    public static List<TeamEntry> discoverTeams(Path teamsRoot) throws IOException {
        List<TeamEntry> entries = new ArrayList<>();
        if (!Files.isDirectory(teamsRoot)) {
            return entries;
        }
        try (DirectoryStream<Path> dirs = Files.newDirectoryStream(teamsRoot)) {
            for (Path dir : dirs) {
                if (Files.isDirectory(dir)) {
                    entries.add(readTeamEntry(dir));
                }
            }
        }
        entries.sort(Comparator.<TeamEntry, String>comparing(e -> e.conference == null ? "" : e.conference)
                .thenComparing(e -> e.division == null ? "" : e.division)
                .thenComparing(e -> e.displayName));
        return entries;
    }

    private static TeamEntry readTeamEntry(Path dir) throws IOException {
        String slug = dir.getFileName().toString();
        String displayName = humanize(slug);
        String conference = "Unassigned";
        String division = "Unassigned";

        Path infoFile = dir.resolve("team_info.csv");
        for (String[] row : CsvUtil.readAll(infoFile)) {
            if (row.length < 2) {
                continue;
            }
            switch (row[0]) {
                case "Team Name":
                    if (!row[1].isBlank()) {
                        displayName = row[1];
                    }
                    break;
                case "Conference":
                    if (!row[1].isBlank()) {
                        conference = row[1];
                    }
                    break;
                case "Division":
                    if (!row[1].isBlank()) {
                        division = row[1];
                    }
                    break;
                default:
                    break;
            }
        }
        return new TeamEntry(slug, dir, displayName, conference, division);
    }

    private static String humanize(String slug) {
        String[] words = slug.split("-");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase(Locale.ROOT));
        }
        return sb.toString();
    }
}
