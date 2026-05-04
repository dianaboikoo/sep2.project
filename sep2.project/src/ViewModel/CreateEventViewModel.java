package ViewModel;

import Model.Event;
import Model.EventRepository;
import Model.EventStatus;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CreateEventViewModel
{
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private CreateEventForm form;
    private EventRepository repository;
    private EventValidator validator;
    private List<FieldError> lastErrors;

    public CreateEventViewModel(EventRepository repository)
    {
        this.repository = repository;
        this.form = new CreateEventForm();
        this.validator = new EventValidator();
        this.lastErrors = new ArrayList<>();
    }

    public void updateField(String field, String value)
    {
        switch (field)
        {
            case "name":         form.setName(value); break;
            case "description":  form.setDescription(value); break;
            case "dateTime":     form.setDateTime(value); break;
            case "venue":        form.setVenue(value); break;
            case "address":      form.setAddress(value); break;
            case "ticketPrice":  form.setTicketPrice(value); break;
            case "totalTickets": form.setTotalTickets(value); break;
            case "imageURL":     form.setImageURL(value); break;
            default:
                System.err.println("Unknown form field: " + field);
        }
    }

    public boolean onCreateEvent()
    {
        lastErrors = validator.validate(form);

        if (!lastErrors.isEmpty())
        {
            return false;
        }

        // Validation passed, safe to parse and build the Event
        Event event = new Event(
                0, // new event, no ID yet
                form.getName().trim(),
                form.getDescription().trim(),
                LocalDateTime.parse(form.getDateTime().trim(), DATE_TIME_FORMAT),
                form.getVenue().trim(),
                form.getAddress().trim(),
                Double.parseDouble(form.getTicketPrice().trim()),
                Integer.parseInt(form.getTotalTickets().trim()),
                0, // ticketsSold = 0 for new event
                form.getImageURL().trim().isEmpty() ? null : form.getImageURL().trim(),
                EventStatus.DRAFT
        );

        try
        {
            repository.save(event);
            // reset form for next entry
            this.form = new CreateEventForm();
            return true;
        }
        catch (SQLException e)
        {
            lastErrors.add(new FieldError("_general",
                    "Could not save event: " + e.getMessage()));
            return false;
        }
    }

    public List<FieldError> getLastErrors()
    {
        return lastErrors;
    }

    public CreateEventForm getForm()
    {
        return form;
    }
}