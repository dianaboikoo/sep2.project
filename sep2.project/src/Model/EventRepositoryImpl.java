package Model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.DriverManager;

public class EventRepositoryImpl implements EventRepository
{
  //using singleton = driver is only registered once and same repository obj is reused throughout the apps lifetime
  private static EventRepositoryImpl instance;

  private EventRepositoryImpl() throws SQLException
  {
    DriverManager.registerDriver(new org.postgresql.Driver());
  }

  //constructor is private so getInstance is the only way to enter the class
  public static synchronized EventRepositoryImpl getInstance() throws SQLException
  {
    if (instance == null)
    {
      instance = new EventRepositoryImpl();
    }
    return instance;
  }

  private Connection getConnection() throws SQLException
  {
    //each of us has different passowrd, so when running it you need to change to your personal Postgres pasword
    return DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres?currentSchema=events",
        "postgres", "1234");
  }

  //CRUD method = Create, Read, Update, Delete
  @Override
  public Event save(Event event) throws SQLException
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement(
          "INSERT INTO events(name, description, date_time, venue, address, category_name, "
          + "zip_code, ticket_price, total_tickets, tickets_sold, status, imageurl) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
      statement.setString(1, event.getName());
      statement.setString(2, event.getDescription());
      statement.setTimestamp(3, java.sql.Timestamp.valueOf(event.getDateTime()));
      statement.setString(4, event.getVenue());
      statement.setString(5, event.getAddress());
      statement.setString(6, event.getCategoryName());
      if (event.getZipCode() != null)
        statement.setInt(7, event.getZipCode());
      else
        statement.setNull(7, java.sql.Types.NUMERIC);
      statement.setDouble(8, event.getTicketPrice());
      statement.setInt(9, event.getTotalTickets());
      statement.setInt(10, event.getTicketsSold());
      statement.setString(11, event.getStatus().toString());
      statement.setString(12, event.getImageURL());

      statement.executeUpdate();
      return event;
    }
  }

  @Override
  public Event findById(int id) throws SQLException
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT * FROM events WHERE event_id = ?");
      statement.setInt(1, id);
      //resultSet = what comes back from database after execution (like results)
      //executeQuery = what sends the SQL to database and runs it (when we expect data back)
      ResultSet resultSet = statement.executeQuery();
      //looking for one specific event by ID = only one result + return
      if (resultSet.next())
      {
        return createEvent(resultSet);
      }
      return null;
    }
  }

  @Override
  public List<Event> findAll() throws SQLException
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT * FROM events");
      ResultSet resultSet = statement.executeQuery();
      ArrayList<Event> result = new ArrayList<>();
      //while = fetching every event in database = keep looping + add each to list + return whole list
      while (resultSet.next())
      {
        result.add(createEvent(resultSet));
      }
      return result;
    }
  }

  @Override
  public void delete(int id) throws SQLException
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement(
          "DELETE FROM events WHERE event_id = ?");
      statement.setInt(1, id);
      statement.executeUpdate();
    }
  }

  @Override
  public boolean exists(int id) throws SQLException
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT 1 FROM events WHERE event_id = ?");
      statement.setInt(1, id);
      ResultSet resultSet = statement.executeQuery();
      return resultSet.next();
    }
  }

  //helping to convert database raw data results to java Event obj
  //thanks to this we are manually mapping each result column into right field
  private Event createEvent(ResultSet resultSet) throws SQLException
  {
    return new Event(
        resultSet.getInt("event_id"),
        resultSet.getString("name"),
        resultSet.getString("description"),
        resultSet.getTimestamp("date_time").toLocalDateTime(),
        resultSet.getString("venue"),
        resultSet.getString("address"),
        resultSet.getString("category_name"),
        resultSet.getDouble("ticket_price"),
        resultSet.getInt("total_tickets"),
        resultSet.getInt("tickets_sold"),
        resultSet.getString("imageurl"),
        EventStatus.valueOf(resultSet.getString("status")) //converts string "DRAFT" stored in dbs to Java value as enum
    );
  }


    @Override
    public List<EventListDto> findAllPublished() throws SQLException
    {
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT e.event_id, e.name, e.date_time, e.venue, e.address, " +
                            "e.total_tickets, e.tickets_sold, " +
                            "COALESCE(e.category_name, 'Uncategorized') as category_name, " +
                            "COALESCE(c.name, '') as city_name " +
                            "FROM events e " +
                            "LEFT JOIN city c ON e.zip_code = c.zip_code " +
                            "WHERE e.date_time > NOW() " +
                            "ORDER BY e.date_time ASC");
            ResultSet rs = statement.executeQuery();
            List<EventListDto> result = new ArrayList<>();
            while (rs.next())
            {
                result.add(new EventListDto(
                        rs.getInt("event_id"),
                        rs.getString("name"),
                        rs.getTimestamp("date_time").toLocalDateTime(),
                        rs.getString("venue"),
                        rs.getString("address"),
                        rs.getString("category_name"),
                        rs.getString("city_name"),
                        rs.getInt("total_tickets"),
                        rs.getInt("tickets_sold")
                ));
            }
            return result;
        }
    }

    @Override
    public List<EventListDto> findAllPublishedFiltered(String category, Integer zipCode,
                                                      LocalDate fromDate, LocalDate toDate) throws SQLException
    {
        StringBuilder sql = new StringBuilder(
                "SELECT e.event_id, e.name, e.date_time, e.venue, e.address, " +
                        "e.total_tickets, e.tickets_sold, " +
                        "COALESCE(e.category_name, 'Uncategorized') as category_name, " +
                        "COALESCE(ci.name, '') as city_name " +
                        "FROM events e " +
                        "JOIN category c ON e.category_name = c.name " +
                        "LEFT JOIN city ci ON e.zip_code = ci.zip_code " +
                        "WHERE e.status = 'PUBLISHED' AND e.date_time > NOW()");

        List<Object> params = new ArrayList<>();

        if (category != null)
        {
            sql.append(" AND e.category_name = ?");
            params.add(category);
        }
        if (zipCode != null)
        {
            sql.append(" AND e.zip_code = ?");
            params.add(zipCode);
        }
        if (fromDate != null)
        {
            sql.append(" AND e.date_time >= ?");
            params.add(java.sql.Timestamp.valueOf(fromDate.atStartOfDay()));
        }
        if (toDate != null)
        {
            sql.append(" AND e.date_time <= ?");
            params.add(java.sql.Timestamp.valueOf(toDate.atTime(23, 59, 59)));
        }

        sql.append(" ORDER BY e.date_time ASC");

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++)
            {
                Object param = params.get(i);
                if (param instanceof String)
                {
                    statement.setString(i + 1, (String) param);
                }
                else if (param instanceof Integer)
                {
                    statement.setInt(i + 1, (Integer) param);
                }
                else if (param instanceof java.sql.Timestamp)
                {
                    statement.setTimestamp(i + 1, (java.sql.Timestamp) param);
                }
                else
                {
                    statement.setObject(i + 1, param);
                }
            }

            ResultSet rs = statement.executeQuery();
            List<EventListDto> result = new ArrayList<>();
            while (rs.next())
            {
                result.add(new EventListDto(
                        rs.getInt("event_id"),
                        rs.getString("name"),
                        rs.getTimestamp("date_time").toLocalDateTime(),
                        rs.getString("venue"),
                        rs.getString("address"),
                        rs.getString("category_name"),
                        rs.getString("city_name"),
                        rs.getInt("total_tickets"),
                        rs.getInt("tickets_sold")
                ));
            }
            return result;
        }
    }

    @Override
    public EventDetailDto findPublishedById(int id) throws SQLException
    {
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT e.event_id, e.name, e.description, e.date_time, e.venue, e.address, " +
                            "e.ticket_price, e.total_tickets, e.tickets_sold, " +
                            "COALESCE(e.category_name, 'Uncategorized') as category_name, " +
                            "COALESCE(c.name, '') as city_name " +
                            "FROM events e " +
                            "LEFT JOIN city c ON e.zip_code = c.zip_code " +
                            "WHERE e.event_id = ?");
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
            {
                return new EventDetailDto(
                        rs.getInt("event_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getTimestamp("date_time").toLocalDateTime(),
                        rs.getString("venue"),
                        rs.getString("address"),
                        rs.getString("category_name"),
                        rs.getString("city_name"),
                        rs.getDouble("ticket_price"),
                        rs.getInt("total_tickets"),
                        rs.getInt("tickets_sold")
                );
            }
            return null;
        }
    }

  @Override
  public List<City> findAllCities() throws SQLException
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT zip_code, name FROM city ORDER BY name");
      ResultSet rs = statement.executeQuery();

      List<City> cities = new ArrayList<>();
      while (rs.next())
      {
        cities.add(new City(rs.getInt("zip_code"), rs.getString("name")));
      }
      return cities;
    }
  }
    public boolean updateTicketsSold(int eventId, int quantity) throws SQLException
    {
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE events " +
                            "SET tickets_sold = tickets_sold + ? " +
                            "WHERE event_id = ? " +
                            "AND tickets_sold + ? <= total_tickets;");
            statement.setInt(1, quantity);
            statement.setInt(2, eventId);
            statement.setInt(3, quantity);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    //no ticket sold = admin shouldn't be able to manipulate sold ticket counts
    //status = keeping it PUBLISHED as set when created
    //created_at = never changes after creation
    public Event update(Event event) throws SQLException
    {
      try (Connection connection = getConnection())
      {
        PreparedStatement statement = connection.prepareStatement(
            "UPDATE events SET " +
                "name = ?, " +
                "description = ?, " +
                "date_time = ?, " +
                "venue = ?, " +
                "address = ?, " +
                "category_name = ?, " +
                "zip_code = ?, " +
                "ticket_price = ?, " +
                "total_tickets = ?, " +
                "imageurl = ?, " +
                "updated_at = CURRENT_TIMESTAMP " +
                "WHERE event_id = ?");

        statement.setString(1, event.getName());
        statement.setString(2, event.getDescription());
        statement.setTimestamp(3, Timestamp.valueOf(event.getDateTime()));
        statement.setString(4, event.getVenue());
        statement.setString(5, event.getAddress());
        statement.setString(6, event.getCategoryName());
        if (event.getZipCode() !=null)
          statement.setInt(7, event.getZipCode());
        else
          statement.setNull(7, Types.NUMERIC);
        statement.setDouble(8, event.getTicketPrice());
        statement.setInt(9, event.getTotalTickets());
        statement.setString(10, event.getImageURL());
        statement.setInt(11, event.getEventId());

        statement.executeUpdate();
        return event;
      }
    }
  }
