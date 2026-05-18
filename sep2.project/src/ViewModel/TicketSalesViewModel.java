package ViewModel;

import Client.ServerConnection;
import Model.TicketSalesDto;
import Shared.GsonFactory;
import Shared.Request;
import Shared.Response;
import com.google.gson.Gson;

import java.util.Map;

public class TicketSalesViewModel
{
  public TicketSalesViewModel()
  {
    // No dependencies — communicates via ServerConnection
  }

  public TicketSalesDto getSalesReport(int eventId)
  {
    try
    {
      Response response = ServerConnection.getInstance()
          .send(new Request("GET_SALES_REPORT", Map.of("eventId", eventId)));
      if (!response.isOk())
      {
        return null;
      }
      Gson gson = GsonFactory.get();
      return gson.fromJson(gson.toJson(response.getData()), TicketSalesDto.class);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }
  }
}