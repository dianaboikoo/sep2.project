package Shared;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Shared Gson factory used by both the client (ServerConnection) and
 * the server (ClientHandler) to ensure identical serialization/deserialization.
 *
 * Registers a LocalDateTime type adapter that uses ISO_LOCAL_DATE_TIME format,
 * so LocalDateTime fields in domain objects are correctly round-tripped over JSON.
 */
public class GsonFactory
{
    private static final Gson INSTANCE = buildGson();

    private GsonFactory() {}

    public static Gson get()
    {
        return INSTANCE;
    }

    private static Gson buildGson()
    {
        GsonBuilder builder = new GsonBuilder();

        // Serialize LocalDateTime → ISO string (e.g. "2025-06-01T14:30:00")
        builder.registerTypeAdapter(LocalDateTime.class,
                (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));

        // Deserialize ISO string → LocalDateTime
        builder.registerTypeAdapter(LocalDateTime.class,
                (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                        LocalDateTime.parse(json.getAsString(),
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return builder.create();
    }
}
