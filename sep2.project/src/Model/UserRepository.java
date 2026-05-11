package Model;

import java.sql.SQLException;

public interface UserRepository
{
    // Returns the role of the user if credentials match, null if not found
    UserRole findByCredentials(String email, String password) throws SQLException;
}
