package View;

import Model.Category;
import ViewModel.CategoryManagementViewModel;
import ViewModel.FieldError;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.List;
import java.util.Optional;

public class CategoryManagementView
{
  private static final int NAME_MAX = 100;


  @FXML private TextField nameField;
  @FXML private TextArea descriptionField;
  @FXML private Label nameError;
  @FXML private Label descriptionError;
  @FXML private Button addButton;


  @FXML private TableView<Category> categoryTable;
  @FXML private TableColumn<Category, String> nameColumn;
  @FXML private TableColumn<Category, String> descriptionColumn;
  @FXML private TableColumn<Category, Void> actionsColumn;


  @FXML private Label statusMessage;

  private CategoryManagementViewModel viewModel;


  public void init(CategoryManagementViewModel viewModel)
  {
    this.viewModel = viewModel;
    setupTableColumns();
    setupAddFormValidation();
    refreshTable();
  }


  private void setupAddFormValidation()
  {
    // Disable Add button when name is empty (or only whitespace).
    // Listener fires every keystroke.
    nameField.textProperty().addListener((obs, oldVal, newVal) ->
    {
      updateAddButtonState();
      // also clear stale error as soon as user starts typing
      if (newVal != null && !newVal.trim().isEmpty())
      {
        nameError.setText("");
      }
    });
    // Initial state: empty field → disabled button
    updateAddButtonState();
  }

  private void updateAddButtonState()
  {
    String text = nameField.getText();
    addButton.setDisable(text == null || text.trim().isEmpty());
  }


  private void setupTableColumns()
  {
    nameColumn.setCellValueFactory(cellData ->
        new SimpleStringProperty(cellData.getValue().getName()));

    descriptionColumn.setCellValueFactory(cellData ->
        new SimpleStringProperty(cellData.getValue().getDescription()));


    actionsColumn.setCellFactory(col -> new TableCell<Category, Void>()
    {
      private final Button editButton = new Button("Edit");
      private final Button deleteButton = new Button("Delete");
      private final HBox box = new HBox(8, editButton, deleteButton);

      {
        box.setPadding(new Insets(2));
        editButton.setOnAction(e ->
        {
          Category cat = getTableView().getItems().get(getIndex());
          onEditCategory(cat);
        });
        deleteButton.setOnAction(e ->
        {
          Category cat = getTableView().getItems().get(getIndex());
          onDeleteCategory(cat);
        });
      }

      @Override
      protected void updateItem(Void item, boolean empty)
      {
        super.updateItem(item, empty);
        if (empty || getIndex() < 0
            || getIndex() >= getTableView().getItems().size())
        {
          setGraphic(null);
          return;
        }
        Category cat = getTableView().getItems().get(getIndex());
        // hide / disable Delete for "Uncategorized"
        deleteButton.setDisable(viewModel.isProtected(cat.getName()));
        setGraphic(box);
      }
    });
  }

  private void refreshTable()
  {
    viewModel.loadCategories();
    List<Category> list = viewModel.getCategories();
    categoryTable.setItems(FXCollections.observableArrayList(list));
  }


  @FXML
  private void onAddCategory()
  {
    clearErrors();

    String name = nameField.getText() == null ? "" : nameField.getText();
    String desc = descriptionField.getText();

    // ---- Frontend (client-side) validation ----
    if (name.trim().isEmpty())
    {
      nameError.setText("Name is required");
      return;
    }
    if (name.length() > NAME_MAX)
    {
      nameError.setText("Name must be under " + NAME_MAX + " characters");
      return;
    }


    setBusy(true);
    boolean ok = viewModel.addCategory(name, desc);
    setBusy(false);

    if (ok)
    {
      clearForm();
      refreshTable();
      showToast("Category added", Color.GREEN);
    }
    else
    {
      showFieldErrors(viewModel.getLastErrors());
    }
  }


