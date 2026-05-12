package View;

import Model.Ticket;
import ViewModel.MyTicketsViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTicketsView
{
    @FXML private TableView<Ticket> ticketsTable;
    @FXML private TableColumn<Ticket, String>  ticketIdColumn;
    @FXML private TableColumn<Ticket, String>  eventNameColumn;
    @FXML private TableColumn<Ticket, String>  purchaseDateColumn;
    @FXML private TableColumn<Ticket, Integer> quantityColumn;
    @FXML private TableColumn<Ticket, String>  statusColumn;
    @FXML private Label noTicketsLabel;

    private MyTicketsViewModel viewModel;

    // Cache event-name lookups so each visible row triggers at most one DB hit.
    private final Map<Integer, String> eventNameCache = new HashMap<>();

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm");

    public void init(MyTicketsViewModel vm, String userEmail)
    {
        this.viewModel = vm;
        setupColumns();
        loadTickets(userEmail);
    }

    private void setupColumns()
    {
        ticketIdColumn.setCellValueFactory(new PropertyValueFactory<>("ticketId"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Resolve event name through the cache (calls viewModel.getEventName(id) on miss).
        eventNameColumn.setCellValueFactory(data ->
            new SimpleStringProperty(
                eventNameCache.computeIfAbsent(
                    data.getValue().getEventId(),
                    viewModel::getEventName)));

        purchaseDateColumn.setCellValueFactory(data ->
            new SimpleStringProperty(
                data.getValue().getPurchaseDate() == null ? ""
                    : data.getValue().getPurchaseDate().format(FORMATTER)));

        statusColumn.setCellValueFactory(data ->
            new SimpleStringProperty(
                data.getValue().getStatus() == null ? ""
                    : data.getValue().getStatus().toString()));
    }

    private void loadTickets(String userEmail)
    {
        List<Ticket> tickets = viewModel.getMyTickets(userEmail);
        if (tickets.isEmpty())
        {
            ticketsTable.setVisible(false);
            noTicketsLabel.setVisible(true);
        }
        else
        {
            noTicketsLabel.setVisible(false);
            ticketsTable.setVisible(true);
            ticketsTable.setItems(FXCollections.observableArrayList(tickets));
        }
    }

    @FXML
    private void onClose()
    {
        Stage stage = (Stage) ticketsTable.getScene().getWindow();
        stage.close();
    }
}
