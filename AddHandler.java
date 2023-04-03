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
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import com.sun.net.httpserver.HttpExchange;

public class AddHandler extends RequestHandler {
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
        String htmlResponse = "<html>";
        htmlResponse += "<head><title>Add Appointment</title>";
        htmlResponse += "<style>a{color:#162533;text-decoration:none;}a:hover{color:#162533;text-decoration:underline}body{font-family:Arial,Helvetica,sans-serif;padding:20px}h1{color:#34495e;text-align:center}form{border:3px solid #f1f1f1;width:50%;margin:0 auto;padding:20px}input[type=text],input[type=date],input[type=time]{width:100%;padding:12px 20px;margin:8px 0;display:inline-block;border:1px solid #ccc;box-sizing:border-box}input[type=submit]{background-color:#34495e;color:#fff;padding:14px 20px;margin:8px 0;border:none;cursor:pointer;width:100%}input[type=submit]:hover{opacity:.8}</style>";
        htmlResponse += "</head>";
        htmlResponse += "<body><h1>Add Appointment</h1>";
        htmlResponse += "<form action=\"/add\" method=\"POST\">";
        htmlResponse += "<label for=\"name\">Name</label><br>";
        htmlResponse += "<input type=\"text\" name=\"name\" id=\"name\" /><br><br>";
        htmlResponse += "<label for=\"date\">Date</label><br>";
        htmlResponse += "<input type=\"date\" name=\"date\" id=\"date\" /><br><br>";
        htmlResponse += "<label for=\"time\">Time</label><br>";
        htmlResponse += "<input type=\"time\" name=\"time\" id=\"time\" value=\"12:00\" /><br><br>";
        htmlResponse += "<input type=\"submit\" value=\"Add\" />";
        htmlResponse += "<br><a href=\"/\">Back to home</a>";
        htmlResponse += "</form>";
        htmlResponse += "</body></html>";
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, htmlResponse.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(htmlResponse.getBytes());
        os.close();
    }

    protected void handlePostRequest(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String[] fields = requestBody.split("&");
        int id = getId();
        String name = fields[0].split("=")[1];
        String date = fields[1].split("=")[1];
        String time = fields[2].split("=")[1];
        String decoded = URLDecoder.decode(time, "UTF-8");
        String newData = id + "," + name + "," + date + "," + decoded + "\n";
        writeToFile(newData, FILE_PATH, true);
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

    private int getId() throws IOException {
        int count = 0;
        BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
        while (reader.readLine() != null) count++;
        reader.close();
        return ++count;

    }

}