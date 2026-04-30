package ViewModel;

public class CreateEventForm
{
  private String name;
  private String description;
  private String dateAndTime;
  private String location;
  private String ticketPrice;
  private String capacity;

  public CreateEventForm(String name, String description, String dateAndTime,
      String location, String ticketPrice, String capacity)
  {
    this.name = name;
    this.description = description;
    this.dateAndTime = dateAndTime;
    this.location = location;
    this.ticketPrice = ticketPrice;
    this.capacity = capacity;
  }

  public String getName() { return name; }
  public String getDescription() { return description; }
  public String getDateAndTime() { return dateAndTime; }
  public String getLocation() { return location; }
  public String getTicketPrice() { return ticketPrice; }
  public String getCapacity() { return capacity; }
}