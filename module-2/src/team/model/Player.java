package team.model;

/**
 * A player on the active roster, practice squad, or injured reserve.
 */
public class Player extends Person {

    public static final String[] CSV_HEADER = {
            "id", "firstName", "lastName", "age", "hometown",
            "jerseyNumber", "position", "college", "yearsExperience",
            "status", "height", "weightLbs"
    };

    public static final String[] POSITIONS = {
            "QB", "RB", "FB", "WR", "TE", "OT", "OG", "C",
            "DE", "DT", "LB", "CB", "S", "K", "P", "LS"
    };

    public static final String[] STATUSES = {
            "Active", "Injured Reserve", "Non-Football Illness", "Practice Squad",
            "Physically Unable to Perform", "Suspended", "Free Agent"
    };

    private int jerseyNumber;
    private String position;
    private String college;
    private int yearsExperience;
    private String status;
    private String height;
    private int weightLbs;

    public Player(String id, String firstName, String lastName, int age, String hometown,
                  int jerseyNumber, String position, String college, int yearsExperience,
                  String status, String height, int weightLbs) {
        super(id, firstName, lastName, age, hometown);
        this.jerseyNumber = jerseyNumber;
        this.position = position;
        this.college = college;
        this.yearsExperience = yearsExperience;
        this.status = status;
        this.height = height;
        this.weightLbs = weightLbs;
    }

    public int getJerseyNumber() {
        return jerseyNumber;
    }

    public void setJerseyNumber(int jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public int getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(int yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public int getWeightLbs() {
        return weightLbs;
    }

    public void setWeightLbs(int weightLbs) {
        this.weightLbs = weightLbs;
    }

    @Override
    public String getRoleLabel() {
        return position;
    }

    @Override
    public String[] toCsvRow() {
        return new String[] {
                id, firstName, lastName, String.valueOf(age), hometown,
                String.valueOf(jerseyNumber), position, college, String.valueOf(yearsExperience),
                status, height, String.valueOf(weightLbs)
        };
    }
}
