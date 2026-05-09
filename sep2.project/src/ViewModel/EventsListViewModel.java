package ViewModel;

import Model.EventListDto;
import Model.EventService;

import java.sql.SQLException;
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
        try
        {
            return eventService.getAllEvents();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
