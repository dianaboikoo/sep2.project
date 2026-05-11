package View;

import Model.CategoryRepositoryImpl;
import Model.CategoryService;
import Model.EventRepositoryImpl;
import Model.EventService;
import Model.UserRole;
import ViewModel.CreateEventViewModel;
import ViewModel.EventsListViewModel;
import ViewModel.LoginViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginView
{
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private LoginViewModel viewModel;

    public void init(LoginViewModel viewModel)
    {
        this.viewModel = viewModel;
    }

    @FXML
    private void onLogin()
    {
        errorLabel.setText("");
        String email    = emailField.getText();
        String password = passwordField.getText();

        UserRole role = viewModel.login(email, password);

        if (role == null)
        {
            errorLabel.setText("Invalid email or password.");
            return;
        }

        try
        {
            openMainWindow(role);
            // Close login window
            Stage loginStage = (Stage) emailField.getScene().getWindow();
            loginStage.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            errorLabel.setText("Could not open application: " + e.getMessage());
        }
    }

    private void openMainWindow(UserRole role) throws Exception
    {
        EventRepositoryImpl eventRepo   = EventRepositoryImpl.getInstance();
        CategoryRepositoryImpl catRepo  = CategoryRepositoryImpl.getInstance();
        CategoryService categoryService = new CategoryService(catRepo);
        EventService eventService       = new EventService(eventRepo);
        EventsListViewModel eventsVM = new EventsListViewModel(eventService, categoryService);

        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/View/EventsListView.fxml"));
        Scene scene = new Scene(loader.load());

        EventsListView eventsView = loader.getController();
        eventsView.init(eventsVM, role);

        Stage stage = new Stage();
        stage.setTitle(role == UserRole.ADMIN ? "Events — Admin" : "Events");
        stage.setScene(scene);
        stage.show();

        // Admin also gets Create Event and Category Management via buttons in the view
    }
}
