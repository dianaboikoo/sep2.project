package ViewModel;

import Client.ServerConnection;
import Model.Category;
import Shared.GsonFactory;
import Shared.Request;
import Shared.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryManagementViewModel
{
  public static final String UNCATEGORIZED = "Uncategorized";

  private List<Category> categories;
  private List<FieldError> lastErrors;
  private boolean busy;

  public CategoryManagementViewModel()
  {
    // No dependencies — communicates via ServerConnection
    this.categories = new ArrayList<>();
    this.lastErrors = new ArrayList<>();
    this.busy = false;
  }

  /** Load all categories from the server. */
  public boolean loadCategories()
  {
    lastErrors = new ArrayList<>();
    try
    {
      Response response = ServerConnection.getInstance()
          .send(new Request("GET_CATEGORIES", Map.of()));
      if (!response.isOk())
      {
        lastErrors.add(new FieldError("_general", response.getMessage()));
        return false;
      }
      Gson gson = GsonFactory.get();
      Type listType = new TypeToken<List<Category>>(){}.getType();
      categories = gson.fromJson(gson.toJson(response.getData()), listType);
      return true;
    }
    catch (Exception e)
    {
      lastErrors.add(new FieldError("_general",
          "Could not load categories: " + e.getMessage()));
      return false;
    }
  }

  /** Add a new category. Returns true on success, false otherwise. */
  public boolean addCategory(String name, String description)
  {
    lastErrors = new ArrayList<>();
    busy = true;
    try
    {
      Response response = ServerConnection.getInstance()
          .send(new Request("ADD_CATEGORY",
              Map.of("name", name, "description", description == null ? "" : description)));

      if (!response.isOk())
      {
        String msg = response.getMessage();
        if (msg != null && msg.startsWith("FIELD:name:"))
        {
          lastErrors.add(new FieldError("name", msg.substring("FIELD:name:".length())));
        }
        else
        {
          lastErrors.add(new FieldError("_general", msg));
        }
        return false;
      }
      loadCategories();
      return true;
    }
    catch (Exception e)
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

  /** Edit an existing category. */
  public boolean editCategory(String currentName, String newName, String newDescription)
  {
    lastErrors = new ArrayList<>();
    busy = true;
    try
    {
      Response response = ServerConnection.getInstance()
          .send(new Request("EDIT_CATEGORY",
              Map.of("currentName", currentName,
                     "newName",     newName,
                     "description", newDescription == null ? "" : newDescription)));

      if (!response.isOk())
      {
        String msg = response.getMessage();
        if (msg != null && msg.startsWith("FIELD:name:"))
        {
          lastErrors.add(new FieldError("name", msg.substring("FIELD:name:".length())));
        }
        else
        {
          lastErrors.add(new FieldError("_general", msg));
        }
        return false;
      }
      loadCategories();
      return true;
    }
    catch (Exception e)
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

  /** Delete a category. Events using it are reassigned to "Uncategorized". */
  public boolean deleteCategory(String name)
  {
    lastErrors = new ArrayList<>();
    busy = true;
    try
    {
      Response response = ServerConnection.getInstance()
          .send(new Request("DELETE_CATEGORY", Map.of("name", name)));

      if (!response.isOk())
      {
        lastErrors.add(new FieldError("_general", response.getMessage()));
        return false;
      }
      loadCategories();
      return true;
    }
    catch (Exception e)
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

  /** @return true when the special "Uncategorized" row should not be deletable. */
  public boolean isProtected(String categoryName)
  {
    return categoryName != null && UNCATEGORIZED.equalsIgnoreCase(categoryName.trim());
  }

  public List<Category> getCategories()    { return categories; }
  public List<FieldError> getLastErrors()  { return lastErrors; }
  public boolean isBusy()                  { return busy; }
}