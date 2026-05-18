package ViewModel;

import Client.ServerConnection;
import Model.Category;
import Shared.GsonFactory;
import Shared.Request;
import Shared.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateEventViewModel
{
    private CreateEventForm form;
    private EventValidator validator;
    private List<FieldError> lastErrors;

    public CreateEventViewModel()
    {
        // No dependencies — communicates via ServerConnection
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
            case "category":     form.setCategoryName(value); break;
            case "zipCode":      form.setZipCode(value); break;
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

        // Parse optional zip code
        Integer zipCode = null;
        String rawZip = form.getZipCode() == null ? "" : form.getZipCode().trim();
        if (!rawZip.isEmpty())
        {
            try { zipCode = Integer.parseInt(rawZip); }
            catch (NumberFormatException e)
            {
                lastErrors.add(new FieldError("zipCode", "City ZIP must be a 4-digit number"));
                return false;
            }
        }

        // Build payload and send to server
        Map<String, Object> payload = new HashMap<>();
        payload.put("name",         form.getName().trim());
        payload.put("description",  form.getDescription().trim());
        payload.put("dateTime",     form.getDateTime().trim());
        payload.put("venue",        form.getVenue().trim());
        payload.put("address",      form.getAddress().trim());
        payload.put("categoryName", form.getCategoryName().trim());
        payload.put("ticketPrice",  Double.parseDouble(form.getTicketPrice().trim()));
        payload.put("totalTickets", Integer.parseInt(form.getTotalTickets().trim()));
        payload.put("imageURL",
                form.getImageURL() != null && !form.getImageURL().trim().isEmpty()
                        ? form.getImageURL().trim() : null);
        payload.put("zipCode", zipCode);
        payload.put("status", "PUBLISHED");

        try
        {
            Response response = ServerConnection.getInstance()
                    .send(new Request("CREATE_EVENT", payload));
            if (!response.isOk())
            {
                lastErrors.add(new FieldError("_general", response.getMessage()));
                return false;
            }
            // Reset form for next entry
            this.form = new CreateEventForm();
            return true;
        }
        catch (Exception e)
        {
            lastErrors.add(new FieldError("_general",
                    "Could not save event: " + e.getMessage()));
            return false;
        }
    }

    public List<Category> getAllCategories()
    {
        try
        {
            Response response = ServerConnection.getInstance()
                    .send(new Request("GET_CATEGORIES", Map.of()));
            if (!response.isOk()) return new ArrayList<>();
            Gson gson = GsonFactory.get();
            Type listType = new TypeToken<List<Category>>(){}.getType();
            return gson.fromJson(gson.toJson(response.getData()), listType);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new ArrayList<>();
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