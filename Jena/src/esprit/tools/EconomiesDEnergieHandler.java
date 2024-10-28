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

public class EconomiesDEnergieHandler implements HttpHandler {
    private String fusekiEndpoint; // URL de l'endpoint Fuseki
    private Gson gson = new Gson();
    private static final String ONTOLOGY_NAMESPACE = "http://www.semanticweb.org/hafsi/ontologies/2024/9/untitled-ontology-24#";

    public EconomiesDEnergieHandler(String fusekiEndpoint) {
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

    // GET method to retrieve energy savings records
    private void handleGet(HttpExchange exchange) throws IOException {
        String queryString = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> " +
                "SELECT ?economies ?id ?montantEconomise ?quantiteEconomise ?source ?dateEmission ?description " +
                "WHERE { " +
                "?economies a ns:EconomiesDEnergie ; " +
                "ns:Id ?id ; " +
                "ns:MontantEconomise ?montantEconomise ; " +
                "ns:QuantiteEconomise ?quantiteEconomise ; " +
                "ns:Source ?source ; " +
                "ns:DateEmission ?dateEmission ; " +
                "ns:Description ?description ." +
                "}";

        String result = executeSparqlQuery(queryString);
        sendResponse(exchange, result, 200);
    }

    // POST method to create a new energy savings record
    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

        String id = jsonObject.get("id").getAsString().trim();
        double montantEconomise = jsonObject.get("montantEconomise").getAsDouble();
        double quantiteEconomise = jsonObject.get("quantiteEconomise").getAsDouble();
        String source = jsonObject.get("source").getAsString().trim();
        String dateEmission = jsonObject.get("dateEmission").getAsString().trim();
        String description = jsonObject.get("description").getAsString().trim();

        String insertQuery = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> " +
                "INSERT DATA { " +
                "<" + ONTOLOGY_NAMESPACE + "EconomiesDEnergie" + id + "> a ns:EconomiesDEnergie . " +
                "<" + ONTOLOGY_NAMESPACE + "EconomiesDEnergie" + id + "> ns:Id \"" + id + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "EconomiesDEnergie" + id + "> ns:MontantEconomise \"" + montantEconomise + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "EconomiesDEnergie" + id + "> ns:QuantiteEconomise \"" + quantiteEconomise + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "EconomiesDEnergie" + id + "> ns:Source \"" + source + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "EconomiesDEnergie" + id + "> ns:DateEmission \"" + dateEmission + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "EconomiesDEnergie" + id + "> ns:Description \"" + description + "\" . }";

        try {
            executeUpdate(insertQuery);
            sendResponse(exchange, createResponse("Energy Savings Record created: " + id), 201);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to create energy savings record: " + e.getMessage()), 500);
        }
    }

    // PUT method to update an existing energy savings record
    private void handlePut(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

        String id = jsonObject.get("id").getAsString().trim();
        double montantEconomise = jsonObject.get("montantEconomise").getAsDouble();
        double quantiteEconomise = jsonObject.get("quantiteEconomise").getAsDouble();
        String source = jsonObject.get("source").getAsString().trim();
        String dateEmission = jsonObject.get("dateEmission").getAsString().trim();
        String description = jsonObject.get("description").getAsString().trim();

        String updateQuery = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> "
                +"DELETE { "
                +"<"
                +" "+ ONTOLOGY_NAMESPACE
                +"EconomiesDEnergie"
                +" "+id
                +" > ns:MontantEconomise ?montant; "
                +"ns:QuantiteEconomise ?quantite; "
                +"ns:Source ?source; "
                +"ns:DateEmission ?date; "
                +"ns:Description ?description. } "
                +"INSERT { "
                +"<"
                +" "+ ONTOLOGY_NAMESPACE
                +"EconomiesDEnergie"
                +" "+id
                +" > ns:MontantEconomise \""
                +" "+montantEconomise
                +"\"; ns:QuantiteEconomise \""
                +" "+quantiteEconomise
                +"\"; ns:Source \""
                +" "+source
                +"\"; ns:DateEmission \""
                +" "+dateEmission
                +"\"; ns:Description \""
                +" "+description
                +"\". } "
                +"WHERE { "
                +"<"
                +" "+ ONTOLOGY_NAMESPACE
                +"EconomiesDEnergie"
                +" "+id
                +" > ns:MontantEconomise ?montant; }";

        try {
            executeUpdate(updateQuery);
            sendResponse(exchange, createResponse("Energy Savings Record updated: " + id), 200);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to update energy savings record: "+ e.getMessage()), 500);
        }
    }

    // DELETE method to remove an energy savings record
    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String economiesId = path.substring(path.lastIndexOf('/')+1); // Extracting the Economies ID from URI

        String deleteQuery = 	"PREFIX ns: <"+ ONTOLOGY_NAMESPACE+"> DELETE { <"+ ONTOLOGY_NAMESPACE+"EconomiesDEnergie"+economiesId+"> a ns:EconomiesDEnergie . } WHERE { <"+ ONTOLOGY_NAMESPACE+"EconomiesDEnergie"+economiesId+"> a ns:EconomiesDEnergie . }";

        try {
            executeUpdate(deleteQuery);
            sendResponse(exchange, createResponse("Energy Savings Record deleted"), 200);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to delete energy savings record: "+ e.getMessage()), 500);
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