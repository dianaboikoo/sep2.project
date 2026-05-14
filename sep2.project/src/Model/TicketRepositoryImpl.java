package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TicketRepositoryImpl implements TicketRepository
{
    private static TicketRepositoryImpl instance;

    private TicketRepositoryImpl() throws SQLException
    {
        DriverManager.registerDriver(new org.postgresql.Driver());
    }

    public static synchronized TicketRepositoryImpl getInstance() throws SQLException
    {
        if (instance == null)
        {
            instance = new TicketRepositoryImpl();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/postgres?currentSchema=events",
                "postgres", "252006");
    }

    @Override
    public Ticket save(Ticket ticket) throws SQLException
    {
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO tickets(ticket_id, event_id, user_email, " +
                            "purchase_date, quantity, status) " +
                            "VALUES (?, ?, ?, ?, ?, ?);");
            statement.setString(1, ticket.getTicketId());
            statement.setInt(2, ticket.getEventId());
            statement.setString(3, ticket.getUserEmail());
            statement.setTimestamp(4,
                    java.sql.Timestamp.valueOf(ticket.getPurchaseDate()));
            statement.setInt(5, ticket.getQuantity());
            statement.setString(6, ticket.getStatus().toString());
            statement.executeUpdate();
            return ticket;
        }
    }

    @Override
    public List<Ticket> findByUserEmail(String email) throws SQLException
    {
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM tickets " +
                            "WHERE user_email = ? " +
                            "ORDER BY purchase_date DESC;");
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            List<Ticket> result = new ArrayList<>();
            while (rs.next())
            {
                result.add(createTicket(rs));
            }
            return result;
        }
    }

    @Override
    public List<Ticket> findByEventId(int eventId) throws SQLException
    {
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM tickets WHERE event_id = ?;");
            statement.setInt(1, eventId);
            ResultSet rs = statement.executeQuery();
            List<Ticket> result = new ArrayList<>();
            while (rs.next())
            {
                result.add(createTicket(rs));
            }
            return result;
        }
    }

    private Ticket createTicket(ResultSet rs) throws SQLException
    {
        return new Ticket(
                rs.getString("ticket_id"),
                rs.getInt("event_id"),
                rs.getString("user_email"),
                rs.getTimestamp("purchase_date").toLocalDateTime(),
                rs.getInt("quantity"),
                TicketStatus.valueOf(rs.getString("status"))
        );
    }
}