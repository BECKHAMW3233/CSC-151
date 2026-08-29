package team.data;

import team.model.Coach;
import team.model.Player;
import team.model.StaffMember;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Owns the in-memory roster/coach/staff lists and moves them to and from the CSV files
 * under one team's folder (see {@link TeamCatalog}). This is the single point of contact
 * between the GUI layer and persistence. Call {@link #switchTeam(Path)} to point this
 * store at a different team's folder (e.g. from a team-selector screen).
 */
public class DataStore {

    private Path teamDir;
    private Path playersFile;
    private Path coachesFile;
    private Path staffFile;
    private Path teamInfoFile;

    private final List<Player> players = new ArrayList<>();
    private final List<Coach> coaches = new ArrayList<>();
    private final List<StaffMember> staff = new ArrayList<>();
    private final Map<String, String> teamInfo = new LinkedHashMap<>();

    public DataStore(Path teamDir) {
        switchTeam(teamDir);
    }

    /** Points this store at a different team's folder. Does not load data - call {@link #loadAll()} next. */
    public final void switchTeam(Path teamDir) {
        this.teamDir = teamDir;
        this.playersFile = teamDir.resolve("players.csv");
        this.coachesFile = teamDir.resolve("coaches.csv");
        this.staffFile = teamDir.resolve("staff.csv");
        this.teamInfoFile = teamDir.resolve("team_info.csv");
    }

    public Path getTeamDir() {
        return teamDir;
    }

    public void loadAll() throws IOException {
        players.clear();
        for (String[] row : CsvUtil.readAll(playersFile)) {
            players.add(rowToPlayer(row));
        }
        coaches.clear();
        for (String[] row : CsvUtil.readAll(coachesFile)) {
            coaches.add(rowToCoach(row));
        }
        staff.clear();
        for (String[] row : CsvUtil.readAll(staffFile)) {
            staff.add(rowToStaff(row));
        }
        teamInfo.clear();
        for (String[] row : CsvUtil.readAll(teamInfoFile)) {
            if (row.length >= 2) {
                teamInfo.put(row[0], row[1]);
            }
        }
    }

    public void saveAll() throws IOException {
        CsvUtil.writeAll(playersFile, Player.CSV_HEADER,
                players.stream().map(Player::toCsvRow).collect(Collectors.toList()));
        CsvUtil.writeAll(coachesFile, Coach.CSV_HEADER,
                coaches.stream().map(Coach::toCsvRow).collect(Collectors.toList()));
        CsvUtil.writeAll(staffFile, StaffMember.CSV_HEADER,
                staff.stream().map(StaffMember::toCsvRow).collect(Collectors.toList()));
    }

    // ---------- Players ----------

    public List<Player> getPlayers() {
        return players;
    }

    public String nextPlayerId() {
        return nextId(players.stream().map(Player::getId).collect(Collectors.toList()), "P");
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public boolean removePlayer(String id) {
        return players.removeIf(p -> p.getId().equals(id));
    }

    public List<Player> searchPlayers(String query, String positionFilter) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        return players.stream()
                .filter(p -> positionFilter == null || positionFilter.equals("All")
                        || p.getPosition().equals(positionFilter))
                .filter(p -> q.isEmpty()
                        || p.getFullName().toLowerCase(Locale.ROOT).contains(q)
                        || p.getPosition().toLowerCase(Locale.ROOT).contains(q)
                        || p.getCollege().toLowerCase(Locale.ROOT).contains(q)
                        || p.getStatus().toLowerCase(Locale.ROOT).contains(q))
                .collect(Collectors.toList());
    }

    // ---------- Coaches ----------

    public List<Coach> getCoaches() {
        return coaches;
    }

    public String nextCoachId() {
        return nextId(coaches.stream().map(Coach::getId).collect(Collectors.toList()), "C");
    }

    public void addCoach(Coach coach) {
        coaches.add(coach);
    }

    public boolean removeCoach(String id) {
        return coaches.removeIf(c -> c.getId().equals(id));
    }

    public List<Coach> searchCoaches(String query, String unitFilter) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        return coaches.stream()
                .filter(c -> unitFilter == null || unitFilter.equals("All") || c.getUnit().equals(unitFilter))
                .filter(c -> q.isEmpty()
                        || c.getFullName().toLowerCase(Locale.ROOT).contains(q)
                        || c.getTitle().toLowerCase(Locale.ROOT).contains(q))
                .collect(Collectors.toList());
    }

    // ---------- Staff ----------

    public List<StaffMember> getStaff() {
        return staff;
    }

    public String nextStaffId() {
        return nextId(staff.stream().map(StaffMember::getId).collect(Collectors.toList()), "S");
    }

    public void addStaff(StaffMember member) {
        staff.add(member);
    }

    public boolean removeStaff(String id) {
        return staff.removeIf(s -> s.getId().equals(id));
    }

    public List<StaffMember> searchStaff(String query, String departmentFilter) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        return staff.stream()
                .filter(s -> departmentFilter == null || departmentFilter.equals("All")
                        || s.getDepartment().equals(departmentFilter))
                .filter(s -> q.isEmpty()
                        || s.getFullName().toLowerCase(Locale.ROOT).contains(q)
                        || s.getTitle().toLowerCase(Locale.ROOT).contains(q))
                .collect(Collectors.toList());
    }

    // ---------- Team info ----------

    public Map<String, String> getTeamInfo() {
        return teamInfo;
    }

    // ---------- Row <-> object conversion ----------

    private static Player rowToPlayer(String[] r) {
        return new Player(r[0], r[1], r[2], parseInt(r[3]), r[4],
                parseInt(r[5]), r[6], r[7], parseInt(r[8]), r[9], r[10], parseInt(r[11]));
    }

    private static Coach rowToCoach(String[] r) {
        return new Coach(r[0], r[1], r[2], parseInt(r[3]), r[4],
                r[5], r[6], parseInt(r[7]), parseInt(r[8]));
    }

    private static StaffMember rowToStaff(String[] r) {
        return new StaffMember(r[0], r[1], r[2], parseInt(r[3]), r[4],
                r[5], r[6], parseInt(r[7]));
    }

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** Generates the next sequential id like "P017" based on the highest existing numeric suffix. */
    private static String nextId(List<String> existingIds, String prefix) {
        int max = 0;
        for (String id : existingIds) {
            if (id != null && id.startsWith(prefix)) {
                try {
                    max = Math.max(max, Integer.parseInt(id.substring(prefix.length())));
                } catch (NumberFormatException ignored) {
                    // non-numeric suffix; skip
                }
            }
        }
        return String.format("%s%03d", prefix, max + 1);
    }
}
