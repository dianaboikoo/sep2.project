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
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.List;
import java.util.Optional;

public class CategoryManagementView
{
  // ---- Add New Category form fields ----
  @FXML private TextField nameField;
  @FXML private TextArea descriptionField;
  @FXML private Label nameError;
  @FXML private Label descriptionError;
  @FXML private Button addButton;

  // ---- Existing Categories table ----
  @FXML private TableView<Category> categoryTable;
  @FXML private TableColumn<Category, String> nameColumn;
  @FXML private TableColumn<Category, String> descriptionColumn;
  @FXML private TableColumn<Category, Void> actionsColumn;

  // ---- Status / toast area ----
  @FXML private Label statusMessage;

  private CategoryManagementViewModel viewModel;

  /** Wired from Main after FXML is loaded. */
  public void init(CategoryManagementViewModel viewModel)
  {
    this.viewModel = viewModel;
    setupTableColumns();
    refreshTable();
  }

  // =====================================================
  // Table setup
  // =====================================================
  private void setupTableColumns()
  {
    nameColumn.setCellValueFactory(cellData ->
        new SimpleStringProperty(cellData.getValue().getName()));

    descriptionColumn.setCellValueFactory(cellData ->
        new SimpleStringProperty(cellData.getValue().getDescription()));

    // "Actions" column with Edit + Delete buttons per row
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

  // =====================================================
  // Add
  // =====================================================
  @FXML
  private void onAddCategory()
  {
    clearErrors();
    setBusy(true);

    String name = nameField.getText();
    String desc = descriptionField.getText();

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

  // =====================================================
  // Edit (modal dialog with pre-filled fields)
  // =====================================================
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

    javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(8,
        new Label("Name *"), nameInput,
        new Label("Description"), descInput,
        errorLabel);
    content.setPadding(new Insets(10));
    dialog.getDialogPane().setContent(content);

    // Intercept Save so we can validate without closing on error
    final Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
    saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, ev ->
    {
      errorLabel.setText("");
      setBusy(true);
      boolean ok = viewModel.editCategory(
          category.getName(),
          nameInput.getText(),
          descInput.getText());
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

  // =====================================================
  // Delete (confirmation dialog)
  // =====================================================
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

  // =====================================================
  // Helpers
  // =====================================================
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
  }

  /** Shows a transient toast-style message (auto-clears after 3s). */
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

  /** Disable all action buttons while a request is "in-flight". */
  private void setBusy(boolean busy)
  {
    addButton.setDisable(busy);
    categoryTable.setDisable(busy);
  }
}