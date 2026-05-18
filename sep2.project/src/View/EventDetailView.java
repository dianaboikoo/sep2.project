package View;

import Model.EventDetailDto;
import ViewModel.EventDetailViewModel;
import ViewModel.PurchaseTicketViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

    private String userEmail;
    private EventDetailDto currentEvent;

    public void init(EventDetailViewModel viewModel, int eventId, String userEmail)
    {
        this.userEmail = userEmail;

        EventDetailDto event = viewModel.getEvent(eventId);
        this.currentEvent = event;

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
        ticketPriceLabel.setText(String.format("DKK %.2f", event.getTicketPrice()));
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
        if (currentEvent == null) return;
        try
        {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/View/PurchaseTicketView.fxml"));
            Scene scene = new Scene(loader.load());

            PurchaseTicketView purchaseView = loader.getController();
            PurchaseTicketViewModel purchaseVM = new PurchaseTicketViewModel();
            purchaseView.init(purchaseVM, currentEvent, userEmail);

            Stage stage = new Stage();
            stage.setTitle("Buy Ticket");
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
