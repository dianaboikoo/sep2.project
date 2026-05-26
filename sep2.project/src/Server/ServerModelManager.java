package Server;

import Model.CategoryRepositoryImpl;
import Model.CategoryService;
import Model.EventRepositoryImpl;
import Model.EventService;
import Model.TicketRepositoryImpl;
import Model.TicketService;
import Model.UserRepositoryImpl;

public class ServerModelManager
{
    private static ServerModelManager instance;

    private final EventRepositoryImpl    eventRepo;
    private final CategoryRepositoryImpl categoryRepo;
    private final TicketRepositoryImpl   ticketRepo;
    private final UserRepositoryImpl     userRepo;

    private final EventService    eventService;
    private final CategoryService categoryService;
    private final TicketService   ticketService;

    private ServerModelManager()
    {
        try
        {
            eventRepo    = EventRepositoryImpl.getInstance();
            categoryRepo = CategoryRepositoryImpl.getInstance();
            ticketRepo   = TicketRepositoryImpl.getInstance();
            userRepo     = UserRepositoryImpl.getInstance();

            eventService    = new EventService(eventRepo);
            categoryService = new CategoryService(categoryRepo);
            ticketService   = new TicketService(ticketRepo, eventRepo);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Failed to initialize ServerModelManager: " + e.getMessage(), e);
        }
    }

    public static synchronized ServerModelManager getInstance()
    {
        if (instance == null)
        {
            instance = new ServerModelManager();
        }
        return instance;
    }

    public EventRepositoryImpl getEventRepo()       { return eventRepo; }
    public UserRepositoryImpl  getUserRepo()         { return userRepo; }

    public EventService    getEventService()    { return eventService; }
    public CategoryService getCategoryService() { return categoryService; }
    public TicketService   getTicketService()   { return ticketService; }
}
