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

public class MetroHandler implements HttpHandler {
    private String fusekiEndpoint; // URL de l'endpoint Fuseki
    private Gson gson = new Gson();
    private static final String ONTOLOGY_NAMESPACE = "http://www.semanticweb.org/hafsi/ontologies/2024/9/untitled-ontology-24#";

    public MetroHandler(String fusekiEndpoint) {
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

    // GET method to retrieve metros
    private void handleGet(HttpExchange exchange) throws IOException {
        String queryString = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> " +
                "SELECT ?metro ?id ?nomMetro ?horairesOuverture ?capaciteMax " +
                "WHERE { " +
                "?metro a ns:Metro ; " +
                "ns:Id ?id ; " +
                "ns:NomMetro ?nomMetro ; " +
                "ns:HorairesOuverture ?horairesOuverture ; " +
                "ns:CapaciteMax ?capaciteMax ." +
                "}";

        String result = executeSparqlQuery(queryString);
        sendResponse(exchange, result, 200);
    }



    // POST method to create a new metro
    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

        String id = jsonObject.get("id").getAsString().trim();
        String nomMetro = jsonObject.get("nomMetro").getAsString().trim();
        String horairesOuverture = jsonObject.get("horairesOuverture").getAsString().trim();
        int capaciteMax = jsonObject.get("capaciteMax").getAsInt();

        String insertQuery = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> " +
                "INSERT DATA { " +
                "<" + ONTOLOGY_NAMESPACE + "Metro" + id + "> a ns:Metro . " +
                "<" + ONTOLOGY_NAMESPACE + "Metro" + id + "> ns:Id \"" + id + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "Metro" + id + "> ns:NomMetro \"" + nomMetro + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "Metro" + id + "> ns:HorairesOuverture \"" + horairesOuverture + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "Metro" + id + "> ns:CapaciteMax \"" + capaciteMax + "\" . }";

        try {
            executeUpdate(insertQuery);
            sendResponse(exchange, createResponse("Metro created: " + nomMetro), 201);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to create metro: " + e.getMessage()), 500);
        }
    }


    // PUT method to update an existing metro
    private void handlePut(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

        String id = jsonObject.get("id").getAsString().trim();
        String nomMetro = jsonObject.get("nomMetro").getAsString().trim();
        String ligne = jsonObject.get("ligne").getAsString().trim();
        String couleur = jsonObject.get("couleur").getAsString().trim();
        String horairesOuverture = jsonObject.get("horairesOuverture").getAsString().trim();
        String horairesFermeture = jsonObject.get("horairesFermeture").getAsString().trim();
        int capaciteMax = jsonObject.get("capaciteMax").getAsInt();

        String updateQuery = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> "
                +"DELETE { "
                +"<"
                +" "+ ONTOLOGY_NAMESPACE
                +"Metro"
                +" "+id
                +" > ns:NomMetro ?nomMetro; "
                +"ns:Ligne ?ligne; "
                +"ns:Couleur ?couleur; "
                +"ns:HorairesOuverture ?horairesOuverture; "
                +"ns:HorairesFermeture ?horairesFermeture; "
                +"ns:CapacitéMax ?capaciteMax. } "
                +"INSERT { "
                +"<"
                +" "+ ONTOLOGY_NAMESPACE
                +"Metro"
                +" "+id
                +" > ns:NomMetro \""
                +" "+nomMetro
                +"\"; ns:Ligne \""
                +" "+ligne
                +"\"; ns:Couleur \""
                +" "+couleur
                +"\"; ns:HorairesOuverture \""
                +" "+horairesOuverture
                +"\"; ns:HorairesFermeture \""
                +" "+horairesFermeture
                +"\"; ns:CapacitéMax \""
                +" "+capaciteMax
                +"\". } "
                +"WHERE { "
                +"<"
                +" "+ ONTOLOGY_NAMESPACE
                +"Metro"
                +" "+id
                +" > ns:NomMetro ?nomMetro; }";

        try {
            executeUpdate(updateQuery);
            sendResponse(exchange, createResponse("Metro updated: " + nomMetro), 200);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to update metro: "+ e.getMessage()), 500);
        }
    }

    // DELETE method to remove a metro
    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String metroId = path.substring(path.lastIndexOf('/')+1); // Extracting the Metro ID from URI

        String deleteQuery = 	"PREFIX ns: <"+ ONTOLOGY_NAMESPACE+"> DELETE { <"+ ONTOLOGY_NAMESPACE+"Metro"+metroId+"> a ns:Metro . } WHERE { <"+ ONTOLOGY_NAMESPACE+"Metro"+metroId+"> a ns:Metro . }";

        try {
            executeUpdate(deleteQuery);
            sendResponse(exchange, createResponse("Metro deleted"), 200);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to delete metro: "+ e.getMessage()), 500);
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