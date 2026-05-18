package ViewModel;

import Client.ServerConnection;
import Model.EventDetailDto;
import Shared.GsonFactory;
import Shared.Request;
import Shared.Response;
import com.google.gson.Gson;

import java.util.Map;

public class EventDetailViewModel
{
    public EventDetailViewModel()
    {
        // No dependencies — communicates via ServerConnection
    }

    public EventDetailDto getEvent(int id)
    {
        try
        {
            Response response = ServerConnection.getInstance()
                    .send(new Request("GET_EVENT_BY_ID", Map.of("eventId", id)));
            if (!response.isOk())
            {
                return null;
            }
            Gson gson = GsonFactory.get();
            return gson.fromJson(gson.toJson(response.getData()), EventDetailDto.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
