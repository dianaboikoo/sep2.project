package Model;

import java.time.LocalDateTime;

public class EventListDto
{
    private int eventId;
    private String name;
    private LocalDateTime dateTime;
    private String venue;
    private String address;
    private String categoryName;
    private String cityName;
    private int totalTickets;
    private int ticketsSold;

    public EventListDto(int eventId, String name, LocalDateTime dateTime,
                        String venue, String address, String categoryName, String cityName,
                        int totalTickets, int ticketsSold)
    {
        this.eventId = eventId;
        this.name = name;
        this.dateTime = dateTime;
        this.venue = venue;
        this.address = address;
        this.categoryName = categoryName;
        this.cityName = cityName;
        this.totalTickets = totalTickets;
        this.ticketsSold = ticketsSold;
    }

    public int getEventId() { return eventId; }
    public String getName() { return name; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getVenue() { return venue; }
    public String getAddress() { return address; }
    public String getCategoryName() { return categoryName; }
    public String getCityName() { return cityName; }
    public int getTotalTickets() { return totalTickets; }
    public int getTicketsSold() { return ticketsSold; }
    public int getAvailableTickets() { return totalTickets - ticketsSold; }
    public boolean isSoldOut() { return getAvailableTickets() == 0; }
}