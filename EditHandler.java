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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

public class EditHandler extends RequestHandler{
    private static final String FILE_PATH = "data.txt";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equalsIgnoreCase("GET")) {
            handleGetRequest(exchange);
        } else if (method.equalsIgnoreCase("POST")) {
            handlePostRequest(exchange);
        }
    }

    @Override
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
        System.out.println("id: " + id);
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
            htmlResponse += "<head><title>Edit appointment</title>";
            htmlResponse += "<style>body{font-family:Arial,Helvetica,sans-serif;padding:20px}h1{color:#34495e;text-align:center}form{border:3px solid #f1f1f1;width:50%;margin:0 auto;padding:20px}input[type=text],input[type=date],input[type=time]{width:100%;padding:12px 20px;margin:8px 0;display:inline-block;border:1px solid #ccc;box-sizing:border-box}input[type=submit]{background-color:#34495e;color:#fff;padding:14px 20px;margin:8px 0;border:none;cursor:pointer;width:100%}input[type=submit]:hover{opacity:.8}</style>";
            htmlResponse += "</head>";
            htmlResponse += "<body><h1>Edit appointment</h1>";
            htmlResponse += "<form method=\"POST\" action=\"/edit\">";
            htmlResponse += "<input type=\"hidden\" name=\"id\" value=\"" + id + "\">";
            htmlResponse += "<label for=\"name\">Name:</label><br>";
            htmlResponse += "<input type=\"text\" name=\"name\" id=\"name\" value=\"" + name + "\"><br><br>";
            htmlResponse += "<label for=\"date\">Date:</label><br>";
            htmlResponse += "<input type=\"date\" name=\"date\" id=\"date\" value=\"" + date + "\"><br><br>";
            htmlResponse += "<label for=\"time\">Time:</label><br>";
            htmlResponse += "<input type=\"time\" name=\"time\" id=\"time\" value=\"" + time + "\"><br><br>";
            htmlResponse += "<input type=\"submit\" value=\"Update\">";
            htmlResponse += "</form>";
            htmlResponse += "</body></html>";
    
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, htmlResponse.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(htmlResponse.getBytes());
            os.close();
        }
    }
    

    @Override
    protected void handlePostRequest(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String[] fields = requestBody.split("&");
        int id = Integer.parseInt(fields[0].split("=")[1]);
        String name = fields[1].split("=")[1];
        String date = fields[2].split("=")[1];
        String time = fields[3].split("=")[1];
        String decoded = URLDecoder.decode(time, "UTF-8");
    
        // read existing data from file
        String data = readFromFile(FILE_PATH);
    
        // find the line with the matching id and replace with new data
        String[] lines = data.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (line.startsWith(String.valueOf(id))) {
                sb.append(id).append(",").append(name).append(",").append(date).append(",").append(decoded).append("\n");
            } else {
                sb.append(line).append("\n");
            }
        }
        String updatedData = sb.toString();
    
        // write the updated data back to the file
        writeToFile(updatedData, FILE_PATH, false);
    
        String successResponse = "<html>";
        successResponse += "<head><title>Success</title>";
        successResponse += "<style>body{font-family:Arial,Helvetica,sans-serif;background-color:#f2f2f2;text-align:center}h1{color:#34495e}p{font-size:20px}a{padding:10px;background:#2980b9;border:1px solid #fff;color:#fff;text-decoration:none;border-radius:5px}a:hover{background:#3498db}</style>";
        successResponse += "</head>";
        successResponse += "<body><h1>Success</h1><p>The data has been updated.</p><a href=\"/\">Back</a></body></html>";
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, successResponse.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(successResponse.getBytes());
        os.close();
    }
    
    
}
