// TODO: are there structs in this language??

public class Credentials {
    public String login;
    public String password;

    public Credentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public boolean hasNullValue() {
        return this.login == null || this.password == null;
    }

    public boolean isSamePassword(String targetPassword) {
        return this.password.equals(targetPassword);
    }
    
}
