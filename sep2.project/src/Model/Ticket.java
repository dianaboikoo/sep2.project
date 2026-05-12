package Model;

import java.time.LocalDateTime;

public class Ticket
{
    private String ticketId;
    private int eventId;
    private String userEmail;
    private LocalDateTime purchaseDate;
    private int quantity;
    private TicketStatus status;

    public Ticket(String ticketId, int eventId, String userEmail,
                  LocalDateTime purchaseDate, int quantity, TicketStatus status)
    {
        this.ticketId = ticketId;
        this.eventId = eventId;
        this.userEmail = userEmail;
        this.purchaseDate = purchaseDate;
        this.quantity = quantity;
        this.status = status;
    }

    public String getTicketId() { return ticketId; }
    public int getEventId() { return eventId; }
    public String getUserEmail() { return userEmail; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public int getQuantity() { return quantity; }
    public TicketStatus getStatus() { return status; }
}