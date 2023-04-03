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
import java.util.Map;
import com.sun.net.httpserver.HttpExchange;

public class SearchHandler extends RequestHandler {
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

    protected void handleGetRequest(HttpExchange exchange) throws IOException {
        String data = readFromFile(FILE_PATH);
        String htmlResponse = "<html>";
        htmlResponse += "<head><title>Search Appointment</title>";
        htmlResponse += "<style>table{width:80%;margin:0 auto;border-collapse:collapse}th,td{border:1px solid #fff;padding:5px;text-align:center}td{color:#162533}tr:nth-child(even){background:#c2d5e8}tr:nth-child(odd){background:#fff}a{color:#162533;text-decoration:none;}a:hover{color:#162533;text-decoration:underline}body{font-family:Arial,Helvetica,sans-serif;padding:20px;text-align:center}h1{color:#34495e;text-align:center}form{border:3px solid #f1f1f1;width:50%;margin:0 auto;padding:20px}input[type=text],input[type=date],input[type=time]{width:100%;padding:12px 20px;margin:8px 0;display:inline-block;border:1px solid #ccc;box-sizing:border-box}input[type=submit]{background-color:#34495e;color:#fff;padding:14px 20px;margin:8px 0;border:none;cursor:pointer;width:100%}input[type=submit]:hover{opacity:.8}</style>";
        htmlResponse += "</head>";
        htmlResponse += "<body><h1>Search Appointment</h1>";

        // Add radio buttons to select search type
        htmlResponse += "<form method=\"get\">";
        htmlResponse += "<input type=\"radio\" name=\"searchType\" value=\"name\" checked> Name";
        htmlResponse += "<input type=\"radio\" name=\"searchType\" value=\"date\"> Date";
        htmlResponse += "<input type=\"radio\" name=\"searchType\" value=\"time\"> Time";
        htmlResponse += "<br><br>";

        // Add search input
        htmlResponse += "<label for=\"search\">Search:</label>";
        htmlResponse += "<input type=\"text\" name=\"search\" id=\"search\">";
        htmlResponse += "<input type=\"submit\" value=\"Search\">";
        htmlResponse += "</form>";

        String queryString = exchange.getRequestURI().getQuery();
        String searchParam = null;
        String searchType = "name"; // default search type
        if (queryString != null) {
            Map<String, String> queryParams = QueryStringParser.parse(queryString);
            searchParam = queryParams.get("search");
            searchType = queryParams.getOrDefault("searchType", "name");
        }

        htmlResponse += "<table>";
        if (data.isEmpty()) {
            htmlResponse += "<tr><td>No appointments</td></tr>";
        } else {
            // Split data into rows (each row is a line in the file)
            String[] rows = data.split("\n");
            for (String row : rows) {
                if (searchParam != null && !row.contains(searchParam)) {
                    continue; // skip row if search parameter not found
                }
                String[] columns = row.split(",");
                Appointment appointment = new Appointment(Integer.parseInt(columns[0]), columns[1], columns[2],
                        columns[3]);
                String searchColumn = "";
                switch (searchType) {
                    case "date":
                        searchColumn = appointment.getDate().toString(); // search by date
                        break;
                    case "time":
                        searchColumn = appointment.getTime(); // search by time
                        break;
                    default:
                        searchColumn = appointment.getName(); // search by name
                }
                if (searchParam != null && !searchColumn.contains(searchParam)) {
                    continue; // skip row if search parameter not found in selected column
                    // System.out.println("not found");
                }
                htmlResponse += "<tr>";
                for (String column : columns) {
                    htmlResponse += "<td>" + column + "</td>";
                }
                htmlResponse += "</tr>";
            }
        }
        htmlResponse += "</table>";
        htmlResponse += "<br><a href=\"/\">Back to home</a>";
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