package ViewModel;

import Model.Event;
import Model.EventRepository;
import Model.EventStatus;

public class CreateEventViewModel
{
  private EventRepository repository;

  public CreateEventViewModel(EventRepository repository)
  {
    this.repository = repository;
  }

  public void createEvent(CreateEventForm form)
  {
    try
    {
      double price = Double.parseDouble(form.getTicketPrice());
      int tickets = Integer.parseInt(form.getTotalTickets());

      Event event = new Event(
          form.getName(),
          form.getDescription(),
          form.getDate(),
          form.getTime(),
          form.getLocation(),
          price,
          tickets,
          tickets, //availableTickets defaults to totalTickets when creating
          form.getImageURL(),
          EventStatus.DRAFT
      );

      repository.save(event);

      System.out.println("Event created: " + form.getName());
    }
    catch (Exception e)
    {
      System.out.println("Error creating event. Check input.");
    }
  }
}