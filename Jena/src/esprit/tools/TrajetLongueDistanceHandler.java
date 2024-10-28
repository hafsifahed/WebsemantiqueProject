package esprit.tools;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TrajetLongueDistanceHandler implements HttpHandler {
    private String fusekiEndpoint; // URL de l'endpoint Fuseki
    private Gson gson = new Gson();
    private static final String ONTOLOGY_NAMESPACE = "http://www.semanticweb.org/hafsi/ontologies/2024/9/untitled-ontology-24#";

    public TrajetLongueDistanceHandler(String fusekiEndpoint) {
        this.fusekiEndpoint = fusekiEndpoint; // Initialiser l'endpoint Fuseki
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "PUT":
                handlePut(exchange);
                break;
            case "DELETE":
                handleDelete(exchange);
                break;
            default:
                sendResponse(exchange, createResponse("Unsupported method."), 405);
        }
    }

    // GET method to retrieve long-distance trips
    private void handleGet(HttpExchange exchange) throws IOException {
        String queryString = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> " +
                "SELECT ?trajet ?id ?pointDeDepart ?pointDArrivee ?heureDeDepart ?heureDArrivee ?distanceEnKm ?description " +
                "WHERE { " +
                "?trajet a ns:TrajetLongueDistance ; " +
                "ns:Id ?id ; " +
                "ns:PointDeDépart ?pointDeDepart ; " +
                "ns:PointD'Arrivée ?pointDArrivee ; " +
                "ns:HeureDeDépart ?heureDeDepart ; " +
                "ns:HeureD'Arrivée ?heureDArrivee ; " +
                "ns:DistanceEnKm ?distanceEnKm ; " +
                "ns:Description ?description ." +
                "}";

        String result = executeSparqlQuery(queryString);
        sendResponse(exchange, result, 200);
    }

    // POST method to create a new long-distance trip
    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

        String id = jsonObject.get("id").getAsString().trim();
        String pointDeDepart = jsonObject.get("pointDeDepart").getAsString().trim();
        String pointDArrivee = jsonObject.get("pointDArrivee").getAsString().trim();
        String heureDeDepart = jsonObject.get("heureDeDepart").getAsString().trim();
        String heureDArrivee = jsonObject.get("heureDArrivee").getAsString().trim();
        double distanceEnKm = jsonObject.get("distanceEnKm").getAsDouble();
        String description = jsonObject.get("description").getAsString().trim();

        String insertQuery = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> " +
                "INSERT DATA { " +
                "<" + ONTOLOGY_NAMESPACE + "TrajetLongueDistance" + id + "> a ns:TrajetLongueDistance . " +
                "<" + ONTOLOGY_NAMESPACE + "TrajetLongueDistance" + id + "> ns:Id \"" + id + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "TrajetLongueDistance" + id + "> ns:PointDeDépart \"" + pointDeDepart + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "TrajetLongueDistance" + id + "> ns:PointD'Arrivée \"" + pointDArrivee + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "TrajetLongueDistance" + id + "> ns:HeureDeDépart \"" + heureDeDepart + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "TrajetLongueDistance" + id + "> ns:HeureD'Arrivée \"" + heureDArrivee + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "TrajetLongueDistance" + id + "> ns:DistanceEnKm \"" + distanceEnKm + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "TrajetLongueDistance" + id + "> ns:Description \"" + description + "\" . }";

        try {
            executeUpdate(insertQuery);
            sendResponse(exchange, createResponse("Trajet Longue Distance created: " + id), 201);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to create trajet longue distance: " + e.getMessage()), 500);
        }
    }

    // PUT method to update an existing long-distance trip
    private void handlePut(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

        String id = jsonObject.get("id").getAsString().trim();
        String pointDeDepart = jsonObject.get("pointDeDepart").getAsString().trim();
        String pointDArrivee = jsonObject.get("pointDArrivee").getAsString().trim();
        String heureDeDepart = jsonObject.get("heureDeDepart").getAsString().trim();
        String heureDArrivee = jsonObject.get("heureDArrivee").getAsString().trim();
        double distanceEnKm = jsonObject.get("distanceEnKm").getAsDouble();
        String description = jsonObject.get("description").getAsString().trim();

        String updateQuery = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> "
                +"DELETE { "
                +"<"
                +" "+ ONTOLOGY_NAMESPACE
                +"TrajetLongueDistance"
                +" "+id
                +" > ns:PointDeDépart ?pointDeDepart; "
                +"ns:PointD'Arrivée ?pointDArrivee; "
                +"ns:HeureDeDépart ?heureDeDepart; "
                +"ns:HeureD'Arrivée ?heureDArrivee; "
                +"ns:DistanceEnKm ?distanceEnKm; "
                +"ns:Description ?description. } "
                +"INSERT { "
                +"<"
                +" "+ ONTOLOGY_NAMESPACE
                +"TrajetLongueDistance"
                +" "+id
                +" > ns:PointDeDépart \""
                +" "+pointDeDepart
                +"\"; ns:PointD'Arrivée \""
                +" "+pointDArrivee
                +"\"; ns:HeureDeDépart \""
                +" "+heureDeDepart
                +"\"; ns:HeureD'Arrivée \""
                +" "+heureDArrivee
                +"\"; ns:DistanceEnKm \""
                +" "+distanceEnKm
                +"\"; ns:Description \""
                +" "+description
                +"\". } "
                +"WHERE { "
                +"<"
                +" "+ ONTOLOGY_NAMESPACE
                +"TrajetLongueDistance"
                +" "+id
                +" > ns:PointDeDépart ?pointDeDepart; }";

        try {
            executeUpdate(updateQuery);
            sendResponse(exchange, createResponse("Trajet Longue Distance updated: " + id), 200);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to update trajet longue distance: "+ e.getMessage()), 500);
        }
    }

    // DELETE method to remove a long-distance trip
    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String trajetId = path.substring(path.lastIndexOf('/')+1); // Extracting the Trajet Longue Distance ID from URI

        String deleteQuery = 	"PREFIX ns: <"+ ONTOLOGY_NAMESPACE+"> DELETE { <"+ ONTOLOGY_NAMESPACE+"TrajetLongueDistance"+trajetId+"> a ns:TrajetLongueDistance . } WHERE { <"+ ONTOLOGY_NAMESPACE+"TrajetLongueDistance"+trajetId+"> a ns:TrajetLongueDistance . }";

        try {
            executeUpdate(deleteQuery);
            sendResponse(exchange, createResponse("Trajet Longue Distance deleted"), 200);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to delete trajet longue distance: "+ e.getMessage()), 500);
        }
    }

    private String executeSparqlQuery(String query) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(fusekiEndpoint).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/sparql-query");

        conn.getOutputStream().write(query.getBytes(StandardCharsets.UTF_8));

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        }
    }

    private void executeUpdate(String update) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(fusekiEndpoint).openConnection();

        conn.setRequestMethod("POST");

        conn.setDoOutput(true);

        conn.setRequestProperty("Content-Type", "application/sparql-update");

        conn.getOutputStream().write(update.getBytes(StandardCharsets.UTF_8));

        conn.getResponseCode(); // Executes the query
    }

    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length());

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    private String createResponse(String message) {
        return gson.toJson(Map.of("message", message));
    }
}