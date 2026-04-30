package ViewModel;

import Model.Event;
import Model.EventRepository;

public class CreateEventViewModel
{
  private EventRepository repository;

  public CreateEventViewModel(EventRepository repository)
  {
    this.repository = repository;
  }

  public void createEvent(String name, String description, String date,
      String time, String location, String ticketPrice,
      String totalTickets, String imageURL)
  {
    try
    {
      double price = Double.parseDouble(ticketPrice);
      int tickets = Integer.parseInt(totalTickets);

      Event event = new Event(name, description, date, time,
          location, price, tickets, imageURL);

      repository.addEvent(event);

      System.out.println("Event created: " + name);
    }
    catch (Exception e)
    {
      System.out.println("Error creating event. Check input.");
    }
  }
}