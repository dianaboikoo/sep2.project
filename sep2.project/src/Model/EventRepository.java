package Model;
//implementing it as a normal class instead of interface breaks the dbs connection
import java.util.List;
import java.sql.SQLException;

public interface EventRepository
{
 Event save(Event event) throws SQLException;
 Event findById(int id) throws SQLException;
 List<Event> findAll() throws SQLException;
 void delete(int id) throws SQLException;
 boolean exists(int id) throws SQLException;
 List<EventListDto> findAllPublished() throws SQLException;
 EventDetailDto findPublishedById(int id) throws SQLException;
}