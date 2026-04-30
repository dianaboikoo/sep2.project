package Model;

public class Event
{
  private int eventId;
  private String name;
  private String description;
  private String date;
  private String time;
  private String location;
  private double ticketPrice;
  private int totalTickets;
  private int availableTickets;
  private EventStatus status;
  private String imageURL;

  public Event(String name, String description, String date, String time,
      String location, double ticketPrice, int totalTickets, String imageURL)
  {
    this.name = name;
    this.description = description;
    this.date = date;
    this.time = time;
    this.location = location;
    this.ticketPrice = ticketPrice;
    this.totalTickets = totalTickets;
    this.availableTickets = totalTickets;
    this.status = EventStatus.DRAFT;
    this.imageURL = imageURL;
  }

  public String getName() { return name; }
  public String getDescription() { return description; }
  public String getDate() { return date; }
  public String getTime() { return time; }
  public String getLocation() { return location; }
  public double getTicketPrice() { return ticketPrice; }
  public int getTotalTickets() { return totalTickets; }
  public int getAvailableTickets() { return availableTickets; }
  public EventStatus getStatus() { return status; }
  public String getImageURL() { return imageURL; }
}