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

public class AdminHandler implements HttpHandler {
    private String fusekiEndpoint; // URL de l'endpoint Fuseki
    private Gson gson = new Gson();
    private static final String ONTOLOGY_NAMESPACE = "http://www.semanticweb.org/hafsi/ontologies/2024/9/untitled-ontology-24#";

    public AdminHandler(String fusekiEndpoint) {
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

    // GET method to retrieve admins


    private void handleGet(HttpExchange exchange) throws IOException {
        String queryString = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> " +
                "SELECT ?admin ?id ?nom ?email " +
                "WHERE { " +
                "?admin a ns:Admin ; " +
                "ns:Id ?id ; " +
                "ns:nom ?nom ; " +
                "ns:email ?email ." +
                "}";

        String result = executeSparqlQuery(queryString);
        sendResponse(exchange, result, 200);
    }


    // POST method to create a new admin
    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

        String id = jsonObject.get("id").getAsString().trim();
        String nom = jsonObject.get("nom").getAsString().trim();
        String email = jsonObject.get("email").getAsString().trim();
        String motDePasse = jsonObject.get("motDePasse").getAsString().trim();
        String numeroDeTelephone = jsonObject.get("numeroDeTelephone").getAsString().trim();

        String insertQuery = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> " +
                "INSERT DATA { " +
                "<" + ONTOLOGY_NAMESPACE + "Admin" + id + "> a ns:Admin . " +
                "<" + ONTOLOGY_NAMESPACE + "Admin" + id + "> ns:Id \"" + id + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "Admin" + id + "> ns:Nom \"" + nom + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "Admin" + id + "> ns:Email \"" + email + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "Admin" + id + "> ns:MotDePasse \"" + motDePasse + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "Admin" + id + "> ns:NuméroDeTéléphone \"" + numeroDeTelephone + "\" . }";

        try {
            executeUpdate(insertQuery);
            sendResponse(exchange, createResponse("Admin created: " + nom), 201);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to create admin: " + e.getMessage()), 500);
        }
    }

    // PUT method to update an existing admin
    private void handlePut(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

        String id = jsonObject.get("id").getAsString().trim();
        String nom = jsonObject.get("nom").getAsString().trim();
        String email = jsonObject.get("email").getAsString().trim();
        String motDePasse = jsonObject.get("motDePasse").getAsString().trim();
        String numeroDeTelephone = jsonObject.get("numeroDeTelephone").getAsString().trim();

        String updateQuery = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> "
                +"DELETE { "
                +"<"
                +" "+ ONTOLOGY_NAMESPACE
                +"Admin"
                +" "+id
                +" > ns:Nom ?nom; "
                +"ns:Email ?email; "
                +"ns:MotDePasse ?motDePasse; "
                +"ns:NuméroDeTéléphone ?numeroDeTelephone. } "
                +"INSERT { "
                +"<"
                +" "+ ONTOLOGY_NAMESPACE
                +"Admin"
                +" "+id
                +" > ns:Nom \""
                +" "+nom
                +"\"; ns:Email \""
                +" "+email
                +"\"; ns:MotDePasse \""
                +" "+motDePasse
                +"\"; ns:NuméroDeTéléphone \""
                +" "+numeroDeTelephone
                +"\". } "
                +"WHERE { "
                +"<"
                +" "+ ONTOLOGY_NAMESPACE
                +"Admin"
                +" "+id
                +" > ns:Nom ?nom; }";

        try {
            executeUpdate(updateQuery);
            sendResponse(exchange, createResponse("Admin updated: " + nom), 200);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to update admin: "+ e.getMessage()), 500);
        }
    }

    // DELETE method to remove an admin
    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String adminId = path.substring(path.lastIndexOf('/')+1); // Extracting the Admin ID from URI

        String deleteQuery = 	"PREFIX ns: <"+ ONTOLOGY_NAMESPACE+"> DELETE { <"+ ONTOLOGY_NAMESPACE+"Admin"+adminId+"> a ns:Admin . } WHERE { <"+ ONTOLOGY_NAMESPACE+"Admin"+adminId+"> a ns:Admin . }";

        try {
            executeUpdate(deleteQuery);
            sendResponse(exchange, createResponse("Admin deleted"), 200);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to delete admin: "+ e.getMessage()), 500);
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