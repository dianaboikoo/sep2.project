package Shared;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

        // Gson doesn't handle LocalDateTime natively, so we register adapters for it
        builder.registerTypeAdapter(LocalDateTime.class,
                (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));

        builder.registerTypeAdapter(LocalDateTime.class,
                (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                        LocalDateTime.parse(json.getAsString(),
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return builder.create();
    }
}
