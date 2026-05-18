package Server;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Entry point for the server process.
 *
 * Start this BEFORE launching any clients.
 * Listens on port 8080 and spawns a ClientHandler thread for every incoming connection.
 * Multiple clients can connect simultaneously.
 *
 * Run order:
 *   1. Start Server.java  (connects to PostgreSQL, listens on :8080)
 *   2. Start Main.java    (JavaFX client, connects to localhost:8080)
 */
public class Server
{
    private static final int PORT = 8080;

    public static void main(String[] args)
    {
        // Initialize all repositories and services eagerly so any DB errors
        // surface at startup, not on the first client request.
        System.out.println("Initializing server model...");
        ServerModelManager manager = ServerModelManager.getInstance();
        System.out.println("Model initialized successfully.");

        try (ServerSocket serverSocket = new ServerSocket(PORT))
        {
            System.out.println("Server started on port " + PORT);

            while (true)
            {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: "
                        + clientSocket.getInetAddress().getHostAddress());

                Thread clientThread = new Thread(
                        new ClientHandler(clientSocket, manager));
                clientThread.setDaemon(true);
                clientThread.start();
            }
        }
        catch (Exception e)
        {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
