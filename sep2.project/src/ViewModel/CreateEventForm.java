package ViewModel;

import java.time.LocalDateTime;

public class CreateEventForm
{
  private String name;
  private String description;
  private double ticketPrice;
  private String imageURL;
  private LocalDateTime dateTime;
  private String venue;
  private String address;
  private int totalTickets;

  public CreateEventForm(String name, String description, double ticketPrice,
      String imageURL, LocalDateTime dateTime, String venue,
      String address, int totalTickets)
  {
    this.name = name;
    this.description = description;
    this.ticketPrice = ticketPrice;
    this.imageURL = imageURL;
    this.dateTime = dateTime;
    this.venue = venue;
    this.address = address;
    this.totalTickets = totalTickets;
  }

  public String getName() { return name; }
  public String getDescription() { return description; }
  public double getTicketPrice() { return ticketPrice; }
  public String getImageURL() { return imageURL; }
  public LocalDateTime getDateTime() { return dateTime; }
  public String getVenue() { return venue; }
  public String getAddress() { return address; }
  public int getTotalTickets() { return totalTickets; }
}