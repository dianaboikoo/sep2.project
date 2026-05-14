package ViewModel;

import Model.EventDetailDto;
import Model.EventRepository;
import Model.EventStatus;
import Model.Event;
import Model.Category;
import Model.CategoryService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EditEventViewModel
{
  private static final DateTimeFormatter DATE_TIME_FORMAT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  private CreateEventForm form;
  private EventRepository repository;
  private EventValidator validator;
  private List<FieldError> lastErrors;
  private CategoryService categoryService;
  private int currentEventId;

  public EditEventViewModel(EventRepository repository, CategoryService categoryService)
  {
    this.repository = repository;
    this.categoryService = categoryService;
    this.form = new CreateEventForm();
    this.validator = new EventValidator();
    this.lastErrors = new ArrayList<>();
  }

  public EventDetailDto loadEvent(int eventId) throws SQLException
  {
    EventDetailDto event = repository.findPublishedById(eventId);
    if (event == null)
    {
      throw new IllegalArgumentException("Event not found: " + eventId);
    }

    this.currentEventId = eventId;

    form.setName(event.getName());
    form.setDescription(event.getDescription());
    form.setDateTime(event.getDateTime().format(DATE_TIME_FORMAT));
    form.setVenue(event.getVenue());
    form.setAddress(event.getAddress());
    form.setCategoryName(event.getCategoryName());
    form.setTicketPrice(String.valueOf(event.getTicketPrice()));
    form.setTotalTickets(String.valueOf(event.getTotalTickets()));

    return event;
  }

  public void updateField(String field, String value)
  {
    switch (field)
    {
      case "name":         form.setName(value); break;
      case "description":  form.setDescription(value); break;
      case "dateTime":     form.setDateTime(value); break;
      case "venue":        form.setVenue(value); break;
      case "address":      form.setAddress(value); break;
      case "category":     form.setCategoryName(value); break;
      case "zipCode":      form.setZipCode(value); break;
      case "ticketPrice":  form.setTicketPrice(value); break;
      case "totalTickets": form.setTotalTickets(value); break;
      case "imageURL":     form.setImageURL(value); break;
      default:
        System.err.println("Unknown form field: " + field);
    }
  }

  public boolean onUpdateEvent()
  {
    lastErrors = validator.validate(form);

    if (!lastErrors.isEmpty())
    {
      return false;
    }

    Integer zipCode = null;
    String rawZip = form.getZipCode() == null ? "" : form.getZipCode().trim();
    if (!rawZip.isEmpty())
    {
      try { zipCode = Integer.parseInt(rawZip); }
      catch (NumberFormatException e)
      {
        lastErrors.add(new FieldError("zipCode", "City ZIP must be a 4-digit number"));
        return false;
      }
    }

    Event event = new Event(
        currentEventId,
        form.getName().trim(),
        form.getDescription().trim(),
        LocalDateTime.parse(form.getDateTime().trim(), DATE_TIME_FORMAT),
        form.getVenue().trim(),
        form.getAddress().trim(),
        form.getCategoryName().trim(),
        Double.parseDouble(form.getTicketPrice().trim()),
        Integer.parseInt(form.getTotalTickets().trim()),
        0,
        form.getImageURL() != null && !form.getImageURL().trim().isEmpty()
            ? form.getImageURL().trim() : null,
        EventStatus.PUBLISHED,
        zipCode
    );

    try
    {
      repository.update(event);
      return true;
    }
    catch (SQLException e)
    {
      lastErrors.add(new FieldError("_general",
          "Could not update event: " + e.getMessage()));
      return false;
    }
  }

  public List<Category> getAllCategories()
  {
    try
    {
      return categoryService.findAll();
    }
    catch (SQLException e)
    {
      return new ArrayList<>();
    }
  }

  public List<FieldError> getLastErrors()
  {
    return lastErrors;
  }

  public CreateEventForm getForm()
  {
    return form;
  }
}