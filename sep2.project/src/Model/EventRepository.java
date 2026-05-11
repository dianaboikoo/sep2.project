package Model;
//implementing it as a normal class instead of interface breaks the dbs connection
import java.util.List;
import java.sql.SQLException;
import java.time.LocalDate;

public interface EventRepository
{
 Event save(Event event) throws SQLException;
 Event findById(int id) throws SQLException;
 List<Event> findAll() throws SQLException;
 void delete(int id) throws SQLException;
 boolean exists(int id) throws SQLException;
 List<EventListDto> findAllPublished() throws SQLException;
 List<EventListDto> findAllPublishedFiltered(String category, Integer zipCode, LocalDate fromDate, LocalDate toDate) throws SQLException;
 EventDetailDto findPublishedById(int id) throws SQLException;
}