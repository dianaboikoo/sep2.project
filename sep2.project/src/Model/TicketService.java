package Model;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TicketService
{
    private final TicketRepository ticketRepository;
    private final EventRepositoryImpl eventRepository;

    public TicketService(TicketRepository ticketRepository,
                         EventRepositoryImpl eventRepository)
    {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
    }

    public List<Ticket> purchaseTicket(int eventId, String userEmail,
                                       int quantity) throws SQLException
    {
        if (quantity <= 0)
        {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        if (quantity > 10)
        {
            throw new IllegalArgumentException("Cannot purchase more than 10 tickets at once");
        }

        // atomic: only increments if enough tickets are left
        boolean success = eventRepository.updateTicketsSold(eventId, quantity);
        if (!success)
        {
            throw new IllegalStateException(
                    "Not enough tickets available for this event");
        }

        String ticketId = UUID.randomUUID().toString();
        Ticket ticket = new Ticket(
                ticketId,
                eventId,
                userEmail,
                LocalDateTime.now(),
                quantity,
                TicketStatus.ACTIVE
        );

        ticketRepository.save(ticket);
        return List.of(ticket);
    }

    public List<Ticket> getTicketsByUser(String email) throws SQLException
    {
        List<Ticket> raw = ticketRepository.findByUserEmail(email);
        List<Ticket> result = new ArrayList<>();
        for (Ticket ticket : raw)
        {
            result.add(resolveExpiry(ticket));
        }
        return result;
    }

    // expiry is computed on load, no point writing it back to the DB
    private Ticket resolveExpiry(Ticket ticket) throws SQLException
    {
        if (ticket.getStatus() != TicketStatus.ACTIVE)
        {
            return ticket;
        }
        EventDetailDto event = eventRepository.findPublishedById(ticket.getEventId());
        if (event != null && LocalDateTime.now().isAfter(event.getDateTime()))
        {
            return ticket.withStatus(TicketStatus.EXPIRED);
        }
        return ticket;
    }

    public TicketSalesDto getSalesReport(int eventId) throws SQLException
    {
        EventDetailDto event = eventRepository.findPublishedById(eventId);
        if (event == null)
        {
            throw new IllegalStateException("Event not found: " + eventId);
        }

        List<Ticket> tickets = ticketRepository.findByEventId(eventId);

        int ticketsSold = tickets.stream()
                .mapToInt(Ticket::getQuantity)
                .sum();

        int ticketsRemaining = event.getTotalTickets() - ticketsSold;

        double totalRevenue = ticketsSold * event.getTicketPrice();

        return new TicketSalesDto(
                event.getName(),
                event.getTotalTickets(),
                ticketsSold,
                ticketsRemaining,
                totalRevenue
        );
    }
}