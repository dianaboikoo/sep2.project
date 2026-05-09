package Model;

import java.sql.SQLException;
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

    public EventDetailDto getEventById(int id) throws SQLException
    {
        return eventRepository.findPublishedById(id);
    }
}