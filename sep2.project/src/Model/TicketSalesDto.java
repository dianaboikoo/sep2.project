package Model;

public class TicketSalesDto
{
    private String eventName;
    private int totalTickets;
    private int ticketsSold;
    private int ticketsRemaining;
    private double totalRevenue;

    public TicketSalesDto(String eventName, int totalTickets,
                          int ticketsSold, int ticketsRemaining, double totalRevenue)
    {
        this.eventName = eventName;
        this.totalTickets = totalTickets;
        this.ticketsSold = ticketsSold;
        this.ticketsRemaining = ticketsRemaining;
        this.totalRevenue = totalRevenue;
    }

    public String getEventName() { return eventName; }
    public int getTotalTickets() { return totalTickets; }
    public int getTicketsSold() { return ticketsSold; }
    public int getTicketsRemaining() { return ticketsRemaining; }
    public double getTotalRevenue() { return totalRevenue; }
}