package core;

public abstract class Users {

    //Fields
    private String id;
    private String name;
    private String email;

    // Constructor to set all three fields
    public Users(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getter method for id
    public String getId() {
        return id;
    }

    // Getter method for name
    public String getName() {
        return name;
    }

    // Getter method for email
    public String getEmail() {
        return email;
    }
}
