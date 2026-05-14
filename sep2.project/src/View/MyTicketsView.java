package View;

import Model.EventDetailDto;
import Model.Ticket;
import ViewModel.MyTicketsViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
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

        ticketsTable.setOnMouseClicked(mouseEvent ->
        {
            if (mouseEvent.getButton() == MouseButton.PRIMARY
                    && mouseEvent.getClickCount() == 2)
            {
                Ticket selected = ticketsTable.getSelectionModel().getSelectedItem();
                if (selected != null)
                {
                    openDigitalTicket(selected);
                }
            }
        });
    }

    private void openDigitalTicket(Ticket ticket)
    {
        EventDetailDto event = viewModel.getEventById(ticket.getEventId());
        if (event == null)
        {
            return;
        }
        try
        {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/View/DigitalTicketView.fxml"));
            Scene scene = new Scene(loader.load());

            DigitalTicketView digitalTicketView = loader.getController();
            digitalTicketView.init(ticket, event);

            Stage stage = new Stage();
            stage.setTitle("Your Ticket");
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    private void onClose()
    {
        Stage stage = (Stage) ticketsTable.getScene().getWindow();
        stage.close();
    }
}
