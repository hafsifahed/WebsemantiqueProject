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

public class VoiturePersonnelleHandler implements HttpHandler {
    private String fusekiEndpoint; // URL de l'endpoint Fuseki
    private Gson gson = new Gson();
    private static final String ONTOLOGY_NAMESPACE = "http://www.semanticweb.org/hafsi/ontologies/2024/9/untitled-ontology-24#";

    public VoiturePersonnelleHandler(String fusekiEndpoint) {
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

    // GET method to retrieve personal cars
    private void handleGet(HttpExchange exchange) throws IOException {
        String queryString = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> " +
                "SELECT ?voiture ?id ?marque ?modele ?couleur ?immatriculation ?annee ?consommation " +
                "WHERE { " +
                "?voiture a ns:VoiturePersonnelle ; " +
                "ns:Id ?id ; " +
                "ns:Marque ?marque ; " +
                "ns:Modele ?modele ; " +
                "ns:Couleur ?couleur ; " +
                "ns:Immatriculation ?immatriculation ; " +
                "ns:Annee ?annee ; " +
                "ns:Consommation ?consommation ." +
                "}";

        String result = executeSparqlQuery(queryString);
        sendResponse(exchange, result, 200);
    }

    // POST method to create a new personal car
    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

        String id = jsonObject.get("id").getAsString().trim();
        String marque = jsonObject.get("marque").getAsString().trim();
        String modele = jsonObject.get("modele").getAsString().trim();
        String couleur = jsonObject.get("couleur").getAsString().trim();
        String immatriculation = jsonObject.get("immatriculation").getAsString().trim();
        int annee = jsonObject.get("annee").getAsInt();
        double consommation = jsonObject.get("consommation").getAsDouble();

        String insertQuery = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> " +
                "INSERT DATA { " +
                "<" + ONTOLOGY_NAMESPACE + "VoiturePersonnelle" + id + "> a ns:VoiturePersonnelle . " +
                "<" + ONTOLOGY_NAMESPACE + "VoiturePersonnelle" + id + "> ns:Id \"" + id + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "VoiturePersonnelle" + id + "> ns:Marque \"" + marque + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "VoiturePersonnelle" + id + "> ns:Modele \"" + modele + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "VoiturePersonnelle" + id + "> ns:Couleur \"" + couleur + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "VoiturePersonnelle" + id + "> ns:Immatriculation \"" + immatriculation + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "VoiturePersonnelle" + id + "> ns:Annee \"" + annee + "\" . " +
                "<" + ONTOLOGY_NAMESPACE + "VoiturePersonnelle" + id + "> ns:Consommation \"" + consommation + "\" . }";

        try {
            executeUpdate(insertQuery);
            sendResponse(exchange, createResponse("Personal Car created: " + marque), 201);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to create personal car: " + e.getMessage()), 500);
        }
    }

    // PUT method to update an existing personal car
    private void handlePut(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

        String id = jsonObject.get("id").getAsString().trim();
        String marque = jsonObject.get("marque").getAsString().trim();
        String modele = jsonObject.get("modele").getAsString().trim();
        String couleur = jsonObject.get("couleur").getAsString().trim();
        String immatriculation = jsonObject.get("immatriculation").getAsString().trim();
        int annee = jsonObject.get("annee").getAsInt();
        double consommation = jsonObject.get("consommation").getAsDouble();

        String updateQuery = "PREFIX ns: <" + ONTOLOGY_NAMESPACE + "> "
                +"DELETE { "
                +"<"
                +" "+ ONTOLOGY_NAMESPACE
                +"VoiturePersonnelle"
                +" "+id
                +" > ns:Marque ?marque; "
                +"ns:Modele ?modele; "
                +"ns:Couleur ?couleur; "
                +"ns:Immatriculation ?immatriculation; "
                +"ns:Annee ?annee; "
                +"ns:Consommation ?consommation. } "
                +"INSERT { "
                +"<"
                +" "+ ONTOLOGY_NAMESPACE
                +"VoiturePersonnelle"
                +" "+id
                +" > ns:Marque \""
                +" "+marque
                +"\"; ns:Modele \""
                +" "+modele
                +"\"; ns:Couleur \""
                +" "+couleur
                +"\"; ns:Immatriculation \""
                +" "+immatriculation
                +"\"; ns:Annee \""
                +" "+annee
                +"\"; ns:Consommation \""
                +" "+consommation
                +"\". } "
                +"WHERE { "
                +"<"
                +" "+ ONTOLOGY_NAMESPACE
                +"VoiturePersonnelle"
                +" "+id
                +" > ns:Marque ?marque; }";

        try {
            executeUpdate(updateQuery);
            sendResponse(exchange, createResponse("Personal Car updated: " + marque), 200);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to update personal car: "+ e.getMessage()), 500);
        }
    }

    // DELETE method to remove a personal car
    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String voitureId = path.substring(path.lastIndexOf('/')+1); // Extracting the Car ID from URI

        String deleteQuery = 	"PREFIX ns: <"+ ONTOLOGY_NAMESPACE+"> DELETE { <"+ ONTOLOGY_NAMESPACE+"VoiturePersonnelle"+voitureId+"> a ns:VoiturePersonnelle . } WHERE { <"+ ONTOLOGY_NAMESPACE+"VoiturePersonnelle"+voitureId+"> a ns:VoiturePersonnelle . }";

        try {
            executeUpdate(deleteQuery);
            sendResponse(exchange, createResponse("Personal Car deleted"), 200);
        } catch (IOException e) {
            sendResponse(exchange, createResponse("Failed to delete personal car: "+ e.getMessage()), 500);
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