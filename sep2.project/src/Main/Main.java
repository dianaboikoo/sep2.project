package Main;

import Model.CategoryRepositoryImpl;
import Model.CategoryService;
import Model.EventRepositoryImpl;
import java.sql.DriverManager;
import View.CreateEventView;
import ViewModel.CreateEventViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
  @Override
  public void start(Stage primaryStage) throws Exception
  {
    FXMLLoader loader = new FXMLLoader(
        getClass().getResource("/View/CreateEventView.fxml"));

    Scene scene = new Scene(loader.load());

    // Get view (controller)
    CreateEventView view = loader.getController();

    // Create Model + ViewModel
    EventRepositoryImpl repository =  EventRepositoryImpl.getInstance();
    CategoryRepositoryImpl categoryRepo = CategoryRepositoryImpl.getInstance();

    CategoryService categoryService = new CategoryService(categoryRepo);

    CreateEventViewModel viewModel = new CreateEventViewModel(repository, categoryService);

    // Connect View ↔ ViewModel
    view.init(viewModel);
    primaryStage.setTitle("Create Event");
    primaryStage.setScene(scene);
    primaryStage.show();

    //test
      // Quick verification — remove after testing
      System.out.println("All categories: " + categoryService.findAll());

      categoryService.add("Concert", "Live music events");
      categoryService.add("Conference", "Professional gatherings");
      System.out.println("After add: " + categoryService.findAll());

      categoryService.edit("Conference", "Workshop", "Hands-on learning");
      System.out.println("After edit: " + categoryService.findAll());

      categoryService.delete("Workshop");
      System.out.println("After delete: " + categoryService.findAll());
  }

  public static void main(String[] args)
  {
    launch(args);
  }
}