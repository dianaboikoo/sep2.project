package Main;

import Model.CategoryRepositoryImpl;
import Model.CategoryService;
import Model.EventRepositoryImpl;
import Model.EventService;
import View.CreateEventView;
import View.EventsListView;
import ViewModel.CreateEventViewModel;
import ViewModel.EventsListViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
  @Override
  public void start(Stage primaryStage) throws Exception
  {
    EventRepositoryImpl eventRepo = EventRepositoryImpl.getInstance();
    CategoryRepositoryImpl categoryRepo = CategoryRepositoryImpl.getInstance();
    CategoryService categoryService = new CategoryService(categoryRepo);

    // --- User-facing: Events Listing (primary window) ---
    FXMLLoader eventsLoader = new FXMLLoader(
        getClass().getResource("/View/EventsListView.fxml"));
    Scene eventsScene = new Scene(eventsLoader.load());

    EventsListView eventsView = eventsLoader.getController();
    EventService eventService = new EventService(eventRepo);
    EventsListViewModel eventsVM = new EventsListViewModel(eventService);
    eventsView.init(eventsVM);

    primaryStage.setTitle("Events");
    primaryStage.setScene(eventsScene);
    primaryStage.show();

    // --- Admin: Create Event (secondary window) ---
    FXMLLoader createLoader = new FXMLLoader(
        getClass().getResource("/View/CreateEventView.fxml"));
    Scene createScene = new Scene(createLoader.load());

    CreateEventView createView = createLoader.getController();
    CreateEventViewModel createVM = new CreateEventViewModel(eventRepo, categoryService);
    createView.init(createVM, categoryService);

    Stage adminStage = new Stage();
    adminStage.setTitle("Create Event");
    adminStage.setScene(createScene);
    adminStage.show();
  }

  public static void main(String[] args)
  {
    launch(args);
  }
}