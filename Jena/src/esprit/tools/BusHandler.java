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

public class BusHandler implements HttpHandler {
    private String fusekiEndpoint; // URL de l'endpoint Fuseki
    private Gson gson = new Gson();
    private static final String ONTOLOGY_NAMESPACE = "http://www.semanticweb.org/hafsi/ontologies/2024/9/untitled-ontology-24#";

    public BusHandler(String fusekiEndpoint) {
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

    // GET method to retrieve buses
    private void handleGet(HttpExchange exchange) throws IOException {
        String queryString = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> " +
                "SELECT ?bus ?id ?marque ?modele ?immatriculation ?capacite ?horairesOuverture ?horairesFermeture " +
                "WHERE { " +
                "?bus a ns:Bus ; " +
                "ns:Id ?id ; " +
                "ns:Marque ?marque ; " +
                "ns:Modele ?modele ; " +
                "ns:Immatriculation ?immatriculation ; " +
                "ns:Capacité ?capacite ; " +
                "ns:HorairesOuverture ?horairesOuverture ; " +
                "ns:HorairesFermeture ?horairesFermeture ." +
                "}";

        String result = executeSparqlQuery(queryString);
        sendResponse(exchange, result, 200);
    }

    // POST method to create a new bus
    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

        String id = jsonObject.get("id").getAsString().trim();
        String marque = jsonObject.get("marque").getAsString().trim();
        String modele = jsonObject.get("modele").getAsString().trim();
        String immatriculation = jsonObject.get("immatriculation").getAsString().trim();
        int capacite = jsonObject.get("capacite").getAsInt();
        String horairesOuverture = jsonObject.get("horairesOuverture").getAsString().trim();
        String horairesFermeture = jsonObject.get("horairesFermeture").getAsString().trim();

        String insertQuery = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> " +
                "INSERT DATA { " +
                "<" + ONTOLOGY_NAMESPACE + "Bus" + id + "> a ns:Bus . " +
                "<" + ONTOLOGY_NAMESPACE + "Bus" + id + "> ns:Id \"" + id + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "Bus" + id + "> ns:Marque \"" + marque + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "Bus" + id + "> ns:Modele \"" + modele + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "Bus" + id + "> ns:Immatriculation \"" + immatriculation + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "Bus" + id + "> ns:Capacité \"" + capacite + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "Bus" + id + "> ns:HorairesOuverture \"" + horairesOuverture + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "Bus" + id + "> ns:HorairesFermeture \"" + horairesFermeture + "\" . }";

        try {
            executeUpdate(insertQuery);
            sendResponse(exchange, createResponse("Bus created: " + marque), 201);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to create bus: " + e.getMessage()), 500);
        }
    }

    // PUT method to update an existing bus
    private void handlePut(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

        String id = jsonObject.get("id").getAsString().trim();
        String marque = jsonObject.get("marque").getAsString().trim();
        String modele = jsonObject.get("modele").getAsString().trim();
        String immatriculation = jsonObject.get("immatriculation").getAsString().trim();
        int capacite = jsonObject.get("capacite").getAsInt();
        String horairesOuverture = jsonObject.get("horairesOuverture").getAsString().trim();
        String horairesFermeture = jsonObject.get("horairesFermeture").getAsString().trim();

        String updateQuery = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> "
                +"DELETE { "
                +"<"
                +" "+ ONTOLOGY_NAMESPACE
                +"Bus"
                +" "+id
                +" > ns:Marque ?marque; "
                +"ns:Modele ?modele; "
                +"ns:Immatriculation ?immatriculation; "
                +"ns:Capacité ?capacite; "
                +"ns:HorairesOuverture ?horairesOuverture; "
                +"ns:HorairesFermeture ?horairesFermeture. } "
                +"INSERT { "
                +"<"
                +" "+ ONTOLOGY_NAMESPACE
                +"Bus"
                +" "+id
                +" > ns:Marque \""
                +" "+marque
                +"\"; ns:Modele \""
                +" "+modele
                +"\"; ns:Immatriculation \""
                +" "+immatriculation
                +"\"; ns:Capacité \""
                +" "+capacite
                +"\"; ns:HorairesOuverture \""
                +" "+horairesOuverture
                +"\"; ns:HorairesFermeture \""
                +" "+horairesFermeture
                +"\". } "
                +"WHERE { "
                +"<"
                +" "+ ONTOLOGY_NAMESPACE
                +"Bus"
                +" "+id
                +" > ns:Marque ?marque; }";

        try {
            executeUpdate(updateQuery);
            sendResponse(exchange, createResponse("Bus updated: " + marque), 200);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to update bus: "+ e.getMessage()), 500);
        }
    }

    // DELETE method to remove a bus
    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String busId = path.substring(path.lastIndexOf('/')+1); // Extracting the Bus ID from URI

        String deleteQuery = 	"PREFIX ns: <"+ ONTOLOGY_NAMESPACE+"> DELETE { <"+ ONTOLOGY_NAMESPACE+"Bus"+busId+"> a ns:Bus . } WHERE { <"+ ONTOLOGY_NAMESPACE+"Bus"+busId+"> a ns:Bus . }";

        try {
            executeUpdate(deleteQuery);
            sendResponse(exchange, createResponse("Bus deleted"), 200);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to delete bus: "+ e.getMessage()), 500);
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