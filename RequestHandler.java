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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class RequestHandler implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equalsIgnoreCase("GET")) {
            handleGetRequest(exchange);
        } else if (method.equalsIgnoreCase("POST")) {
            handlePostRequest(exchange);
        }
    }
    
    protected abstract void handleGetRequest(HttpExchange exchange) throws IOException;

    protected abstract void handlePostRequest(HttpExchange exchange) throws IOException;

    protected String readFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileInputStream fis = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        reader.close();
        return sb.toString();
    }
    

    protected void writeToFile(String data, String filePath, boolean append) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file, append);
        fos.write(data.getBytes(StandardCharsets.UTF_8));
        fos.close();
    }
}
