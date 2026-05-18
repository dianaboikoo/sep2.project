package ViewModel;

import Client.ServerConnection;
import Model.EventDetailDto;
import Model.Ticket;
import Shared.GsonFactory;
import Shared.Request;
import Shared.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MyTicketsViewModel
{
    public MyTicketsViewModel()
    {
        // No dependencies — communicates via ServerConnection
    }

    public List<Ticket> getMyTickets(String email)
    {
        try
        {
            Response response = ServerConnection.getInstance()
                    .send(new Request("GET_MY_TICKETS", Map.of("userEmail", email)));
            if (!response.isOk()) return Collections.emptyList();
            Gson gson = GsonFactory.get();
            Type listType = new TypeToken<List<Ticket>>(){}.getType();
            return gson.fromJson(gson.toJson(response.getData()), listType);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public EventDetailDto getEventById(int eventId)
    {
        try
        {
            Response response = ServerConnection.getInstance()
                    .send(new Request("GET_EVENT_BY_ID", Map.of("eventId", eventId)));
            if (!response.isOk()) return null;
            Gson gson = GsonFactory.get();
            return gson.fromJson(gson.toJson(response.getData()), EventDetailDto.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Helper used by MyTicketsView to render the Event Name column.
     * Sends GET_EVENT_BY_ID and returns just the name string.
     */
    public String getEventName(int eventId)
    {
        EventDetailDto event = getEventById(eventId);
        return event == null ? "(unknown event)" : event.getName();
    }
}
