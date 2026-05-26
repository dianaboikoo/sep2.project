package Main;

import Client.ServerConnection;
import View.LoginView;
import ViewModel.LoginViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Main extends Application
{
  @Override
  public void start(Stage primaryStage) throws Exception
  {
    // Fail fast - connect to the server before showing any UI.
    // If the server is not running the user gets a clear error immediately.
    try
    {
      ServerConnection.getInstance();
    }
    catch (Exception e)
    {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Cannot Connect to Server");
      alert.setHeaderText("Could not connect to the event ticketing server.");
      alert.setContentText(
          "Make sure Server.java is running on port 8080, then try again.\n\n"
          + "Details: " + e.getMessage());
      alert.showAndWait();
      return;
    }

    LoginViewModel loginVM = new LoginViewModel();

    FXMLLoader loader = new FXMLLoader(
        getClass().getResource("/View/LoginView.fxml"));
    Scene scene = new Scene(loader.load());

    LoginView loginView = loader.getController();
    loginView.init(loginVM);

    primaryStage.setTitle("Login");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args)
  {
    launch(args);
  }
}