  private void onEditCategory(Category category)
  {
    Dialog<Category> dialog = new Dialog<>();
    dialog.setTitle("Edit Category");
    dialog.setHeaderText("Edit \"" + category.getName() + "\"");

    ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

    TextField nameInput = new TextField(category.getName());
    TextArea descInput  = new TextArea(category.getDescription());
    descInput.setPrefRowCount(3);
    Label errorLabel = new Label();
    errorLabel.setTextFill(Color.RED);

    VBox content = new VBox(8,
        new Label("Name *"), nameInput,
        new Label("Description"), descInput,
        errorLabel);
    content.setPadding(new Insets(10));
    dialog.getDialogPane().setContent(content);

    // Get the Save button so we can control its enabled state
    final Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);


    nameInput.textProperty().addListener((obs, oldVal, newVal) ->
    {
      saveButton.setDisable(newVal == null || newVal.trim().isEmpty());
      errorLabel.setText("");
    });


    saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, ev ->
    {
      errorLabel.setText("");
      String newName = nameInput.getText() == null ? "" : nameInput.getText();
      String newDesc = descInput.getText();

      // Frontend validation
      if (newName.trim().isEmpty())
      {
        errorLabel.setText("Name is required");
        ev.consume();
        return;
      }
      if (newName.length() > NAME_MAX)
      {
        errorLabel.setText("Name must be under " + NAME_MAX + " characters");
        ev.consume();
        return;
      }

      // Send to backend
      setBusy(true);
      boolean ok = viewModel.editCategory(category.getName(), newName, newDesc);
      setBusy(false);

      if (!ok)
      {
        List<FieldError> errors = viewModel.getLastErrors();
        if (!errors.isEmpty())
        {
          errorLabel.setText(errors.get(0).getMessage());
        }
        ev.consume();   // keep dialog open so user can fix
      }
      else
      {
        refreshTable();
        showToast("Category updated", Color.GREEN);
      }
    });

    dialog.showAndWait();
  }


  // Delete

  private void onDeleteCategory(Category category)
  {
    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
    confirm.setTitle("Delete category");
    confirm.setHeaderText("Delete \"" + category.getName() + "\"?");
    confirm.setContentText(
        "Events in this category will be moved to Uncategorized. Are you sure?");

    Optional<ButtonType> answer = confirm.showAndWait();
    if (answer.isEmpty() || answer.get() != ButtonType.OK)
    {
      return;
    }

    setBusy(true);
    boolean ok = viewModel.deleteCategory(category.getName());
    setBusy(false);

    if (ok)
    {
      refreshTable();
      showToast("Category deleted", Color.GREEN);
    }
    else
    {
      List<FieldError> errors = viewModel.getLastErrors();
      String msg = errors.isEmpty() ? "Could not delete category"
          : errors.get(0).getMessage();
      showToast(msg, Color.RED);
    }
  }


  // Helpers

  private void showFieldErrors(List<FieldError> errors)
  {
    for (FieldError error : errors)
    {
      switch (error.getFieldName())
      {
        case "name":        nameError.setText(error.getMessage()); break;
        case "description": descriptionError.setText(error.getMessage()); break;
        case "_general":    showToast(error.getMessage(), Color.RED); break;
      }
    }
  }

  private void clearErrors()
  {
    nameError.setText("");
    descriptionError.setText("");
    statusMessage.setText("");
  }

  private void clearForm()
  {
    nameField.clear();
    descriptionField.clear();
    // disable button again since field is now empty
    updateAddButtonState();
  }


  private void showToast(String text, Color color)
  {
    statusMessage.setTextFill(color);
    statusMessage.setText(text);
    PauseTransition fade = new PauseTransition(Duration.seconds(3));
    fade.setOnFinished(e ->
    {
      // only clear if the same message is still showing
      if (text.equals(statusMessage.getText()))
      {
        statusMessage.setText("");
      }
    });
    fade.play();
  }


  private void setBusy(boolean busy)
  {
    // when not busy, restore based on whether the field has text
    if (busy)
    {
      addButton.setDisable(true);
    }
    else
    {
      updateAddButtonState();
    }
    categoryTable.setDisable(busy);
  }
}