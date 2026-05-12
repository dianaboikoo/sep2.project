package View;

import Model.EventDetailDto;
import Model.Ticket;
import ViewModel.FieldError;
import ViewModel.PurchaseTicketViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class PurchaseTicketView
{
    @FXML private Label     eventNameLabel;
    @FXML private Label     ticketPriceLabel;
    @FXML private Label     availableLabel;
    @FXML private TextField quantityField;
    @FXML private Label     errorLabel;
    @FXML private Label     successLabel;

    private PurchaseTicketViewModel viewModel;
    private EventDetailDto event;
    private String userEmail;

    public void init(PurchaseTicketViewModel viewModel,
                     EventDetailDto event,
                     String userEmail)
    {
        this.viewModel = viewModel;
        this.event     = event;
        this.userEmail = userEmail;

        eventNameLabel.setText(event.getName());
        ticketPriceLabel.setText(String.format("DKK %.2f", event.getTicketPrice()));
        availableLabel.setText(event.getAvailableTickets() + " tickets left");
    }

    @FXML
    private void onConfirm()
    {
        hideMessages();

        String quantityText = quantityField.getText().trim();
        if (quantityText.isEmpty())
        {
            showError("Please enter a quantity.");
            return;
        }

        int quantity;
        try
        {
            quantity = Integer.parseInt(quantityText);
        }
        catch (NumberFormatException e)
        {
            showError("Quantity must be a whole number.");
            return;
        }

        List<Ticket> tickets = viewModel.purchaseTicket(
                event.getEventId(), userEmail, quantity);

        List<FieldError> errors = viewModel.getLastErrors();
        if (!errors.isEmpty())
        {
            showError(errors.get(0).getMessage());
        }
        else
        {
            showSuccess("Purchase successful!\nTicket ID: "
                    + tickets.get(0).getTicketId());
            quantityField.setDisable(true);
        }
    }

    @FXML
    private void onCancel()
    {
        Stage stage = (Stage) quantityField.getScene().getWindow();
        stage.close();
    }

    private void showError(String message)
    {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        successLabel.setVisible(false);
        successLabel.setManaged(false);
    }

    private void showSuccess(String message)
    {
        successLabel.setText(message);
        successLabel.setVisible(true);
        successLabel.setManaged(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void hideMessages()
    {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        successLabel.setVisible(false);
        successLabel.setManaged(false);
    }
}
