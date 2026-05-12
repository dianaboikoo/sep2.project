package ViewModel;

import Model.TicketSalesDto;
import Model.TicketService;

import java.sql.SQLException;

public class TicketSalesViewModel
{
  private TicketService ticketService;

  public TicketSalesViewModel(TicketService ticketService)
  {
    this.ticketService = ticketService;
  }

  public TicketSalesDto getSalesReport(int eventId)
  {
    try
    {
      return ticketService.getSalesReport(eventId);
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      return null;
    }
  }
}