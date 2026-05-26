package Server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    private static final int PORT = 8080;

    public static void main(String[] args)
    {
        // init eagerly so DB errors show up at startup, not on the first request
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
