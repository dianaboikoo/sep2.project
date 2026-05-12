package Model;

import java.sql.SQLException;
import java.util.List;

public interface TicketRepository
{
    Ticket save(Ticket ticket) throws SQLException;
    List<Ticket> findByUserEmail(String email) throws SQLException;
    List<Ticket> findByEventId(int eventId) throws SQLException;
}