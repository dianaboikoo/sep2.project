package View;

import ViewModel.*;
import javafx.scene.control.Alert;
import Model.Category;
import Model.CategoryRepositoryImpl;
import Model.CategoryService;
import Model.City;
import Model.EventListDto;
import Model.EventRepositoryImpl;
import Model.EventService;
import Model.TicketService;
import Model.UserRole;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventsListView
{
  @FXML private Button viewSalesButton;
  @FXML private TableView<EventListDto> eventsTable;
  @FXML private TableColumn<EventListDto, String>  nameColumn;
  @FXML private TableColumn<EventListDto, String>  dateTimeColumn;
  @FXML private TableColumn<EventListDto, String>  venueColumn;
  @FXML private TableColumn<EventListDto, String>  categoryColumn;
  @FXML private TableColumn<EventListDto, String>  cityColumn;
  @FXML private TableColumn<EventListDto, Integer> availableTicketsColumn;
  @FXML private Label  noEventsLabel;
  @FXML private Button createEventButton;
  @FXML private Button manageCategoriesButton;
  @FXML private Button editEventButton;

  // ---- Filter UI controls ----
  @FXML private ComboBox<Category> categoryFilter;
  @FXML private ComboBox<City>     cityFilter;
  @FXML private DatePicker         fromDatePicker;
  @FXML private DatePicker         toDatePicker;
  @FXML private Button             applyFiltersButton;
  @FXML private Button             clearFiltersButton;

  // ---- Single shared filter error label (per spec) ----
  @FXML private Label filterError;

  private EventsListViewModel viewModel;
  private String userEmail;
  private TicketService ticketService;

  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm");

  public void init(EventsListViewModel viewModel, UserRole role,
                   String userEmail, TicketService ticketService)
  {
    this.viewModel = viewModel;
    this.userEmail = userEmail;
    this.ticketService = ticketService;
    setupColumns();
    setupFilterDropdowns();
    loadEvents();   // initial load (no filter)

    boolean isAdmin = role == UserRole.ADMIN;
    createEventButton.setVisible(isAdmin);
    createEventButton.setManaged(isAdmin);
    manageCategoriesButton.setVisible(isAdmin);
    manageCategoriesButton.setManaged(isAdmin);
    viewSalesButton.setVisible(isAdmin);
    viewSalesButton.setManaged(isAdmin);
    editEventButton.setVisible(isAdmin);
    editEventButton.setManaged(isAdmin);
  }

  private void setupColumns()
  {
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    venueColumn.setCellValueFactory(new PropertyValueFactory<>("venue"));
    categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
    cityColumn.setCellValueFactory(new PropertyValueFactory<>("cityName"));
    availableTicketsColumn.setCellValueFactory(new PropertyValueFactory<>("availableTickets"));

    dateTimeColumn.setCellValueFactory(data ->
        new javafx.beans.property.SimpleStringProperty(
            data.getValue().getDateTime() == null ? ""
                : data.getValue().getDateTime().format(FORMATTER)));
  }

  private void setupFilterDropdowns()
  {
    // ---- Category dropdown ----
    List<Category> categories = viewModel.getCategoryOptions();
    categoryFilter.setItems(FXCollections.observableArrayList(categories));
    categoryFilter.setConverter(new StringConverter<>()
    {
      @Override public String toString(Category c) { return c == null ? "" : c.getName(); }
      @Override public Category fromString(String s) { return null; }
    });

    // ---- City dropdown ----
    List<City> cities = viewModel.getCityOptions();
    cityFilter.setItems(FXCollections.observableArrayList(cities));
    cityFilter.setConverter(new StringConverter<>()
    {
      @Override public String toString(City c) { return c == null ? "" : c.getCityName(); }
      @Override public City fromString(String s) { return null; }
    });
  }

  // =====================================================
  // Loading events
  // =====================================================
  private void loadEvents()
  {
    List<EventListDto> events = viewModel.getPublishedEvents();
    displayEvents(events);
  }

  private void displayEvents(List<EventListDto> events)
  {
    if (events.isEmpty())
    {
      eventsTable.setVisible(false);
      noEventsLabel.setVisible(true);
    }
    else
    {
      noEventsLabel.setVisible(false);
      eventsTable.setVisible(true);
      eventsTable.setItems(FXCollections.observableArrayList(events));
    }
  }

  // =====================================================
  // Filter actions
  // =====================================================
  @FXML
  private void onApplyFilters()
  {
    hideFilterError();

    // Collect input values
    Category selectedCategory = categoryFilter.getValue();
    City     selectedCity     = cityFilter.getValue();
    LocalDate fromDate        = fromDatePicker.getValue();
    LocalDate toDate          = toDatePicker.getValue();

    // ---- Frontend validation per spec ----
    // If fromDate AND toDate are both set, fromDate must be <= toDate.
    // If only one is set, no validation needed (open-ended range is allowed).
    if (fromDate != null && toDate != null && fromDate.isAfter(toDate))
    {
      showFilterError("From date must be before To date");
      return;   // do not run query
    }

    String categoryName = selectedCategory == null ? null : selectedCategory.getName();
    Integer zipCode     = selectedCity == null ? null : selectedCity.getCityId();

    // fromDate set, toDate null  → filter from that date onwards (no upper bound)
    // fromDate null, toDate set  → filter up to that date (no lower bound)
    // both null                  → no date filter
    List<EventListDto> filtered =
        viewModel.getFilteredEvents(categoryName, zipCode, fromDate, toDate);
    displayEvents(filtered);
  }

  @FXML
  private void onClearFilters()
  {
    hideFilterError();
    categoryFilter.getSelectionModel().clearSelection();
    cityFilter.getSelectionModel().clearSelection();
    fromDatePicker.setValue(null);
    toDatePicker.setValue(null);
    loadEvents();   // reload unfiltered
  }

  private void showFilterError(String message)
  {
    filterError.setText(message);
    filterError.setVisible(true);
    filterError.setManaged(true);
  }

  private void hideFilterError()
  {
    filterError.setText("");
    filterError.setVisible(false);
    filterError.setManaged(false);
  }

  // =====================================================
  // Navigation (unchanged from existing code)
  // =====================================================
  @FXML
  private void onViewDetails()
  {
    EventListDto selected = eventsTable.getSelectionModel().getSelectedItem();
    if (selected == null) return;

    try
    {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/View/EventDetailView.fxml"));
      Scene scene = new Scene(loader.load());

      EventDetailView detailView = loader.getController();
      EventService eventService = new EventService(EventRepositoryImpl.getInstance());
      EventDetailViewModel detailVM = new EventDetailViewModel(eventService);
      detailView.init(detailVM, selected.getEventId(), userEmail, ticketService);

      Stage stage = new Stage();
      stage.setTitle("Event Details");
      stage.setScene(scene);
      stage.show();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  @FXML
  private void onCreateEvent()
  {
    try
    {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/View/CreateEventView.fxml"));
      Scene scene = new Scene(loader.load());

      CategoryRepositoryImpl catRepo = CategoryRepositoryImpl.getInstance();
      CategoryService categoryService = new CategoryService(catRepo);
      CreateEventViewModel createVM = new CreateEventViewModel(
          EventRepositoryImpl.getInstance(), categoryService);

      CreateEventView createView = loader.getController();
      createView.init(createVM, categoryService);

      Stage stage = new Stage();
      stage.setTitle("Create Event");
      stage.setScene(scene);
      stage.show();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  @FXML
  private void onMyTickets()
  {
    try
    {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/View/MyTicketsView.fxml"));
      Scene scene = new Scene(loader.load());

      EventService eventService = new EventService(EventRepositoryImpl.getInstance());
      MyTicketsViewModel ticketsVM = new MyTicketsViewModel(ticketService, eventService);

      MyTicketsView ticketsView = loader.getController();
      ticketsView.init(ticketsVM, userEmail);

      Stage stage = new Stage();
      stage.setTitle("My Tickets");
      stage.setScene(scene);
      stage.show();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  @FXML
  private void onManageCategories()
  {
    try
    {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/View/CategoryManagementView.fxml"));
      Scene scene = new Scene(loader.load());

      CategoryRepositoryImpl catRepo = CategoryRepositoryImpl.getInstance();
      CategoryService categoryService = new CategoryService(catRepo);
      CategoryManagementViewModel catVM = new CategoryManagementViewModel(categoryService);

      CategoryManagementView catView = loader.getController();
      catView.init(catVM);

      Stage stage = new Stage();
      stage.setTitle("Manage Categories");
      stage.setScene(scene);
      stage.show();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  @FXML
  private void onViewSales()
  {
    EventListDto selected = eventsTable.getSelectionModel().getSelectedItem();

    if (selected == null)
    {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("No event selected");
      alert.setHeaderText(null);
      alert.setContentText("Please select an event first");
      alert.showAndWait();
      return;
    }

    try
    {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/View/TicketSalesView.fxml"));
      Scene scene = new Scene(loader.load());

      TicketSalesView salesView = loader.getController();
      TicketSalesViewModel salesVM = new TicketSalesViewModel(ticketService);
      salesView.init(salesVM, selected.getEventId());

      Stage stage = new Stage();
      stage.setTitle("Ticket Sales Report");
      stage.setScene(scene);
      stage.show();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  @FXML
  private void onEditEvent()
  {
    EventListDto selected = eventsTable.getSelectionModel().getSelectedItem();

    if (selected == null)
    {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("No event selected");
      alert.setHeaderText(null);
      alert.setContentText("Please select an event first");
      alert.showAndWait();
      return;
    }

    try
    {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/View/EditEventView.fxml"));
      Scene scene = new Scene(loader.load());

      CategoryRepositoryImpl catRepo = CategoryRepositoryImpl.getInstance();
      CategoryService categoryService = new CategoryService(catRepo);
      EditEventViewModel editVM = new EditEventViewModel(
          EventRepositoryImpl.getInstance(), categoryService);

      EditEventView editView = loader.getController();
      editView.init(editVM, selected.getEventId());

      Stage stage = new Stage();
      stage.setTitle("Edit Event");
      stage.setScene(scene);
      stage.showAndWait(); // wait so table refreshes after closing

      loadEvents(); // refresh table with updated data
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}