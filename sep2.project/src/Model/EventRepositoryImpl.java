package Model;

import java.sql.*;
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
      PreparedStatement statement = connection.prepareStatement("INSERT INTO events(name, description, date_time, venue, address, "
          + "ticket_price, total_tickets, tickets_sold, status, imageurl) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
      statement.setString(1, event.getName());
      statement.setString(2, event.getDescription());
      statement.setTimestamp(3, java.sql.Timestamp.valueOf(event.getDateTime()));
      statement.setString(4, event.getVenue());
      statement.setString(5,event.getAddress());
      statement.setDouble(6, event.getTicketPrice());
      statement.setInt(7, event.getTotalTickets());
      statement.setInt(8, event.getTicketsSold());
      statement.setString(10, event.getStatus().toString());
      statement.setString(9, event.getImageURL());

      statement.executeUpdate(); //always need to execute/close at the end
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
        resultSet.getDouble("ticket_price"),
        resultSet.getInt("total_tickets"),
        resultSet.getInt("tickets_sold"),
        resultSet.getString("imageurl"),
        EventStatus.valueOf(resultSet.getString("status")) //converts string "DRAFT" stored in dbs to Java value as enum
    );
  }
}