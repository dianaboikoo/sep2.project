package Model;

import java.time.LocalDateTime;

public class Event
{
  private int eventId;
  private String name;
  private String description;
  private LocalDateTime dateTime;
  private String venue;
  private String address;
  private double ticketPrice;
  private int totalTickets;
  private int ticketsSold;
  private EventStatus status;
  private String imageURL;
  private String categoryName;

  public Event(int eventId, String name, String description,
      LocalDateTime dateTime, String venue, String address, String categoryName,
      double ticketPrice, int totalTickets, int ticketsSold,
      String imageURL, EventStatus status)
  {
    this.eventId = eventId;
    this.name = name;
    this.description = description;
    this.dateTime = dateTime;
    this.venue = venue;
    this.address = address;
    this.categoryName = categoryName;
    this.ticketPrice = ticketPrice;
    this.totalTickets = totalTickets;
    this.ticketsSold = ticketsSold;
    this.status = status;
    this.imageURL = imageURL;

  }

  public boolean publish()
  {
    if (status == EventStatus.DRAFT)
    {
      status = EventStatus.PUBLISHED;
      return true;
    }
    return false;
  }

  public int getAvailableTickets()
  {
    return totalTickets - ticketsSold;
  }

  // getters
  public int getEventId() { return eventId; }
  public String getName() { return name; }
  public String getDescription() { return description; }
  public LocalDateTime getDateTime() { return dateTime; }
  public String getVenue() { return venue; }
  public String getAddress() { return address; }
  public String getCategoryName() { return  categoryName; }
  public double getTicketPrice() { return ticketPrice; }
  public int getTotalTickets() { return totalTickets; }
  public int getTicketsSold() { return ticketsSold; }
  public EventStatus getStatus() { return status; }
  public String getImageURL() { return imageURL; }
}