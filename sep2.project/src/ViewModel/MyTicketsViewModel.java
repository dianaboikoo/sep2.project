package ViewModel;

import Model.EventDetailDto;
import Model.EventService;
import Model.Ticket;
import Model.TicketService;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class MyTicketsViewModel
{
    private final TicketService ticketService;
    private final EventService eventService;

    public MyTicketsViewModel(TicketService ticketService, EventService eventService)
    {
        this.ticketService = ticketService;
        this.eventService = eventService;
    }

    public List<Ticket> getMyTickets(String email)
    {
        try
        {
            return ticketService.getTicketsByUser(email);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Helper used by MyTicketsView to render the Event Name column.
    // ViewModel still "depends on TicketService" per spec; EventService is only
    // used to resolve the eventId → event name lookup, since Ticket carries the id only.
    public String getEventName(int eventId)
    {
        try
        {
            EventDetailDto event = eventService.getEventById(eventId);
            return event == null ? "(unknown event)" : event.getName();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return "(error)";
        }
    }
}
