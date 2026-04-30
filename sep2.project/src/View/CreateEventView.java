package View;

import ViewModel.CreateEventViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CreateEventView
{
  @FXML private TextField nameField;
  @FXML private TextArea descriptionField;
  @FXML private TextField dateField;
  @FXML private TextField timeField;
  @FXML private TextField locationField;
  @FXML private TextField ticketPriceField;
  @FXML private TextField totalTicketsField;
  @FXML private TextField imageURLField;

  private CreateEventViewModel viewModel;

  public void init(CreateEventViewModel viewModel)
  {
    this.viewModel = viewModel;
  }

  @FXML
  private void onCreateEvent()
  {
    viewModel.createEvent(
        nameField.getText(),
        descriptionField.getText(),
        dateField.getText(),
        timeField.getText(),
        locationField.getText(),
        ticketPriceField.getText(),
        totalTicketsField.getText(),
        imageURLField.getText()
    );

    nameField.clear();
    descriptionField.clear();
    dateField.clear();
    timeField.clear();
    locationField.clear();
    ticketPriceField.clear();
    totalTicketsField.clear();
    imageURLField.clear();
  }
}