/*
=================================================================
| PROJECT:              | COS 332 Practical 4                   |
|-----------------------|---------------------------------------|
| PROJECT PROGRAMMER:   | Francois Smith                        |
|-----------------------|---------------------------------------|
| STUDENT NUMBER:       | u19314486                             |
|-----------------------|---------------------------------------|
| DUE DATE:             | 03-04-2023                            |
=================================================================
*/
import java.net.*;

import com.sun.net.httpserver.*;

public class Server {
    public static void main(String[] args) throws Exception {
        
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", new RootHandler());
        server.createContext("/add", new AddHandler());
        server.createContext("/search", new SearchHandler());
        server.createContext("/delete", new DeleteHandler());
        server.createContext("/edit", new EditHandler());

        server.setExecutor(null); // creates a default executor

        server.start();

        System.out.println("Appointment server listening on http://localhost:" + port);
    }
}
