package Shared;

/**
 * Represents a response sent from the server to the client over TCP.
 * Serialized as a single JSON line.
 */
public class Response
{
    private String status;   // "OK" or "ERROR"
    private Object data;
    private String message;

    public Response(String status, Object data, String message)
    {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    /** Factory: successful response with a data payload. */
    public static Response ok(Object data)
    {
        return new Response("OK", data, "");
    }

    /** Factory: error response with a human-readable message. */
    public static Response error(String message)
    {
        return new Response("ERROR", null, message);
    }

    public boolean isOk()
    {
        return "OK".equals(status);
    }

    public String getStatus()
    {
        return status;
    }

    public Object getData()
    {
        return data;
    }

    public String getMessage()
    {
        return message;
    }
}
