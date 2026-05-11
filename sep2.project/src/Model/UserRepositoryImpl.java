package Model;

import java.sql.*;

public class UserRepositoryImpl implements UserRepository
{
    private static UserRepositoryImpl instance;

    private UserRepositoryImpl() throws SQLException
    {
        DriverManager.registerDriver(new org.postgresql.Driver());
    }

    public static synchronized UserRepositoryImpl getInstance() throws SQLException
    {
        if (instance == null)
        {
            instance = new UserRepositoryImpl();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(
            "jdbc:postgresql://localhost:5432/postgres?currentSchema=events",
            "postgres", "postgres");
    }

    @Override
    public UserRole findByCredentials(String email, String password) throws SQLException
    {
        // Check admin table first
        try (Connection connection = getConnection())
        {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT email FROM admin WHERE email = ? AND password = ?");
            stmt.setString(1, email);
            stmt.setString(2, password);
            if (stmt.executeQuery().next())
            {
                return UserRole.ADMIN;
            }
        }

        // Check users table
        try (Connection connection = getConnection())
        {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT email FROM users WHERE email = ? AND password = ?");
            stmt.setString(1, email);
            stmt.setString(2, password);
            if (stmt.executeQuery().next())
            {
                return UserRole.USER;
            }
        }

        return null; // no match
    }
}
