package team.model;

/**
 * Base type for anyone tracked in the organization: players, coaches, and support staff.
 */
public abstract class Person {

    protected String id;
    protected String firstName;
    protected String lastName;
    protected int age;
    protected String hometown;

    protected Person(String id, String firstName, String lastName, int age, String hometown) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.hometown = hometown;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    /** Short label describing this person's role, used in table columns and dialogs. */
    public abstract String getRoleLabel();

    /** Serializes this person to a CSV row matching the subclass's CSV_HEADER order. */
    public abstract String[] toCsvRow();

    @Override
    public String toString() {
        return getFullName();
    }
}
