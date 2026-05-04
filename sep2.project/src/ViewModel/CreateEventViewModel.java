package ViewModel;
import Model.Event;
import Model.EventRepository;
public class CreateEventViewModel
{
  private CreateEventForm createEventForm;
  private EventRepository repository;

  public CreateEventViewModel(EventRepository repository)
  {
    this.repository = repository;
  }

  public void onCreateEvent()
  {
    // validate and create Event from form, then save via repository
  }

  public void updateField(String field, String val)
  {
    // update the corresponding field in createEventForm
  }
}