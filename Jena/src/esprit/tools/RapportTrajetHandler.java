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

public class RapportTrajetHandler implements HttpHandler {
    private String fusekiEndpoint; // URL de l'endpoint Fuseki
    private Gson gson = new Gson();
    private static final String ONTOLOGY_NAMESPACE = "http://www.semanticweb.org/hafsi/ontologies/2024/9/untitled-ontology-24#";

    public RapportTrajetHandler(String fusekiEndpoint) {
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

    // GET method to retrieve trip reports
    private void handleGet(HttpExchange exchange) throws IOException {
        String queryString = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> " +
                "SELECT ?rapport ?id ?dateEmission ?description ?montantEconomise ?quantiteEmise ?source ?nomMetro ?nomVelo " +
                "WHERE { " +
                "?rapport a ns:RapportTrajet ; " +
                "ns:Id ?id ; " +
                "ns:DateEmission ?dateEmission ; " +
                "ns:Description ?description ; " +
                "ns:MontantEconomise ?montantEconomise ; " +
                "ns:QuantiteEmise ?quantiteEmise ; " +
                "ns:Source ?source ; " +
                "ns:NomMetro ?nomMetro ; " +
                "ns:NomVelo ?nomVelo ." +
                "}";

        String result = executeSparqlQuery(queryString);
        sendResponse(exchange, result, 200);
    }

    // POST method to create a new trip report
    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

        String id = jsonObject.get("id").getAsString().trim();
        String dateEmission = jsonObject.get("dateEmission").getAsString().trim();
        String description = jsonObject.get("description").getAsString().trim();
        double montantEconomise = jsonObject.get("montantEconomise").getAsDouble();
        double quantiteEmise = jsonObject.get("quantiteEmise").getAsDouble();
        String source = jsonObject.get("source").getAsString().trim();
        String nomMetro = jsonObject.get("nomMetro").getAsString().trim();
        String nomVelo = jsonObject.get("nomVelo").getAsString().trim();

        String insertQuery = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> " +
                "INSERT DATA { " +
                "<" + ONTOLOGY_NAMESPACE + "RapportTrajet" + id + "> a ns:RapportTrajet . " +
                "<" + ONTOLOGY_NAMESPACE + "RapportTrajet" + id + "> ns:Id \"" + id + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "RapportTrajet" + id + "> ns:DateEmission \"" + dateEmission + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "RapportTrajet" + id + "> ns:Description \"" + description + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "RapportTrajet" + id + "> ns:MontantEconomise \"" + montantEconomise + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "RapportTrajet" + id + "> ns:QuantiteEmise \"" + quantiteEmise + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "RapportTrajet" + id + "> ns:Source \"" + source + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "RapportTrajet" + id + "> ns:NomMetro \"" + nomMetro + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "RapportTrajet" + id + "> ns:NomVelo \"" + nomVelo + "\" . }";

        try {
            executeUpdate(insertQuery);
            sendResponse(exchange, createResponse("Trip Report created: " + id), 201);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to create trip report: " + e.getMessage()), 500);
        }
    }

    // PUT method to update an existing trip report
    private void handlePut(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

        String id = jsonObject.get("id").getAsString().trim();
        String dateEmission = jsonObject.get("dateEmission").getAsString().trim();
        String description = jsonObject.get("description").getAsString().trim();
        double montantEconomise = jsonObject.get("montantEconomise").getAsDouble();
        double quantiteEmise = jsonObject.get("quantiteEmise").getAsDouble();
        String source = jsonObject.get("source").getAsString().trim();
        String nomMetro = jsonObject.get("nomMetro").getAsString().trim();
        String nomVelo = jsonObject.get("nomVelo").getAsString().trim();

        String updateQuery = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> "
                +"DELETE { "
                +"<"
                +" "+ ONTOLOGY_NAMESPACE
                +"RapportTrajet"
                +" "+id
                +" > ns:DateEmission ?dateEmission; "
                +"ns:Description ?description; "
                +"ns:MontantEconomise ?montantEconomise; "
                +"ns:QuantiteEmise ?quantiteEmise; "
                +"ns:Source ?source; "
                +"ns:NomMetro ?nomMetro; "
                +"ns:NomVelo ?nomVelo. } "
                +"INSERT { "
                +"<"
                +" "+ ONTOLOGY_NAMESPACE
                +"RapportTrajet"
                +" "+id
                +" > ns:DateEmission \""
                +" "+dateEmission
                +"\"; ns:Description \""
                +" "+description
                +"\"; ns:MontantEconomise \""
                +" "+montantEconomise
                +"\"; ns:QuantiteEmise \""
                +" "+quantiteEmise
                +"\"; ns:Source \""
                +" "+source
                +"\"; ns:NomMetro \""
                +" "+nomMetro
                +"\"; ns:NomVelo \""
                +" "+nomVelo
                +"\". } "
                +"WHERE { "
                +"<"
                +" "+ ONTOLOGY_NAMESPACE
                +"RapportTrajet"
                +" "+id
                +" > ns:DateEmission ?dateEmission; }";

        try {
            executeUpdate(updateQuery);
            sendResponse(exchange, createResponse("Trip Report updated: " + id), 200);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to update trip report: "+ e.getMessage()), 500);
        }
    }

    // DELETE method to remove a trip report
    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String rapportId = path.substring(path.lastIndexOf('/')+1); // Extracting the Report ID from URI

        String deleteQuery = 	"PREFIX ns: <"+ ONTOLOGY_NAMESPACE+"> DELETE { <"+ ONTOLOGY_NAMESPACE+"RapportTrajet"+rapportId+"> a ns:RappelTrajet . } WHERE { <"+ ONTOLOGY_NAMESPACE+"RapportTrajet"+rapportId+"> a ns:RappelTrajet . }";

        try {
            executeUpdate(deleteQuery);
            sendResponse(exchange, createResponse("Trip Report deleted"), 200);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to delete trip report: "+ e.getMessage()), 500);
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