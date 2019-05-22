package krupski.aleksander.keepassdroid.data;

import java.util.List;

public class User {
    private String username;
    private List<Password> passwords;

    public User(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Password> getPasswords() {
        return passwords;
    }

    public void setPasswords(List<Password> passwords) {
        this.passwords = passwords;
    }



}
