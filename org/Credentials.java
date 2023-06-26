
public class Credentials {
    public String login;
    public String password;

    public Credentials(String login, String password) {
        this.login = login;
        this.password = password;
    }
    
    public void setPassword(String newPassword) {
    	this.password = newPassword;
    }
    
    public boolean hasNullValue() {
        return this.login == null || this.password == null;
    }

    public boolean isSamePassword(String targetPassword) {
        return this.password.equals(targetPassword);
    }
    
}
