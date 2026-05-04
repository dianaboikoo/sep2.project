package ViewModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class EventValidator
{
    // length limits
    private static final int NAME_MAX = 200;
    private static final int DESCRIPTION_MAX = 2000;
    private static final int VENUE_MAX = 200;
    private static final int ADDRESS_MAX = 200;
    private static final int IMAGE_URL_MAX = 500;

    // expected format: "yyyy-MM-dd HH:mm"  combined by the View before reaching the form
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public List<FieldError> validate(CreateEventForm form)
    {
        List<FieldError> errors = new ArrayList<>();

        validateName(form.getName(), errors);
        validateDescription(form.getDescription(), errors);
        validateDateTime(form.getDateTime(), errors);
        validateVenue(form.getVenue(), errors);
        validateAddress(form.getAddress(), errors);
        validateTicketPrice(form.getTicketPrice(), errors);
        validateTotalTickets(form.getTotalTickets(), errors);
        validateImageURL(form.getImageURL(), errors);

        return errors;
    }

    private void validateName(String name, List<FieldError> errors)
    {
        if (name == null || name.trim().isEmpty())
        {
            errors.add(new FieldError("name", "Name is required"));
            return;
        }
        if (name.trim().length() > NAME_MAX)
        {
            errors.add(new FieldError("name",
                    "Name must be at most " + NAME_MAX + " characters"));
        }
    }

    private void validateDescription(String description, List<FieldError> errors)
    {
        if (description == null || description.trim().isEmpty())
        {
            errors.add(new FieldError("description", "Description is required"));
            return;
        }
        if (description.trim().length() > DESCRIPTION_MAX)
        {
            errors.add(new FieldError("description",
                    "Description must be at most " + DESCRIPTION_MAX + " characters"));
        }
    }

    private void validateDateTime(String dateTime, List<FieldError> errors)
    {
        if (dateTime == null || dateTime.trim().isEmpty())
        {
            errors.add(new FieldError("dateTime", "Date and time are required"));
            return;
        }

        LocalDateTime parsed;
        try
        {
            parsed = LocalDateTime.parse(dateTime.trim(), DATE_TIME_FORMAT);
        }
        catch (DateTimeParseException e)
        {
            errors.add(new FieldError("dateTime",
                    "Date and time must be in format yyyy-MM-dd HH:mm (e.g. 2026-06-15 20:30)"));
            return;
        }

        if (!parsed.isAfter(LocalDateTime.now()))
        {
            errors.add(new FieldError("dateTime",
                    "Event must be scheduled in the future"));
        }
    }

    private void validateVenue(String venue, List<FieldError> errors)
    {
        if (venue == null || venue.trim().isEmpty())
        {
            errors.add(new FieldError("venue", "Venue is required"));
            return;
        }
        if (venue.trim().length() > VENUE_MAX)
        {
            errors.add(new FieldError("venue",
                    "Venue must be at most " + VENUE_MAX + " characters"));
        }
    }

    private void validateAddress(String address, List<FieldError> errors)
    {
        if (address == null || address.trim().isEmpty())
        {
            errors.add(new FieldError("address", "Address is required"));
            return;
        }
        if (address.trim().length() > ADDRESS_MAX)
        {
            errors.add(new FieldError("address",
                    "Address must be at most " + ADDRESS_MAX + " characters"));
        }
    }

    private void validateTicketPrice(String ticketPrice, List<FieldError> errors)
    {
        if (ticketPrice == null || ticketPrice.trim().isEmpty())
        {
            errors.add(new FieldError("ticketPrice", "Ticket price is required"));
            return;
        }

        double price;
        try
        {
            price = Double.parseDouble(ticketPrice.trim());
        }
        catch (NumberFormatException e)
        {
            errors.add(new FieldError("ticketPrice",
                    "Ticket price must be a valid number (e.g. 99.50)"));
            return;
        }

        if (price < 0)
        {
            errors.add(new FieldError("ticketPrice",
                    "Ticket price cannot be negative"));
        }
    }

    private void validateTotalTickets(String totalTickets, List<FieldError> errors)
    {
        if (totalTickets == null || totalTickets.trim().isEmpty())
        {
            errors.add(new FieldError("totalTickets", "Total tickets is required"));
            return;
        }

        int total;
        try
        {
            total = Integer.parseInt(totalTickets.trim());
        }
        catch (NumberFormatException e)
        {
            errors.add(new FieldError("totalTickets",
                    "Total tickets must be a whole number"));
            return;
        }

        if (total <= 0)
        {
            errors.add(new FieldError("totalTickets",
                    "Total tickets must be greater than zero"));
        }
    }

    private void validateImageURL(String imageURL, List<FieldError> errors)
    {
        // imageURL is OPTIONAL — empty/null is valid
        if (imageURL == null || imageURL.trim().isEmpty())
        {
            return;
        }

        if (imageURL.trim().length() > IMAGE_URL_MAX)
        {
            errors.add(new FieldError("imageURL",
                    "Image URL must be at most " + IMAGE_URL_MAX + " characters"));
        }
    }
}