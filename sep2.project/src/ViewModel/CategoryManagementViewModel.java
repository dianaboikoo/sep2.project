package ViewModel;

import Model.Category;
import Model.CategoryService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryManagementViewModel
{
  public static final String UNCATEGORIZED = "Uncategorized";

  private final CategoryService categoryService;
  private List<Category> categories;
  private List<FieldError> lastErrors;
  private boolean busy; // true while a request is "in-flight" (so view can disable buttons)

  public CategoryManagementViewModel(CategoryService categoryService)
  {
    this.categoryService = categoryService;
    this.categories = new ArrayList<>();
    this.lastErrors = new ArrayList<>();
    this.busy = false;
  }

  /**
   * Load all categories from the database into the in-memory list.
   * Returns true on success, false on DB error (check getLastErrors()).
   */
  public boolean loadCategories()
  {
    lastErrors = new ArrayList<>();
    try
    {
      categories = categoryService.findAll();
      return true;
    }
    catch (SQLException e)
    {
      lastErrors.add(new FieldError("_general",
          "Could not load categories: " + e.getMessage()));
      return false;
    }
  }

  /**
   * Add a new category. Returns true on success, false otherwise.
   * Populates lastErrors with field-level or general errors on failure.
   */
  public boolean addCategory(String name, String description)
  {
    lastErrors = new ArrayList<>();
    busy = true;
    try
    {
      categoryService.add(name, description);
      loadCategories();         // refresh list
      return true;
    }
    catch (IllegalArgumentException e)
    {
      // validation error (empty name, too long, etc.) → field error
      lastErrors.add(new FieldError("name", e.getMessage()));
      return false;
    }
    catch (IllegalStateException e)
    {
      // duplicate name (maps to HTTP 409 conceptually)
      lastErrors.add(new FieldError("name", "Name already exists"));
      return false;
    }
    catch (SQLException e)
    {
      lastErrors.add(new FieldError("_general",
          "Could not add category: " + e.getMessage()));
      return false;
    }
    finally
    {
      busy = false;
    }
  }

  /**
   * Edit an existing category. Pre-fill comes from the row; this just persists.
   */
  public boolean editCategory(String currentName, String newName, String newDescription)
  {
    lastErrors = new ArrayList<>();
    busy = true;
    try
    {
      categoryService.edit(currentName, newName, newDescription);
      loadCategories();         // refresh list
      return true;
    }
    catch (IllegalArgumentException e)
    {
      lastErrors.add(new FieldError("name", e.getMessage()));
      return false;
    }
    catch (IllegalStateException e)
    {
      lastErrors.add(new FieldError("name", e.getMessage()));
      return false;
    }
    catch (SQLException e)
    {
      lastErrors.add(new FieldError("_general",
          "Could not update category: " + e.getMessage()));
      return false;
    }
    finally
    {
      busy = false;
    }
  }

  /**
   * Delete a category. Events using this category are reassigned to "Uncategorized".
   */
  public boolean deleteCategory(String name)
  {
    lastErrors = new ArrayList<>();
    busy = true;
    try
    {
      categoryService.delete(name);
      loadCategories();         // refresh list
      return true;
    }
    catch (IllegalArgumentException e)
    {
      lastErrors.add(new FieldError("_general", e.getMessage()));
      return false;
    }
    catch (IllegalStateException e)
    {
      lastErrors.add(new FieldError("_general", e.getMessage()));
      return false;
    }
    catch (SQLException e)
    {
      lastErrors.add(new FieldError("_general",
          "Could not delete category: " + e.getMessage()));
      return false;
    }
    finally
    {
      busy = false;
    }
  }

  /**
   * @return true when the special "Uncategorized" row should not be deletable.
   */
  public boolean isProtected(String categoryName)
  {
    return categoryName != null && UNCATEGORIZED.equalsIgnoreCase(categoryName.trim());
  }

  public List<Category> getCategories()    { return categories; }
  public List<FieldError> getLastErrors()  { return lastErrors; }
  public boolean isBusy()                  { return busy; }
}