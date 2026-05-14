package View;

import Model.Category;
import Model.EventDetailDto;
import ViewModel.EditEventViewModel;
import ViewModel.FieldError;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class EditEventView
{
  // Input fields
  @FXML private TextField nameField;
  @FXML private TextArea descriptionField;
  @FXML private TextField dateField;
  @FXML private TextField timeField;
  @FXML private TextField venueField;
  @FXML private TextField addressField;
  @FXML private TextField zipCodeField;
  @FXML private ComboBox<String> categoryComboBox;
  @FXML private TextField ticketPriceField;
  @FXML private TextField totalTicketsField;
  @FXML private TextField imageURLField;

  // Error labels
  @FXML private Label nameError;
  @FXML private Label descriptionError;
  @FXML private Label dateTimeError;
  @FXML private Label venueError;
  @FXML private Label addressError;
  @FXML private Label zipCodeError;
  @FXML private Label categoryError;
  @FXML private Label ticketPriceError;
  @FXML private Label totalTicketsError;
  @FXML private Label imageURLError;

  // General message (success / DB errors)
  @FXML private Label generalMessage;

  private EditEventViewModel viewModel;

  public void init(EditEventViewModel viewModel, int eventId)
  {
    this.viewModel = viewModel;
    populateCategoryDropdown();
    prefillFields(eventId);
  }

  // Loads the event from DB and fills all fields with existing values
  private void prefillFields(int eventId)
  {
    try
    {
      EventDetailDto event = viewModel.loadEvent(eventId);

      nameField.setText(event.getName());
      descriptionField.setText(event.getDescription());

      // Split dateTime back into separate date and time fields
      String[] parts = event.getDateTime().toString().split("T");
      dateField.setText(parts[0]);                          // yyyy-MM-dd
      timeField.setText(parts[1].substring(0, 5));          // HH:mm

      venueField.setText(event.getVenue());
      addressField.setText(event.getAddress());
      ticketPriceField.setText(String.valueOf(event.getTicketPrice()));
      totalTicketsField.setText(String.valueOf(event.getTotalTickets()));

      // Select the right category in the dropdown
      categoryComboBox.setValue(event.getCategoryName());
    }
    catch (Exception e)
    {
      generalMessage.setTextFill(Color.RED);
      generalMessage.setText("Could not load event: " + e.getMessage());
    }
  }

  private void populateCategoryDropdown()
  {
    List<Category> categories = viewModel.getAllCategories();
    List<String> names = categories.stream()
        .map(Category::getName)
        .collect(Collectors.toList());
    categoryComboBox.setItems(FXCollections.observableArrayList(names));
  }

  @FXML
  private void onSave()
  {
    clearErrors();

    // Push current form values to the ViewModel
    viewModel.updateField("name", nameField.getText());
    viewModel.updateField("description", descriptionField.getText());
    viewModel.updateField("dateTime", combineDateTime());
    viewModel.updateField("venue", venueField.getText());
    viewModel.updateField("address", addressField.getText());
    viewModel.updateField("zipCode", zipCodeField.getText());
    viewModel.updateField("category", getSelectedCategory());
    viewModel.updateField("ticketPrice", ticketPriceField.getText());
    viewModel.updateField("totalTickets", totalTicketsField.getText());
    viewModel.updateField("imageURL", imageURLField.getText());

    boolean success = viewModel.onUpdateEvent();

    if (success)
    {
      generalMessage.setTextFill(Color.GREEN);
      generalMessage.setText("Event updated successfully!");
      // Close window after short delay so user sees the success message
      new Thread(() -> {
        try { Thread.sleep(1000); }
        catch (InterruptedException ignored) {}
        javafx.application.Platform.runLater(this::closeWindow);
      }).start();
    }
    else
    {
      showFieldErrors(viewModel.getLastErrors());
    }
  }

  @FXML
  private void onCancel()
  {
    closeWindow();
  }

  private void closeWindow()
  {
    ((Stage) nameField.getScene().getWindow()).close();
  }

  private String getSelectedCategory()
  {
    String selected = categoryComboBox.getValue();
    return selected == null ? "" : selected;
  }

  private String combineDateTime()
  {
    String date = dateField.getText() == null ? "" : dateField.getText().trim();
    String time = timeField.getText() == null ? "" : timeField.getText().trim();

    if (date.isEmpty() && time.isEmpty())
    {
      return "";
    }
    return date + " " + time;
  }

  private void showFieldErrors(List<FieldError> errors)
  {
    for (FieldError error : errors)
    {
      switch (error.getFieldName())
      {
        case "name":         nameError.setText(error.getMessage()); break;
        case "description":  descriptionError.setText(error.getMessage()); break;
        case "dateTime":     dateTimeError.setText(error.getMessage()); break;
        case "venue":        venueError.setText(error.getMessage()); break;
        case "address":      addressError.setText(error.getMessage()); break;
        case "zipCode":      zipCodeError.setText(error.getMessage()); break;
        case "category":     categoryError.setText(error.getMessage()); break;
        case "ticketPrice":  ticketPriceError.setText(error.getMessage()); break;
        case "totalTickets": totalTicketsError.setText(error.getMessage()); break;
        case "imageURL":     imageURLError.setText(error.getMessage()); break;
        case "_general":
          generalMessage.setTextFill(Color.RED);
          generalMessage.setText(error.getMessage());
          break;
      }
    }
  }

  private void clearErrors()
  {
    nameError.setText("");
    descriptionError.setText("");
    dateTimeError.setText("");
    venueError.setText("");
    addressError.setText("");
    zipCodeError.setText("");
    categoryError.setText("");
    ticketPriceError.setText("");
    totalTicketsError.setText("");
    imageURLError.setText("");
    generalMessage.setText("");
  }
}