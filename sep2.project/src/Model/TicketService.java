package Model;

import java.sql.SQLException;
import java.time.LocalDateTime;
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
        // Validate quantity
        if (quantity <= 0)
        {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        if (quantity > 10)
        {
            throw new IllegalArgumentException("Cannot purchase more than 10 tickets at once");
        }

        // Atomically check availability and increment tickets_sold
        // If this returns false, the event is sold out or quantity exceeds availability
        boolean success = eventRepository.updateTicketsSold(eventId, quantity);
        if (!success)
        {
            throw new IllegalStateException(
                    "Not enough tickets available for this event");
        }

        // Generate a unique ticket ID and create the ticket
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
        return ticketRepository.findByUserEmail(email);
    }

    public TicketSalesDto getSalesReport(int eventId) throws SQLException
    {
        // Get event details for name, totalTickets, ticketPrice
        EventDetailDto event = eventRepository.findPublishedById(eventId);
        if (event == null)
        {
            throw new IllegalStateException("Event not found: " + eventId);
        }

        // Get all tickets for this event
        List<Ticket> tickets = ticketRepository.findByEventId(eventId);

        // Calculate totals
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