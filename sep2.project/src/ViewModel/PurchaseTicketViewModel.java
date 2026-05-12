package ViewModel;

import Model.Ticket;
import Model.TicketService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PurchaseTicketViewModel
{
    private final TicketService ticketService;
    private List<FieldError> lastErrors = new ArrayList<>();

    public PurchaseTicketViewModel(TicketService ticketService)
    {
        this.ticketService = ticketService;
    }

    public List<Ticket> purchaseTicket(int eventId, String userEmail, int quantity)
    {
        lastErrors = new ArrayList<>();
        try
        {
            return ticketService.purchaseTicket(eventId, userEmail, quantity);
        }
        catch (IllegalArgumentException | IllegalStateException e)
        {
            lastErrors.add(new FieldError("quantity", e.getMessage()));
            return Collections.emptyList();
        }
        catch (Exception e)
        {
            lastErrors.add(new FieldError("_general", "Purchase failed: " + e.getMessage()));
            return Collections.emptyList();
        }
    }

    public List<FieldError> getLastErrors() { return lastErrors; }
}
