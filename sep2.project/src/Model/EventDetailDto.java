package Model;

import java.time.LocalDateTime;

public class EventDetailDto
{
    private int eventId;
    private String name;
    private String description;
    private LocalDateTime dateTime;
    private String venue;
    private String address;
    private String categoryName;
    private String cityName;
    private double ticketPrice;
    private int totalTickets;
    private int ticketsSold;

    public EventDetailDto(int eventId, String name, String description,
                          LocalDateTime dateTime, String venue, String address,
                          String categoryName, String cityName, double ticketPrice,
                          int totalTickets, int ticketsSold)
    {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.dateTime = dateTime;
        this.venue = venue;
        this.address = address;
        this.categoryName = categoryName;
        this.cityName = cityName;
        this.ticketPrice = ticketPrice;
        this.totalTickets = totalTickets;
        this.ticketsSold = ticketsSold;
    }

    public int getEventId() { return eventId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getVenue() { return venue; }
    public String getAddress() { return address; }
    public String getCategoryName() { return categoryName; }
    public String getCityName() { return cityName; }
    public double getTicketPrice() { return ticketPrice; }
    public int getTotalTickets() { return totalTickets; }
    public int getTicketsSold() { return ticketsSold; }
    public int getAvailableTickets() { return totalTickets - ticketsSold; }
    public boolean isSoldOut() { return getAvailableTickets() == 0; }
}