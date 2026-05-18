package Server;

import Model.Category;
import Model.Event;
import Model.EventDetailDto;
import Model.EventListDto;
import Model.EventStatus;
import Model.City;
import Model.Ticket;
import Model.TicketSalesDto;
import Model.UserRole;
import Shared.GsonFactory;
import Shared.Request;
import Shared.Response;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Handles all communication with a single connected client.
 * Runs on its own thread (spawned by Server.java).
 *
 * Protocol: one JSON object per line (request in, response out).
 * Each request is routed to a dedicated private handler method.
 * Any exception is caught and returned as Response.error(...).
 */
public class ClientHandler implements Runnable
{
    private static final DateTimeFormatter DATE_TIME_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Socket             socket;
    private final ServerModelManager manager;
    private final Gson               gson;

    public ClientHandler(Socket socket, ServerModelManager manager)
    {
        this.socket  = socket;
        this.manager = manager;
        this.gson    = GsonFactory.get();
    }

    // =========================================================================
    // Main loop
    // =========================================================================

    @Override
    public void run()
    {
        try (
            BufferedReader in  = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter    out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String line;
            while ((line = in.readLine()) != null)
            {
                Response response;
                try
                {
                    Request request = gson.fromJson(line, Request.class);
                    System.out.println("[Server] Request: " + request.getType());
                    response = route(request);
                }
                catch (Exception e)
                {
                    System.err.println("[Server] Routing error: " + e.getMessage());
                    response = Response.error("Internal server error: " + e.getMessage());
                }
                out.println(gson.toJson(response));
            }
        }
        catch (Exception e)
        {
            System.err.println("[Server] Client handler error: " + e.getMessage());
        }
        finally
        {
            try { socket.close(); } catch (Exception ignored) {}
            System.out.println("[Server] Client disconnected.");
        }
    }

    // =========================================================================
    // Router
    // =========================================================================

    private Response route(Request request)
    {
        Map<String, Object> p = request.getPayload();
        switch (request.getType())
        {
            case "LOGIN":               return handleLogin(p);
            case "GET_ALL_EVENTS":      return handleGetAllEvents();
            case "GET_FILTERED_EVENTS": return handleGetFilteredEvents(p);
            case "GET_EVENT_BY_ID":     return handleGetEventById(p);
            case "CREATE_EVENT":        return handleCreateEvent(p);
            case "UPDATE_EVENT":        return handleUpdateEvent(p);
            case "DELETE_EVENT":        return handleDeleteEvent(p);
            case "GET_CATEGORIES":      return handleGetCategories();
            case "ADD_CATEGORY":        return handleAddCategory(p);
            case "EDIT_CATEGORY":       return handleEditCategory(p);
            case "DELETE_CATEGORY":     return handleDeleteCategory(p);
            case "GET_CITIES":          return handleGetCities();
            case "PURCHASE_TICKET":     return handlePurchaseTicket(p);
            case "GET_MY_TICKETS":      return handleGetMyTickets(p);
            case "GET_SALES_REPORT":    return handleGetSalesReport(p);
            default:
                return Response.error("Unknown request type: " + request.getType());
        }
    }

    // =========================================================================
    // Handlers — one per request type
    // =========================================================================

    /** LOGIN: { email, password } → { role: "ADMIN" | "USER" } */
    private Response handleLogin(Map<String, Object> p)
    {
        try
        {
            String email    = str(p, "email");
            String password = str(p, "password");
            UserRole role = manager.getUserRepo().findByCredentials(email, password);
            if (role == null)
            {
                return Response.error("Invalid credentials");
            }
            return Response.ok(Map.of("role", role.name()));
        }
        catch (Exception e)
        {
            return Response.error("Login failed: " + e.getMessage());
        }
    }

    /** GET_ALL_EVENTS: {} → List<EventListDto> */
    private Response handleGetAllEvents()
    {
        try
        {
            List<EventListDto> events = manager.getEventService().getAllEvents();
            return Response.ok(events);
        }
        catch (Exception e)
        {
            return Response.error("Failed to load events: " + e.getMessage());
        }
    }

    /** GET_FILTERED_EVENTS: { category?, zipCode?, fromDate?, toDate? } → List<EventListDto> */
    private Response handleGetFilteredEvents(Map<String, Object> p)
    {
        try
        {
            String   category = strOrNull(p, "category");
            Integer  zipCode  = intOrNull(p, "zipCode");
            LocalDate fromDate = localDateOrNull(p, "fromDate");
            LocalDate toDate   = localDateOrNull(p, "toDate");

            List<EventListDto> events = manager.getEventService()
                    .getFilteredEvents(category, zipCode, fromDate, toDate);
            return Response.ok(events);
        }
        catch (Exception e)
        {
            return Response.error("Failed to filter events: " + e.getMessage());
        }
    }

