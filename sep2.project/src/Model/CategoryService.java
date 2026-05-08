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
    validateName(name);
    String trimmedName = name.trim();
    String safeDescription = description == null ? "" : description.trim();

    if (repository.existsByName(trimmedName))
    {
      throw new IllegalStateException("Category already exists");
    }

    Category category = new Category(trimmedName, safeDescription);
    return repository.save(category);
  }

  public Category edit(String currentName, String newName, String newDescription) throws SQLException
  {
    if (currentName == null || currentName.trim().isEmpty())
    {
      throw new IllegalArgumentException("Current name is required");
    }

    validateName(newName);
    String trimmedNewName = newName.trim();
    String safeNewDescription = newDescription == null ? "" : newDescription.trim();

    // Duplicate check — only if the name is actually changing (case-insensitive comparison)
    // This excludes the current category from the duplicate check, allowing user to save with same name
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

  /**
   * Validates a category name against business rules:
   * 1. Not null / not empty
   * 2. No leading or trailing whitespace
   * 3. Max 100 characters
   * Throws IllegalArgumentException with the appropriate spec error message.
   */
  private void validateName(String name)
  {
    if (name == null || name.isEmpty())
    {
      throw new IllegalArgumentException("Name is required");
    }
    // Reject leading or trailing whitespace BEFORE trimming
    // (this is what the spec means by "no leading/trailing whitespace")
    if (!name.equals(name.trim()))
    {
      throw new IllegalArgumentException("Name cannot have leading or trailing whitespace");
    }
    if (name.trim().isEmpty())
    {
      throw new IllegalArgumentException("Name is required");
    }
    if (name.length() > NAME_MAX)
    {
      throw new IllegalArgumentException("Name must be under " + NAME_MAX + " characters");
    }
  }
}