package ViewModel;

public class CreateEventForm
{
  private String name;
  private String description;
  private String date;
  private String time;
  private String location;
  private String ticketPrice;
  private String totalTickets;
  private String imageURL;

  public CreateEventForm(String name, String description, String date, Sting Time,
      String location, String ticketPrice, String totalTickets, Sting imageURL)
  {
    this.name = name;
    this.description = description;
    this.date = date;
    this.time = time;
    this.location = location;
    this.ticketPrice = ticketPrice;
    this.totalTickets = totalTickets;
    this.imageURL = imageURL;
  }

  public String getName() { return name; }
  public String getDescription() { return description; }
  public String getDate() { return date; }
  public String getTime() { return time; }
  public String getLocation() { return location; }
  public String getTicketPrice() { return ticketPrice; }
  public String getTotalTickets() { return totalTickets; }
  public String getImageURL() { return iamgeURL; }
}