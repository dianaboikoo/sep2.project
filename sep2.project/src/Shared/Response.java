package Shared;

public class Response
{
    private String status;
    private Object data;
    private String message;

    public Response(String status, Object data, String message)
    {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static Response ok(Object data)
    {
        return new Response("OK", data, "");
    }

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
