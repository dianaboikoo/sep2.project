package Client;

import Shared.GsonFactory;
import Shared.Request;
import Shared.Response;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Singleton TCP client that manages the persistent connection to the server.
 *
 * Usage:
 *   ServerConnection.getInstance().send(new Request("LOGIN", Map.of(...)));
 *
 * All communication is JSON over a single persistent TCP socket (port 8080).
 * The send() method is synchronized to prevent concurrent writes on the same socket.
 */
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
        this.out    = new PrintWriter(socket.getOutputStream(), true); // auto-flush
        this.in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.gson   = GsonFactory.get();
    }

    /**
     * Returns the singleton ServerConnection, creating it on the first call.
     * Throws an exception if the server cannot be reached.
     */
    public static synchronized ServerConnection getInstance() throws Exception
    {
        if (instance == null)
        {
            instance = new ServerConnection();
        }
        return instance;
    }

    /**
     * Sends a request to the server and returns the response.
     * Synchronized to prevent interleaving of concurrent requests on the same socket.
     */
    public synchronized Response send(Request request) throws Exception
    {
        String json = gson.toJson(request);
        out.println(json);           // write JSON line (auto-flushed)
        String responseLine = in.readLine();   // read response line
        if (responseLine == null)
        {
            throw new RuntimeException("Server closed the connection unexpectedly");
        }
        return gson.fromJson(responseLine, Response.class);
    }

    /**
     * Closes the underlying socket. Call on application shutdown.
     */
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
