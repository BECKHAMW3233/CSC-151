package team.model;

/**
 * A member of the coaching staff (head coach, coordinators, position coaches, etc.).
 */
public class Coach extends Person {

    public static final String[] CSV_HEADER = {
            "id", "firstName", "lastName", "age", "hometown",
            "title", "unit", "yearsWithTeam", "yearsExperienceTotal"
    };

    public static final String[] TITLES = {
            "Head Coach", "Offensive Coordinator", "Defensive Coordinator",
            "Special Teams Coordinator", "Quarterbacks Coach", "Running Backs Coach",
            "Wide Receivers Coach", "Offensive Line Coach", "Defensive Line Coach",
            "Linebackers Coach", "Defensive Backs Coach", "Strength & Conditioning Coordinator"
    };

    public static final String[] UNITS = {
            "Administration", "Offense", "Defense", "Special Teams", "Strength & Conditioning"
    };

    private String title;
    private String unit;
    private int yearsWithTeam;
    private int yearsExperienceTotal;

    public Coach(String id, String firstName, String lastName, int age, String hometown,
                 String title, String unit, int yearsWithTeam, int yearsExperienceTotal) {
        super(id, firstName, lastName, age, hometown);
        this.title = title;
        this.unit = unit;
        this.yearsWithTeam = yearsWithTeam;
        this.yearsExperienceTotal = yearsExperienceTotal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getYearsWithTeam() {
        return yearsWithTeam;
    }

    public void setYearsWithTeam(int yearsWithTeam) {
        this.yearsWithTeam = yearsWithTeam;
    }

    public int getYearsExperienceTotal() {
        return yearsExperienceTotal;
    }

    public void setYearsExperienceTotal(int yearsExperienceTotal) {
        this.yearsExperienceTotal = yearsExperienceTotal;
    }

    @Override
    public String getRoleLabel() {
        return title;
    }

    @Override
    public String[] toCsvRow() {
        return new String[] {
                id, firstName, lastName, String.valueOf(age), hometown,
                title, unit, String.valueOf(yearsWithTeam), String.valueOf(yearsExperienceTotal)
        };
    }
}
