package ViewModel;

import Client.ServerConnection;
import Model.UserRole;
import Shared.GsonFactory;
import Shared.Request;
import Shared.Response;
import com.google.gson.Gson;

import java.util.Map;

public class LoginViewModel
{
    public LoginViewModel()
    {
        // No dependencies — communicates via ServerConnection
    }

    /**
     * Sends a LOGIN request to the server.
     * Returns the matched UserRole, or null if credentials are wrong or an error occurs.
     */
    public UserRole login(String email, String password)
    {
        if (email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty())
        {
            return null;
        }

        try
        {
            Response response = ServerConnection.getInstance().send(
                    new Request("LOGIN", Map.of(
                            "email",    email.trim(),
                            "password", password.trim())));

            if (!response.isOk())
            {
                return null;
            }

            // data is { "role": "ADMIN" } or { "role": "USER" }
            Gson gson = GsonFactory.get();
            @SuppressWarnings("unchecked")
            Map<String, String> data = gson.fromJson(
                    gson.toJson(response.getData()), Map.class);
            String roleStr = data.get("role");
            return UserRole.valueOf(roleStr);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
