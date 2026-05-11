package ViewModel;

import Model.UserRepository;
import Model.UserRole;

import java.sql.SQLException;

public class LoginViewModel
{
    private final UserRepository userRepository;

    public LoginViewModel(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    // Returns the matched role, or null if credentials are wrong
    public UserRole login(String email, String password)
    {
        if (email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty())
        {
            return null;
        }

        try
        {
            return userRepository.findByCredentials(email.trim(), password.trim());
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
