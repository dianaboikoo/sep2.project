package View;

import Model.EventDetailDto;
import Model.Ticket;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class DigitalTicketView
{
    @FXML private Label eventNameLabel;
    @FXML private Label dateTimeLabel;
    @FXML private Label venueLabel;
    @FXML private Label addressLabel;
    @FXML private Label ticketIdLabel;
    @FXML private Label holderLabel;
    @FXML private Label quantityLabel;
    @FXML private Label purchaseDateLabel;
    @FXML private Label totalPriceLabel;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm");

    public void init(Ticket ticket, EventDetailDto event)
    {
        eventNameLabel.setText(event.getName());

        dateTimeLabel.setText(
                event.getDateTime() == null ? "" : event.getDateTime().format(FORMATTER));
        venueLabel.setText(event.getVenue());
        addressLabel.setText(event.getAddress());

        ticketIdLabel.setText(ticket.getTicketId());

        holderLabel.setText(ticket.getUserEmail());
        quantityLabel.setText(String.valueOf(ticket.getQuantity()));

        purchaseDateLabel.setText(
                ticket.getPurchaseDate() == null ? "" : ticket.getPurchaseDate().format(FORMATTER));

        double total = ticket.getQuantity() * event.getTicketPrice();
        totalPriceLabel.setText(String.format("DKK %.2f", total));
    }

    @FXML
    private void onClose()
    {
        Stage stage = (Stage) eventNameLabel.getScene().getWindow();
        stage.close();
    }
}
