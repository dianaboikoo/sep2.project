package ViewModel;

import Model.EventDetailDto;
import Model.EventService;

import java.sql.SQLException;

public class EventDetailViewModel
{
    private final EventService eventService;

    public EventDetailViewModel(EventService eventService)
    {
        this.eventService = eventService;
    }

    public EventDetailDto getEvent(int id)
    {
        try
        {
            return eventService.getEventById(id);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
