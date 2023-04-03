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
import java.sql.Date;
import java.util.Map;
import com.sun.net.httpserver.HttpExchange;

public class RootHandler extends RequestHandler {
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
        // Extract search parameter from URL query string
        String queryString = exchange.getRequestURI().getQuery();
        String searchParam = null;
        if (queryString != null) {
            Map<String, String> queryParams = QueryStringParser.parse(queryString);
            searchParam = queryParams.get("search");
        }

        String data = readFromFile(FILE_PATH);
        String htmlResponse = "<html>";
        htmlResponse += "<head><title>Appointments</title>";
        htmlResponse += "<style>body{margin:0;padding:0;font-family:sans-serif;background:#fff}h1{text-align:center;color:#34495e;padding:10px}table{width:80%;margin:0 auto;border-collapse:collapse}th,td{border:1px solid #fff;padding:5px;text-align:center}td{color:#162533}tr:nth-child(even){background:#eee}tr:nth-child(odd){background:#fff}a{color:#162533;text-decoration:none}a:hover{color:#162533;text-decoration:underline}div{text-align:center}div a{padding:10px;background:#2980b9;border:1px solid #fff;color:#fff;text-decoration:none}div a:hover{background:#3498db}</style>";
        htmlResponse += "</head>";
        htmlResponse += "<body><h1>Appointments</h1><table>";
        if (data.isEmpty()) {
            htmlResponse += "<tr><td>No appointments</td></tr>";
        }
        else {
            String[] rows = data.split("\n");
            for (String row : rows) {
                if (searchParam != null && !row.contains(searchParam)) {
                    continue; // skip row if search parameter not found
                }
                String[] columns = row.split(",");
                htmlResponse += "<tr>";
                if (columns.length == 0) {
                    htmlResponse += "<td>No appointments</td>";
                } else {
                    Appointment appointment = new Appointment(Integer.parseInt(columns[0]), columns[1], columns[2], columns[3]);
                        Date date = appointment.getDate();
                        htmlResponse += "<td>" + appointment.getName() + "</td>";
                        htmlResponse += "<td>" + date.toLocalDate() + "</td>";
                        htmlResponse += "<td>" + appointment.getTime() + "</td>";
                        htmlResponse += "<td><a href=\"/edit?id=" + appointment.getId() + "\">edit</a></td>";
                        htmlResponse += "<td><a href=\"/delete?id=" + appointment.getId() + "\">Delete</a></td>";
                        htmlResponse += "</tr>";
                }
            }
        }
        htmlResponse += "</table><br>";
        htmlResponse += "<div>";
        htmlResponse += "<a href=\"/add\">Add</a>";
        htmlResponse += "<a href=\"/search\">Search</a>";
        htmlResponse += "</div>";
        htmlResponse += "</body></html>";
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, htmlResponse.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(htmlResponse.getBytes());
        os.close();
    }

    protected void handlePostRequest(HttpExchange exchange) throws IOException {
        // Return 404 since POST requests are not allowed on this page
        String htmlResponse = "<html>";
        htmlResponse += "<head><title>404 Not Found</title>";
        htmlResponse += "<style>body{font-family:Arial,Helvetica,sans-serif;background-color:#f2f2f2;text-align:center}h1{color:#34495e}p{font-size:20px}a{padding:10px;background:#2980b9;border:1px solid #fff;color:#fff;text-decoration:none;border-radius:5px}a:hover{background:#3498db}</style>";
        htmlResponse += "</head>";
        htmlResponse += "<body><h1>404 Not Found</h1></body></html>";
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(404, htmlResponse.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(htmlResponse.getBytes());
        os.close();
    }

}