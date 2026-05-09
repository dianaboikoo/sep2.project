package View;

import Model.EventDetailDto;
import ViewModel.EventDetailViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class EventDetailView
{
    @FXML private Label nameLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label dateTimeLabel;
    @FXML private Label venueLabel;
    @FXML private Label addressLabel;
    @FXML private Label categoryLabel;
    @FXML private Label cityLabel;
    @FXML private Label ticketPriceLabel;
    @FXML private Label availableTicketsLabel;
    @FXML private Label soldOutLabel;
    @FXML private Label notFoundLabel;
    @FXML private Button buyTicketButton;

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm");

    public void init(EventDetailViewModel viewModel, int eventId)
    {
        EventDetailDto event = viewModel.getEvent(eventId);

        if (event == null)
        {
            notFoundLabel.setVisible(true);
            buyTicketButton.setVisible(false);
            return;
        }

        notFoundLabel.setVisible(false);

        nameLabel.setText(event.getName());
        descriptionLabel.setText(event.getDescription());
        dateTimeLabel.setText(event.getDateTime() == null ? "" : event.getDateTime().format(FORMATTER));
        venueLabel.setText(event.getVenue());
        addressLabel.setText(event.getAddress());
        categoryLabel.setText(event.getCategoryName());
        cityLabel.setText(event.getCityName());
        ticketPriceLabel.setText(String.format("€ %.2f", event.getTicketPrice()));
        availableTicketsLabel.setText(event.getAvailableTickets() + " tickets left");

        if (event.isSoldOut())
        {
            soldOutLabel.setVisible(true);
            buyTicketButton.setDisable(true);
        }
        else
        {
            soldOutLabel.setVisible(false);
            buyTicketButton.setDisable(false);
        }
    }

    @FXML
    private void onBack()
    {
        Stage stage = (Stage) nameLabel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onBuyTicket()
    {
        // placeholder — ticket purchase is a future use case
    }
}
