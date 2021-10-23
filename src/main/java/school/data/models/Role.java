package school.data.models;

public enum Role {
    STUDENT("student"),
    TEACHER("teacher"),
    ADMIN("admin");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
