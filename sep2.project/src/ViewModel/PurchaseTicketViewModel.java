package ViewModel;

import Client.ServerConnection;
import Model.Ticket;
import Shared.GsonFactory;
import Shared.Request;
import Shared.Response;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PurchaseTicketViewModel
{
    private List<FieldError> lastErrors = new ArrayList<>();

    public PurchaseTicketViewModel()
    {
        // No dependencies — communicates via ServerConnection
    }

    public List<Ticket> purchaseTicket(int eventId, String userEmail, int quantity)
    {
        lastErrors = new ArrayList<>();
        try
        {
            Response response = ServerConnection.getInstance()
                    .send(new Request("PURCHASE_TICKET", Map.of(
                            "eventId",   eventId,
                            "userEmail", userEmail,
                            "quantity",  quantity)));

            if (!response.isOk())
            {
                String msg = response.getMessage();
                if (msg != null && msg.startsWith("FIELD:quantity:"))
                {
                    lastErrors.add(new FieldError("quantity",
                            msg.substring("FIELD:quantity:".length())));
                }
                else
                {
                    lastErrors.add(new FieldError("_general", msg));
                }
                return Collections.emptyList();
            }

            Gson gson = GsonFactory.get();
            Ticket ticket = gson.fromJson(gson.toJson(response.getData()), Ticket.class);
            return ticket == null ? Collections.emptyList() : List.of(ticket);
        }
        catch (Exception e)
        {
            lastErrors.add(new FieldError("_general", "Purchase failed: " + e.getMessage()));
            return Collections.emptyList();
        }
    }

    public List<FieldError> getLastErrors() { return lastErrors; }
}
