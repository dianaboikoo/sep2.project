package View;

import Model.Category;
import ViewModel.CreateEventViewModel;
import ViewModel.FieldError;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

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
        categoryComboBox.setItems(FXCollections.observableArrayList(names));
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
        categoryComboBox.getSelectionModel().clearSelection();
        ticketPriceField.clear();
        totalTicketsField.clear();
        imageURLField.clear();
    }
}