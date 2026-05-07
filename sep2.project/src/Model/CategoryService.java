package Model;

import java.sql.SQLException;
import java.util.List;

public class CategoryService
{
    private static final String UNCATEGORIZED = "Uncategorized";
    private static final int NAME_MAX = 100;

    private CategoryRepository repository;

    public CategoryService(CategoryRepository repository)
    {
        this.repository = repository;
    }

    public Category add(String name, String description) throws SQLException
    {
        String trimmedName = name == null ? "" : name.trim();
        String safeDescription = description == null ? "" : description.trim();

        if (trimmedName.isEmpty())
        {
            throw new IllegalArgumentException("Name is required");
        }
        if (trimmedName.length() > NAME_MAX)
        {
            throw new IllegalArgumentException("Name must be at most " + NAME_MAX + " characters");
        }
        if (repository.existsByName(trimmedName))
        {
            throw new IllegalStateException("Category already exists");
        }

        Category category = new Category(trimmedName, safeDescription);
        return repository.save(category);
    }

    public Category edit(String currentName, String newName, String newDescription) throws SQLException
    {
        String trimmedNewName = newName == null ? "" : newName.trim();
        String safeNewDescription = newDescription == null ? "" : newDescription.trim();

        if (currentName == null || currentName.trim().isEmpty())
        {
            throw new IllegalArgumentException("Current name is required");
        }
        if (trimmedNewName.isEmpty())
        {
            throw new IllegalArgumentException("Name is required");
        }
        if (trimmedNewName.length() > NAME_MAX)
        {
            throw new IllegalArgumentException("Name must be at most " + NAME_MAX + " characters");
        }

        // Duplicate check — only if the name is actually changing
        if (!trimmedNewName.equalsIgnoreCase(currentName.trim())
                && repository.existsByName(trimmedNewName))
        {
            throw new IllegalStateException("Category already exists");
        }

        Category updated = repository.update(currentName.trim(), trimmedNewName, safeNewDescription);
        if (updated == null)
        {
            throw new IllegalStateException("Category not found: " + currentName);
        }
        return updated;
    }

    public void delete(String name) throws SQLException
    {
        if (name == null || name.trim().isEmpty())
        {
            throw new IllegalArgumentException("Name is required");
        }

        String trimmedName = name.trim();

        if (UNCATEGORIZED.equalsIgnoreCase(trimmedName))
        {
            throw new IllegalStateException("Cannot delete the default 'Uncategorized' category");
        }

        if (!repository.existsByName(trimmedName))
        {
            throw new IllegalStateException("Category not found: " + trimmedName);
        }

        // Reassign any events using this category to "Uncategorized" before deleting
        repository.reassignEvents(trimmedName, UNCATEGORIZED);
        repository.delete(trimmedName);
    }

    public List<Category> findAll() throws SQLException
    {
        return repository.findAll();
    }

    public Category findByName(String name) throws SQLException
    {
        if (name == null || name.trim().isEmpty())
        {
            return null;
        }
        return repository.findByName(name.trim());
    }
}