    /** GET_EVENT_BY_ID: { eventId } → EventDetailDto */
    private Response handleGetEventById(Map<String, Object> p)
    {
        try
        {
            int id = intVal(p, "eventId");
            EventDetailDto event = manager.getEventService().getEventById(id);
            if (event == null)
            {
                return Response.error("Event not found: " + id);
            }
            return Response.ok(event);
        }
        catch (Exception e)
        {
            return Response.error("Failed to load event: " + e.getMessage());
        }
    }

    /**
     * CREATE_EVENT: { name, description, dateTime, venue, address, categoryName,
     *                 ticketPrice, totalTickets, imageURL, zipCode?, status } → Event
     */
    private Response handleCreateEvent(Map<String, Object> p)
    {
        try
        {
            Event event = buildEventFromPayload(0, p);
            Event saved = manager.getEventRepo().save(event);
            return Response.ok(saved);
        }
        catch (Exception e)
        {
            return Response.error("Failed to create event: " + e.getMessage());
        }
    }

    /**
     * UPDATE_EVENT: { eventId, name, description, dateTime, venue, address, categoryName,
     *                 ticketPrice, totalTickets, imageURL, zipCode? } → Event
     */
    private Response handleUpdateEvent(Map<String, Object> p)
    {
        try
        {
            int id = intVal(p, "eventId");
            Event event = buildEventFromPayload(id, p);
            Event updated = manager.getEventRepo().update(event);
            return Response.ok(updated);
        }
        catch (Exception e)
        {
            return Response.error("Failed to update event: " + e.getMessage());
        }
    }

    /** DELETE_EVENT: { eventId } → null */
    private Response handleDeleteEvent(Map<String, Object> p)
    {
        try
        {
            int id = intVal(p, "eventId");
            manager.getEventRepo().delete(id);
            return Response.ok(null);
        }
        catch (Exception e)
        {
            return Response.error("Failed to delete event: " + e.getMessage());
        }
    }

    /** GET_CATEGORIES: {} → List<Category> */
    private Response handleGetCategories()
    {
        try
        {
            List<Category> categories = manager.getCategoryService().findAll();
            return Response.ok(categories);
        }
        catch (Exception e)
        {
            return Response.error("Failed to load categories: " + e.getMessage());
        }
    }

    /** ADD_CATEGORY: { name, description } → Category */
    private Response handleAddCategory(Map<String, Object> p)
    {
        try
        {
            String name        = str(p, "name");
            String description = strOrEmpty(p, "description");
            Category category = manager.getCategoryService().add(name, description);
            return Response.ok(category);
        }
        catch (IllegalArgumentException e)
        {
            return Response.error("FIELD:name:" + e.getMessage());
        }
        catch (IllegalStateException e)
        {
            return Response.error("FIELD:name:Category already exists");
        }
        catch (Exception e)
        {
            return Response.error("Failed to add category: " + e.getMessage());
        }
    }

    /** EDIT_CATEGORY: { currentName, newName, description } → Category */
    private Response handleEditCategory(Map<String, Object> p)
    {
        try
        {
            String currentName = str(p, "currentName");
            String newName     = str(p, "newName");
            String description = strOrEmpty(p, "description");
            Category updated = manager.getCategoryService()
                    .edit(currentName, newName, description);
            return Response.ok(updated);
        }
        catch (IllegalArgumentException e)
        {
            return Response.error("FIELD:name:" + e.getMessage());
        }
        catch (IllegalStateException e)
        {
            return Response.error("FIELD:name:" + e.getMessage());
        }
        catch (Exception e)
        {
            return Response.error("Failed to edit category: " + e.getMessage());
        }
    }

    /** DELETE_CATEGORY: { name } → null */
    private Response handleDeleteCategory(Map<String, Object> p)
    {
        try
        {
            String name = str(p, "name");
            manager.getCategoryService().delete(name);
            return Response.ok(null);
        }
        catch (IllegalArgumentException | IllegalStateException e)
        {
            return Response.error(e.getMessage());
        }
        catch (Exception e)
        {
            return Response.error("Failed to delete category: " + e.getMessage());
        }
    }

    /** GET_CITIES: {} → List<City> */
    private Response handleGetCities()
    {
        try
        {
            List<City> cities = manager.getEventService().getAllCities();
            return Response.ok(cities);
        }
        catch (Exception e)
        {
            return Response.error("Failed to load cities: " + e.getMessage());
        }
    }

