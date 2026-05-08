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
   * Populates lastErrors on failure with field-level (name) or general errors.
   *
   * Maps service-layer exceptions to spec-defined errors:
   *  - IllegalArgumentException → 400 (field error on "name")
   *  - IllegalStateException    → 409 (duplicate, "Category already exists")
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
      // 400 → validation error on the name field
      lastErrors.add(new FieldError("name", e.getMessage()));
      return false;
    }
    catch (IllegalStateException e)
    {
      // 409 → duplicate. Spec wants "Category already exists" as the message.
      lastErrors.add(new FieldError("name", "Category already exists"));
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
   * Edit an existing category.
   */
  public boolean editCategory(String currentName, String newName, String newDescription)
  {
    lastErrors = new ArrayList<>();
    busy = true;
    try
    {
      categoryService.edit(currentName, newName, newDescription);
      loadCategories();
      return true;
    }
    catch (IllegalArgumentException e)
    {
      lastErrors.add(new FieldError("name", e.getMessage()));
      return false;
    }
    catch (IllegalStateException e)
    {
      // could be "Category already exists" or "Category not found"
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
   * Delete a category. Events using it are reassigned to "Uncategorized".
   */
  public boolean deleteCategory(String name)
  {
    lastErrors = new ArrayList<>();
    busy = true;
    try
    {
      categoryService.delete(name);
      loadCategories();
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