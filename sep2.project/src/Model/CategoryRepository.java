package Model;

import java.sql.SQLException;
import java.util.List;

public interface CategoryRepository {

    Category save(Category category) throws SQLException;
    List<Category> findAll() throws SQLException;
    Category findByName(String name) throws SQLException;
    Category update(String currentName, String newName, String newDescription) throws SQLException;
    void delete(String name) throws SQLException;
    boolean existsByName(String name) throws SQLException;
    void reassignEvents(String fromCategory, String toCategory) throws SQLException;
}
