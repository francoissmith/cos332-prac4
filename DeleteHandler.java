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
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;


public class DeleteHandler extends RequestHandler {
    private static final String FILE_PATH = "data.txt";

    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equalsIgnoreCase("GET")) {
            handleGetRequest(exchange);
        } else if (method.equalsIgnoreCase("POST")) {
            handlePostRequest(exchange);
        }
    }

    protected void handleGetRequest(HttpExchange exchange) throws IOException {
        // Extract ID parameter from URL query string
        String queryString = exchange.getRequestURI().getQuery();
        String id = null;
        if (queryString != null) {
            Map<String, String> queryParams = QueryStringParser.parse(queryString);
            id = queryParams.get("id");
        }

        String data = readFromFile(FILE_PATH); // read data from file
        String[] rows = data.split("\n");
        String rowWithId = null;
        for (String row : rows) {
            String[] columns = row.split(",");
            if (columns.length > 0 && columns[0].equals(id)) {
                rowWithId = row;
                break;
            }
        }

        // check if appointment with specified ID was found
        if (rowWithId == null) {
            // appointment with specified ID not found
            String htmlResponse = "<html><body><h1>Appointment not found</h1></body></html>";
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(404, htmlResponse.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(htmlResponse.getBytes());
            os.close();
        } else {
            // appointment with specified ID found
            String[] columns = rowWithId.split(",");
            String name = columns[1];
            String date = columns[2];
            String time = columns[3];

            // create HTML response with appointment form
            String htmlResponse = "<html>";
            htmlResponse += "<head><title>Delete Appointment</title>";
            htmlResponse += "<style>body{font-family:Arial,Helvetica,sans-serif;padding:20px}h1{color:#34495e;text-align:center}form{border:3px solid #f1f1f1;width:50%;margin:0 auto;padding:20px}input[type=text],input[type=date],input[type=time]{width:100%;padding:12px 20px;margin:8px 0;display:inline-block;border:1px solid #ccc;box-sizing:border-box}input[type=submit]{background-color:#34495e;color:#fff;padding:14px 20px;margin:8px 0;border:none;cursor:pointer;width:100%}input[type=submit]:hover{opacity:.8}</style>";
            htmlResponse += "</head>";
            htmlResponse += "<body><h1>Delete Appointment</h1><form method=\"POST\" action=\"/delete?id=" + id + "\">"
                    + "<p>Name: " + name + "</p>" + "<p>Date: " + date + "</p>" + "<p>Time: " + time + "</p>"
                    + "<input type=\"submit\" value=\"Delete\" />" + "</form></body></html>";
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, htmlResponse.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(htmlResponse.getBytes());
            os.close();
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange exchange) throws IOException {
        // Extract ID parameter from URL query string
        String queryString = exchange.getRequestURI().getQuery();
        String id = null;
        if (queryString != null) {
            Map<String, String> queryParams = QueryStringParser.parse(queryString);
            id = queryParams.get("id");
        }
        System.out.println("id: "+id);
    
        // Read file into list of items
        List<String> items = new ArrayList<>();
        String data = readFromFile(FILE_PATH);
        String[] rows = data.split("\n");
        for (String row : rows) {
            if (!row.isEmpty()) {
                items.add(row);
            }
        }
    
        // Remove item with matching ID and update remaining IDs
        int indexToRemove = -1;
        for (int i = 0; i < items.size(); i++) {
            String[] columns = items.get(i).split(",");
            if (columns[0].equals(id)) {
                indexToRemove = i;
            } else if (indexToRemove != -1) {
                // Update ID of items below removed item
                int newId = Integer.parseInt(columns[0]) - 1;
                columns[0] = Integer.toString(newId);
                items.set(i, String.join(",", columns));
            }
        }
        System.out.println(indexToRemove);
        if (indexToRemove != -1) {
            System.out.println(items.toString());
            items.remove(indexToRemove);
        }
    
        // Write updated list back to file
        StringBuilder sb = new StringBuilder();
        for (String item : items) {
            sb.append(item + "\n");
        }
        writeToFile(sb.toString(), FILE_PATH, false);
    
        // Redirect to home page
        exchange.getResponseHeaders().set("Location", "/");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }
    

}