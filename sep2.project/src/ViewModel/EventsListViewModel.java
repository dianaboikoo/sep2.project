package ViewModel;

import Client.ServerConnection;
import Model.Category;
import Model.City;
import Model.EventListDto;
import Shared.GsonFactory;
import Shared.Request;
import Shared.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsListViewModel
{
  private static final DateTimeFormatter DATE_FMT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private List<FieldError> lastErrors = new ArrayList<>();

  public EventsListViewModel()
  {
    // No dependencies — communicates via ServerConnection
  }

  public List<EventListDto> getPublishedEvents()
  {
    try
    {
      Response response = ServerConnection.getInstance()
          .send(new Request("GET_ALL_EVENTS", Map.of()));
      if (!response.isOk())
      {
        lastErrors.add(new FieldError("_general", response.getMessage()));
        return Collections.emptyList();
      }
      Gson gson = GsonFactory.get();
      Type listType = new TypeToken<List<EventListDto>>(){}.getType();
      return gson.fromJson(gson.toJson(response.getData()), listType);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      lastErrors.add(new FieldError("_general", "Could not load events: " + e.getMessage()));
      return Collections.emptyList();
    }
  }

  /**
   * Raw filter — dates may be null for open-ended ranges.
   */
  public List<EventListDto> getFilteredEvents(String category, Integer zipCode,
      LocalDate from, LocalDate to)
  {
    try
    {
      String safeCategory = (category == null || category.trim().isEmpty()) ? null : category;

      Map<String, Object> payload = new HashMap<>();
      payload.put("category", safeCategory);
      payload.put("zipCode",  zipCode);
      payload.put("fromDate", from  == null ? null : from.format(DATE_FMT));
      payload.put("toDate",   to    == null ? null : to.format(DATE_FMT));

      Response response = ServerConnection.getInstance()
          .send(new Request("GET_FILTERED_EVENTS", payload));
      if (!response.isOk())
      {
        lastErrors.add(new FieldError("_general", response.getMessage()));
        return Collections.emptyList();
      }
      Gson gson = GsonFactory.get();
      Type listType = new TypeToken<List<EventListDto>>(){}.getType();
      return gson.fromJson(gson.toJson(response.getData()), listType);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      lastErrors.add(new FieldError("_general", "Could not filter events: " + e.getMessage()));
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
      Response response = ServerConnection.getInstance()
          .send(new Request("GET_CATEGORIES", Map.of()));
      if (!response.isOk()) return Collections.emptyList();
      Gson gson = GsonFactory.get();
      Type listType = new TypeToken<List<Category>>(){}.getType();
      return gson.fromJson(gson.toJson(response.getData()), listType);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return Collections.emptyList();
    }
  }

  public List<City> getCityOptions()
  {
    try
    {
      Response response = ServerConnection.getInstance()
          .send(new Request("GET_CITIES", Map.of()));
      if (!response.isOk()) return Collections.emptyList();
      Gson gson = GsonFactory.get();
      Type listType = new TypeToken<List<City>>(){}.getType();
      return gson.fromJson(gson.toJson(response.getData()), listType);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return Collections.emptyList();
    }
  }

  /**
   * Sends DELETE_EVENT to the server and deletes the event from the database.
   * Returns true on success, false if the server returned an error.
   * The error message is available via getLastErrors() on failure.
   */
  public boolean deleteEvent(int eventId)
  {
    try
    {
      Response response = ServerConnection.getInstance()
          .send(new Request("DELETE_EVENT", Map.of("eventId", eventId)));
      if (!response.isOk())
      {
        lastErrors.add(new FieldError("_general", response.getMessage()));
        return false;
      }
      return true;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      lastErrors.add(new FieldError("_general", "Could not delete event: " + e.getMessage()));
      return false;
    }
  }

  public List<FieldError> getLastErrors() { return lastErrors; }
}