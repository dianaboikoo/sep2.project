package ViewModel;

import Model.Category;
import Model.CategoryService;
import Model.City;
import Model.EventListDto;
import Model.EventService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventsListViewModel
{
  private final EventService eventService;
  private final CategoryService categoryService;

  private List<FieldError> lastErrors = new ArrayList<>();

  public EventsListViewModel(EventService eventService, CategoryService categoryService)
  {
    this.eventService = eventService;
    this.categoryService = categoryService;
  }

  public List<EventListDto> getPublishedEvents()
  {
    return getFilteredEvents(null, null, null, null);
  }

  /**
   * Raw filter (no validation). Used internally after the view has validated inputs.
   * Returns an empty list on DB error.
   */
  public List<EventListDto> getFilteredEvents(String category, Integer zipCode,
      LocalDate from, LocalDate to)
  {
    try
    {
      // Treat blank strings as "no filter" (null)
      String safeCategory = (category == null || category.trim().isEmpty()) ? null : category;
      return eventService.getFilteredEvents(safeCategory, zipCode, from, to);
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      lastErrors.add(new FieldError("_general",
          "Could not load events: " + e.getMessage()));
      return Collections.emptyList();
    }
  }


  public boolean validateFilters(LocalDate fromDate, LocalDate toDate)
  {
    lastErrors = new ArrayList<>();
    LocalDate today = LocalDate.now();

    if (fromDate != null && fromDate.isBefore(today))
    {
      lastErrors.add(new FieldError("fromDate", "From date cannot be in the past"));
    }

    if (toDate != null && fromDate != null && toDate.isBefore(fromDate))
    {
      lastErrors.add(new FieldError("toDate", "To date cannot be before From date"));
    }

    if (toDate != null && fromDate == null && toDate.isBefore(today))
    {
      lastErrors.add(new FieldError("toDate", "To date cannot be in the past"));
    }

    return lastErrors.isEmpty();
  }


  public List<EventListDto> applyFilters(String category, Integer zipCode,
      LocalDate fromDate, LocalDate toDate)
  {
    if (!validateFilters(fromDate, toDate))
    {
      return Collections.emptyList();
    }
    return getFilteredEvents(category, zipCode, fromDate, toDate);
  }

  public List<Category> getCategoryOptions()
  {
    try
    {
      return categoryService.findAll();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      return Collections.emptyList();
    }
  }

  public List<City> getCityOptions()
  {
    try
    {
      return eventService.getAllCities();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      return Collections.emptyList();
    }
  }


  public List<FieldError> getLastErrors() { return lastErrors; }
}