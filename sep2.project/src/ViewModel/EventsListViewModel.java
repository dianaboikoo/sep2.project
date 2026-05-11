package ViewModel;

import Model.EventListDto;
import Model.EventService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class EventsListViewModel
{
    private final EventService eventService;

    public EventsListViewModel(EventService eventService)
    {
        this.eventService = eventService;
    }

    public List<EventListDto> getPublishedEvents()
    {
        return getFilteredEvents(null, null, null, null);
    }

    public List<EventListDto> getFilteredEvents(String category, Integer zipCode, LocalDate from, LocalDate to)
    {
        try
        {
            return eventService.getFilteredEvents(category, zipCode, from, to);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
