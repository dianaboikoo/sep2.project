package View;

import Model.EventDetailDto;
import Model.EventListDto;
import Model.EventRepositoryImpl;
import Model.EventService;
import ViewModel.EventDetailViewModel;
import ViewModel.EventsListViewModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventsListView
{
    @FXML private TableView<EventListDto> eventsTable;
    @FXML private TableColumn<EventListDto, String>  nameColumn;
    @FXML private TableColumn<EventListDto, String>  dateTimeColumn;
    @FXML private TableColumn<EventListDto, String>  venueColumn;
    @FXML private TableColumn<EventListDto, String>  categoryColumn;
    @FXML private TableColumn<EventListDto, String>  cityColumn;
    @FXML private TableColumn<EventListDto, Integer> availableTicketsColumn;
    @FXML private Label noEventsLabel;

    private EventsListViewModel viewModel;

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm");

    public void init(EventsListViewModel viewModel)
    {
        this.viewModel = viewModel;
        setupColumns();
        loadEvents();
    }

    private void setupColumns()
    {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        venueColumn.setCellValueFactory(new PropertyValueFactory<>("venue"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("cityName"));
        availableTicketsColumn.setCellValueFactory(new PropertyValueFactory<>("availableTickets"));

        // dateTime needs formatting — use a custom cell factory
        dateTimeColumn.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getDateTime() == null ? ""
                    : data.getValue().getDateTime().format(FORMATTER)));
    }

    private void loadEvents()
    {
        List<EventListDto> events = viewModel.getPublishedEvents();

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
            detailView.init(detailVM, selected.getEventId());

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
}