    /** PURCHASE_TICKET: { eventId, userEmail, quantity } → Ticket */
    private synchronized Response handlePurchaseTicket(Map<String, Object> p)
    {
        try
        {
            int    eventId   = intVal(p, "eventId");
            String userEmail = str(p, "userEmail");
            int    quantity  = intVal(p, "quantity");

            List<Ticket> tickets = manager.getTicketService()
                    .purchaseTicket(eventId, userEmail, quantity);
            // Return the first (and only) ticket
            return Response.ok(tickets.get(0));
        }
        catch (IllegalArgumentException | IllegalStateException e)
        {
            return Response.error("FIELD:quantity:" + e.getMessage());
        }
        catch (Exception e)
        {
            return Response.error("Purchase failed: " + e.getMessage());
        }
    }

    /** GET_MY_TICKETS: { userEmail } → List<Ticket> */
    private Response handleGetMyTickets(Map<String, Object> p)
    {
        try
        {
            String userEmail = str(p, "userEmail");
            List<Ticket> tickets = manager.getTicketService()
                    .getTicketsByUser(userEmail);
            return Response.ok(tickets);
        }
        catch (Exception e)
        {
            return Response.error("Failed to load tickets: " + e.getMessage());
        }
    }

    /** GET_SALES_REPORT: { eventId } → TicketSalesDto */
    private Response handleGetSalesReport(Map<String, Object> p)
    {
        try
        {
            int eventId = intVal(p, "eventId");
            TicketSalesDto report = manager.getTicketService().getSalesReport(eventId);
            if (report == null)
            {
                return Response.error("Sales report not available for event: " + eventId);
            }
            return Response.ok(report);
        }
        catch (Exception e)
        {
            return Response.error("Failed to load sales report: " + e.getMessage());
        }
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /** Build an Event from CREATE_EVENT or UPDATE_EVENT payload. */
    private Event buildEventFromPayload(int eventId, Map<String, Object> p)
    {
        String name         = str(p, "name");
        String description  = str(p, "description");
        String dateTimeStr  = str(p, "dateTime");
        String venue        = str(p, "venue");
        String address      = str(p, "address");
        String categoryName = str(p, "categoryName");
        double ticketPrice  = doubleVal(p, "ticketPrice");
        int    totalTickets = intVal(p, "totalTickets");
        String imageURL     = strOrNull(p, "imageURL");
        Integer zipCode     = intOrNull(p, "zipCode");

        // Parse status if provided, default to PUBLISHED
        EventStatus status = EventStatus.PUBLISHED;
        String statusStr = strOrNull(p, "status");
        if (statusStr != null && !statusStr.isEmpty())
        {
            try { status = EventStatus.valueOf(statusStr); }
            catch (IllegalArgumentException ignored) {}
        }

        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DATE_TIME_FMT);

        return new Event(
                eventId,
                name,
                description,
                dateTime,
                venue,
                address,
                categoryName,
                ticketPrice,
                totalTickets,
                0,           // ticketsSold = 0 (will be ignored by UPDATE query anyway)
                imageURL,
                status,
                zipCode
        );
    }

    // ---- Payload extraction helpers ----

    private String str(Map<String, Object> p, String key)
    {
        Object v = p.get(key);
        return v == null ? "" : v.toString();
    }

    private String strOrNull(Map<String, Object> p, String key)
    {
        Object v = p.get(key);
        if (v == null) return null;
        String s = v.toString().trim();
        return s.isEmpty() ? null : s;
    }

    private String strOrEmpty(Map<String, Object> p, String key)
    {
        Object v = p.get(key);
        return v == null ? "" : v.toString();
    }

    private int intVal(Map<String, Object> p, String key)
    {
        Object v = p.get(key);
        if (v == null) throw new IllegalArgumentException("Missing field: " + key);
        // Gson deserializes numbers as Double when the type is Object
        if (v instanceof Double) return ((Double) v).intValue();
        if (v instanceof Number) return ((Number) v).intValue();
        return Integer.parseInt(v.toString());
    }

    private Integer intOrNull(Map<String, Object> p, String key)
    {
        Object v = p.get(key);
        if (v == null) return null;
        String s = v.toString().trim();
        if (s.isEmpty() || s.equals("null")) return null;
        if (v instanceof Double) return ((Double) v).intValue();
        if (v instanceof Number) return ((Number) v).intValue();
        return Integer.parseInt(s);
    }

    private double doubleVal(Map<String, Object> p, String key)
    {
        Object v = p.get(key);
        if (v == null) throw new IllegalArgumentException("Missing field: " + key);
        if (v instanceof Double) return (Double) v;
        if (v instanceof Number) return ((Number) v).doubleValue();
        return Double.parseDouble(v.toString());
    }

    private LocalDate localDateOrNull(Map<String, Object> p, String key)
    {
        Object v = p.get(key);
        if (v == null) return null;
        String s = v.toString().trim();
        if (s.isEmpty() || s.equals("null")) return null;
        return LocalDate.parse(s, DATE_FMT);
    }
}
