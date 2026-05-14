package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepositoryImpl implements  CategoryRepository{

    private static CategoryRepositoryImpl instance;

    private CategoryRepositoryImpl() throws SQLException
    {
        DriverManager.registerDriver(new org.postgresql.Driver());
    }

    public static synchronized CategoryRepositoryImpl getInstance() throws SQLException
    {
        if(instance == null)
        {
            instance = new CategoryRepositoryImpl();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres?currentSchema=events",
                "postgres", "252006");
    }

    @Override
    public Category save(Category category) throws SQLException {
        try(Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(
              "INSERT INTO category(name, description) VALUES (?, ?);"      
            );
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            statement.executeUpdate();
            return category;
        }
    }

    @Override
    public List<Category> findAll() throws SQLException {
        try(Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement("SELECT name, description FROM category ORDER BY name ASC;");
            ResultSet resultSet = statement.executeQuery();
            List<Category> result = new ArrayList<>();
            while (resultSet.next())
            {
                result.add(createCategory(resultSet));
            }
            return  result;
        }
    }

    private Category createCategory(ResultSet resultSet) throws SQLException {
        return new Category(
                resultSet.getString("name"),
                resultSet.getString("description")
        );
    }

    @Override
    public Category findByName(String name) throws SQLException {
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT name, description FROM category WHERE name = ?;");
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
            {
                return createCategory(resultSet);
            }
            return null;
        }
    }

    @Override
    public Category update(String currentName, String newName, String newDescription) throws SQLException {
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE category SET name = ?, description = ? WHERE name = ?;");
            statement.setString(1, newName);
            statement.setString(2, newDescription);
            statement.setString(3, currentName);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0)
            {
                return null;
            }
            return new Category(newName, newDescription);
        }
    }

    @Override
    public void delete(String name) throws SQLException {
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM category WHERE name = ?;");
            statement.setString(1, name);
            statement.executeUpdate();
        }
    }

    @Override
    public boolean existsByName(String name) throws SQLException {
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT 1 FROM category WHERE LOWER(name) = LOWER(?);");
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    @Override
    public void reassignEvents(String fromCategory, String toCategory) throws SQLException {
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE events SET category_name = ? WHERE category_name = ?;");
            statement.setString(1, toCategory);
            statement.setString(2, fromCategory);
            statement.executeUpdate();
        }
    }
}
