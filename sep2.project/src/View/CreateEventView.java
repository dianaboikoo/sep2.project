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
    viewModel.updateField("name", nameField.getText());
    viewModel.updateField("description", descriptionField.getText());
    viewModel.updateField("date", dateField.getText());
    viewModel.updateField("time", timeField.getText());
    viewModel.updateField("location", locationField.getText());
    viewModel.updateField("ticketPrice", ticketPriceField.getText());
    viewModel.updateField("totalTickets", totalTicketsField.getText());
    viewModel.updateField("imageURL", imageURLField.getText());
    viewModel.onCreateEvent();

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