package krupski.aleksander.keepassdroid.data;

public class Password {
    public Password(){}
    public Password(String login, String site, String encryptedPassword){
        this.login = login;
        this.site = site;
        this.encryptedPassword = encryptedPassword;
    }
    private String login;
    private String site;
    private String encryptedPassword;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }
}
