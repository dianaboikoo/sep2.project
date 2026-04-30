package Model;

import java.util.ArrayList;

public class EventRepository
{
  private ArrayList<Event> events;

  public EventRepository()
  {
    events = new ArrayList<>();
  }

  public void addEvent(Event event)
  {
    events.add(event);
  }

  public ArrayList<Event> getEvents()
  {
    return new ArrayList<>(events);
  }
}