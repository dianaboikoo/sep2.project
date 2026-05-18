package View;

import Model.Category;
import ViewModel.CategoryManagementViewModel;
import ViewModel.CreateEventViewModel;
import ViewModel.FieldError;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class CreateEventView
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

  private CreateEventViewModel viewModel;

  public void init(CreateEventViewModel viewModel)
  {
    this.viewModel = viewModel;
    populateCategoryDropdown();
  }

  private void populateCategoryDropdown()
  {
    List<Category> categories = viewModel.getAllCategories();
    List<String> names = categories.stream()
        .map(Category::getName)
        .collect(Collectors.toList());

    // remember current selection so it stays selected after refresh
    String currentlySelected = categoryComboBox.getValue();
    categoryComboBox.setItems(FXCollections.observableArrayList(names));
    if (currentlySelected != null && names.contains(currentlySelected))
    {
      categoryComboBox.setValue(currentlySelected);
    }
  }

  @FXML
  private void onCreateEvent()
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

    boolean success = viewModel.onCreateEvent();

    if (success)
    {
      clearForm();
      generalMessage.setTextFill(Color.GREEN);
      generalMessage.setText("Event created successfully");
    }
    else
    {
      showFieldErrors(viewModel.getLastErrors());
    }
  }

  /**
   * Open the Manage Categories window as a modal dialog.
   * When it closes, refresh the dropdown so any new categories appear.
   */
  @FXML
  private void onManageCategories()
  {
    try
    {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/View/CategoryManagementView.fxml"));
      Scene scene = new Scene(loader.load());

      CategoryManagementView mgmtView = loader.getController();
      CategoryManagementViewModel mgmtVM = new CategoryManagementViewModel();
      mgmtView.init(mgmtVM);

      Stage stage = new Stage();
      stage.setTitle("Manage Categories");
      stage.setScene(scene);
      // make it modal so user must close it before going back to Create Event
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.initOwner(categoryComboBox.getScene().getWindow());
      stage.showAndWait();

      // refresh dropdown — any new/edited/deleted categories now reflected
      populateCategoryDropdown();
    }
    catch (Exception e)
    {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText("Could not open Manage Categories");
      alert.setContentText(e.getMessage());
      alert.showAndWait();
    }
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

  private void clearForm()
  {
    nameField.clear();
    descriptionField.clear();
    dateField.clear();
    timeField.clear();
    venueField.clear();
    addressField.clear();
    zipCodeField.clear();
    categoryComboBox.getSelectionModel().clearSelection();
    ticketPriceField.clear();
    totalTicketsField.clear();
    imageURLField.clear();
  }
}