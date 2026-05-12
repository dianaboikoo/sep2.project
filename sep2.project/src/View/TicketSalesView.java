package View;

import Model.TicketSalesDto;
import ViewModel.TicketSalesViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class TicketSalesView
{
  @FXML private Label eventNameLabel;
  @FXML private Label totalTicketsLabel;
  @FXML private Label ticketsSoldLabel;
  @FXML private Label ticketsRemainingLabel;
  @FXML private Label totalRevenueLabel;

  private TicketSalesViewModel viewModel;

  public void init(TicketSalesViewModel viewModel, int eventId)
  {
    this.viewModel = viewModel;

    TicketSalesDto report = viewModel.getSalesReport(eventId);

    if (report == null)
    {
      eventNameLabel.setText("Could not load report");
      totalTicketsLabel.setText("-");
      ticketsSoldLabel.setText("-");
      ticketsRemainingLabel.setText("-");
      totalRevenueLabel.setText("-");
      return;
    }

    eventNameLabel.setText(report.getEventName());
    totalTicketsLabel.setText(String.valueOf(report.getTotalTickets()));
    ticketsSoldLabel.setText(String.valueOf(report.getTicketsSold()));
    ticketsRemainingLabel.setText(String.valueOf(report.getTicketsRemaining()));
    totalRevenueLabel.setText(String.format("%.2f", report.getTotalRevenue()));
  }

  @FXML
  private void onClose()
  {
    Stage stage = (Stage) eventNameLabel.getScene().getWindow();
    stage.close();
  }
}