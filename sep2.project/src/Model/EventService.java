package Model;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class EventService
{
  private final EventRepository eventRepository;

  public EventService(EventRepository eventRepository)
  {
    this.eventRepository = eventRepository;
  }

  public List<EventListDto> getAllEvents() throws SQLException
  {
    return eventRepository.findAllPublished();
  }

  public List<EventListDto> getFilteredEvents(String category, Integer zipCode,
      LocalDate from, LocalDate to) throws SQLException
  {
    return eventRepository.findAllPublishedFiltered(category, zipCode, from, to);
  }

  public EventDetailDto getEventById(int id) throws SQLException
  {
    return eventRepository.findPublishedById(id);
  }

  public List<City> getAllCities() throws SQLException
  {
    return eventRepository.findAllCities();
  }
}