package Shared;

import java.util.Map;

public class Request
{
    private String type;
    private Map<String, Object> payload;

    public Request(String type, Map<String, Object> payload)
    {
        this.type = type;
        this.payload = payload;
    }

    public String getType()
    {
        return type;
    }

    public Map<String, Object> getPayload()
    {
        return payload;
    }
}
