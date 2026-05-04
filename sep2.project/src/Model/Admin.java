package Model;

public class Admin
{
  private String email;
  private String password;

  public Admin(String email, String password)
  {
    this.email = email;
    this.password = password;
  }

  public String getEmail() { return email; }
  public String getPassword() { return password; }

  public void login()
  {
    // login logic here
  }
}