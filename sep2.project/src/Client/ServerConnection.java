package Client;

import Shared.GsonFactory;
import Shared.Request;
import Shared.Response;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnection
{
    private static final String HOST = "localhost";
    private static final int    PORT = 8080;

    private static ServerConnection instance;

    private final Socket       socket;
    private final PrintWriter  out;
    private final BufferedReader in;
    private final Gson          gson;

    private ServerConnection() throws Exception
    {
        this.socket = new Socket(HOST, PORT);
        this.out    = new PrintWriter(socket.getOutputStream(), true);
        this.in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.gson   = GsonFactory.get();
    }

    public static synchronized ServerConnection getInstance() throws Exception
    {
        if (instance == null)
        {
            instance = new ServerConnection();
        }
        return instance;
    }

    // synchronized so multiple ViewModels don't write to the socket at the same time
    public synchronized Response send(Request request) throws Exception
    {
        String json = gson.toJson(request);
        out.println(json);
        String responseLine = in.readLine();
        if (responseLine == null)
        {
            throw new RuntimeException("Server closed the connection unexpectedly");
        }
        return gson.fromJson(responseLine, Response.class);
    }

    public void close()
    {
        try
        {
            socket.close();
        }
        catch (Exception ignored) {}
        finally
        {
            instance = null;
        }
    }
}
