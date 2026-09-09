package team.model;

/**
 * Front-office and operations personnel who are not players or on-field coaches
 * (medical staff, equipment, scouting, analytics, communications, etc.).
 */
public class StaffMember extends Person {

    public static final String[] CSV_HEADER = {
            "id", "firstName", "lastName", "age", "hometown",
            "department", "title", "yearsWithOrganization"
    };

    public static final String[] DEPARTMENTS = {
            "Ownership", "Front Office/Executive", "Football Operations",
            "Medical/Athletic Training", "Equipment", "Scouting", "Analytics",
            "Strength & Conditioning", "Media Relations", "Marketing",
            "Business Operations", "Human Resources", "Stadium Operations", "Security"
    };

    private String department;
    private String title;
    private int yearsWithOrganization;

    public StaffMember(String id, String firstName, String lastName, int age, String hometown,
                        String department, String title, int yearsWithOrganization) {
        super(id, firstName, lastName, age, hometown);
        this.department = department;
        this.title = title;
        this.yearsWithOrganization = yearsWithOrganization;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYearsWithOrganization() {
        return yearsWithOrganization;
    }

    public void setYearsWithOrganization(int yearsWithOrganization) {
        this.yearsWithOrganization = yearsWithOrganization;
    }

    @Override
    public String getRoleLabel() {
        return department;
    }

    @Override
    public String[] toCsvRow() {
        return new String[] {
                id, firstName, lastName, String.valueOf(age), hometown,
                department, title, String.valueOf(yearsWithOrganization)
        };
    }
}
