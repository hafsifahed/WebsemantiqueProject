package esprit.tools;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ClientHandler implements HttpHandler {
    private static final String FUSEKI_SERVER_UPDATE_URL = "http://localhost:3030/projet/update";

    private String fusekiEndpoint; // URL de l'endpoint Fuseki
    private Gson gson = new Gson();
    private static final String ONTOLOGY_NAMESPACE = "http://www.semanticweb.org/hafsi/ontologies/2024/9/untitled-ontology-24#";

    public ClientHandler(String fusekiEndpoint) {
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

    // GET method to retrieve clients
    private void handleGet(HttpExchange exchange) throws IOException {
        String queryString = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> " +
                "SELECT ?client ?id ?nom ?numeroDeTelephone ?adresse ?prenom ?email " +
                "WHERE { " +
                "?client a ns:Client ; " +
                "ns:Id ?id ; " +
                "ns:nom ?nom ; " +
                "ns:NumeroDeTelephone ?numeroDeTelephone ; " +
                "ns:Adresse ?adresse ; " +
                "ns:prenom ?prenom ; " +
                "ns:email ?email ." +
                "}";


        String result = executeSparqlQuery(queryString);
        sendResponse(exchange, result, 200);
    }

    // POST method to create a new client

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

        String id = jsonObject.get("id").getAsString().trim();
        String nom = jsonObject.get("nom").getAsString().trim();
        String prenom = jsonObject.get("prenom").getAsString().trim();
        String motDePasse = jsonObject.get("motDePasse").getAsString().trim(); // Assuming motDePasse is needed
        String numeroDeTelephone = jsonObject.get("numeroDeTelephone").getAsString().trim();
        String adresse = jsonObject.get("adresse").getAsString().trim();
        String dateDeNaissance = jsonObject.get("dateDeNaissance").getAsString().trim();
        String email = jsonObject.get("email").getAsString().trim();
        String aPreferenceTransport = jsonObject.get("aPreferenceTransport").getAsString().trim(); // Assuming this is included

        String insertQuery = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> " +
                "INSERT DATA { " +
                "<" + ONTOLOGY_NAMESPACE + "Client" + id + "> a ns:Client . " +
                "<" + ONTOLOGY_NAMESPACE + "Client" + id + "> ns:Id \"" + id + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "Client" + id + "> ns:Nom \"" + nom + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "Client" + id + "> ns:prenom \"" + prenom + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "Client" + id + "> ns:NumeroDeTelephone \"" + numeroDeTelephone + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "Client" + id + "> ns:Adresse \"" + adresse + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "Client" + id + "> ns:DateDeNaissance \"" + dateDeNaissance + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "Client" + id + "> ns:email \"" + email + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "Client" + id + "> ns:aPreferenceTransport <" + ONTOLOGY_NAMESPACE + aPreferenceTransport + "> . " +
                "}";

        try {
            executeUpdateQuery(insertQuery);
            sendResponse(exchange, createResponse("Client created: " + nom), 201);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to create client: " + e.getMessage()), 500);
        }
    }



    public String executeUpdateQuery(String sparqlUpdate) {
        try {
            UpdateRequest updateRequest = UpdateFactory.create(sparqlUpdate);
            UpdateProcessor updateProcessor = UpdateExecutionFactory.createRemote(updateRequest, FUSEKI_SERVER_UPDATE_URL);
            updateProcessor.execute();
            return "Update executed successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error executing update: " + e.getMessage();
        }
    }


    // PUT method to update an existing client
    private void handlePut(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

        String id = jsonObject.get("id").getAsString().trim();
        String nom = jsonObject.get("nom").getAsString().trim();
        String prenom = jsonObject.get("prenom").getAsString().trim();
        String motDePasse = jsonObject.get("motDePasse").getAsString().trim();
        String numeroDeTelephone = jsonObject.get("numeroDeTelephone").getAsString().trim();
        String adresse = jsonObject.get("adresse").getAsString().trim();
        String dateDeNaissance = jsonObject.get("dateDeNaissance").getAsString().trim();
        String email = jsonObject.get("email").getAsString().trim();
        String aPreferenceTransport = jsonObject.get("aPreferenceTransport").getAsString().trim();

        String updateQuery = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> "
                + "DELETE { "
                + "<" + ONTOLOGY_NAMESPACE + "Client" + id + "> ns:Nom ?nom; "
                + "ns:prenom ?prenom; "
                + "ns:MotDePasse ?motDePasse; "
                + "ns:NumeroDeTelephone ?numero; "
                + "ns:Adresse ?adresse; "
                + "ns:DateDeNaissance ?date; "
                + "ns:email ?email; "
                + "ns:aPreferenceTransport ?transport. } "
                + "INSERT { "
                + "<" + ONTOLOGY_NAMESPACE + "Client" + id + "> ns:Nom \"" + nom + "\"; "
                + "ns:prenom \"" + prenom + "\"; "
                + "ns:MotDePasse \"" + motDePasse + "\"; "
                + "ns:NumeroDeTelephone \"" + numeroDeTelephone + "\"; "
                + "ns:Adresse \"" + adresse + "\"; "
                + "ns:DateDeNaissance \"" + dateDeNaissance + "\"; "
                + "ns:email \"" + email + "\"; "
                + "ns:aPreferenceTransport <" + ONTOLOGY_NAMESPACE + aPreferenceTransport + "> . } "
                + "WHERE { "
                + "<" + ONTOLOGY_NAMESPACE + "Client" + id + "> ns:Nom ?nom; "
                + "ns:prenom ?prenom; }";

        try {
            executeUpdate(updateQuery);
            sendResponse(exchange, createResponse("Client updated: " + nom), 200);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to update client: " + e.getMessage()), 500);
        }
    }



    // DELETE method to remove a client
    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String clientId = path.substring(path.lastIndexOf('/')+1); // Extracting the Client ID from URI

        String deleteQuery = 	"PREFIX ns: <"+ ONTOLOGY_NAMESPACE+"> DELETE { <"+ ONTOLOGY_NAMESPACE+"Client"+clientId+"> a ns:estUnClient . } WHERE { <"+ ONTOLOGY_NAMESPACE+"Client"+clientId+"> a ns:estUnClient . }";

        try {
            executeUpdate(deleteQuery);
            sendResponse(exchange, createResponse("Client deleted"), 200);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to delete client: "+ e.getMessage()), 500);
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