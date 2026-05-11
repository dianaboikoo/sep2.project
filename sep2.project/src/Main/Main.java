package Main;

import Model.UserRepositoryImpl;
import View.LoginView;
import ViewModel.LoginViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
  @Override
  public void start(Stage primaryStage) throws Exception
  {
    UserRepositoryImpl userRepo = UserRepositoryImpl.getInstance();
    LoginViewModel loginVM = new LoginViewModel(userRepo);

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