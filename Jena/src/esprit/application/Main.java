package esprit.application;

import esprit.tools.AdminHandler;
import esprit.tools.BusHandler;
import esprit.tools.ClientHandler;
import esprit.tools.CovoitureurHandler;
import esprit.tools.EconomiesDEnergieHandler;
import esprit.tools.EmissionsDeCO2Handler;
import esprit.tools.MetroHandler;
import esprit.tools.RapportImpactEcologiqueHandler;
import esprit.tools.RapportTrajetHandler;
import esprit.tools.ReductionDePollutionHandler;
import esprit.tools.StationDeBusHandler;
import esprit.tools.StationDeMetroHandler;
import esprit.tools.StationTramwayHandler;
import esprit.tools.TrajetLongueDistanceHandler;
import esprit.tools.TrajetPartageHandler;
import esprit.tools.VeloHandler;
import esprit.tools.VoiturePersonnelleHandler;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) {
        try {
            String fusekiEndpoint = "http://localhost:3030/projet"; // Change this URL to your Fuseki endpoint


            HttpServer server = HttpServer.create(new InetSocketAddress(9000), 0);
            server.createContext("/admins", new AdminHandler(fusekiEndpoint));
            server.createContext("/buses", new BusHandler(fusekiEndpoint));
            server.createContext("/clients", new ClientHandler(fusekiEndpoint));
            server.createContext("/covoitureurs", new CovoitureurHandler(fusekiEndpoint));
            server.createContext("/economies", new EconomiesDEnergieHandler(fusekiEndpoint));
            server.createContext("/emissions", new EmissionsDeCO2Handler(fusekiEndpoint));
            server.createContext("/metros", new MetroHandler(fusekiEndpoint));
            server.createContext("/rapports-impact-ecologique", new RapportImpactEcologiqueHandler(fusekiEndpoint));
            server.createContext("/rapports-trajet", new RapportTrajetHandler(fusekiEndpoint));
            server.createContext("/reductions-pollution", new ReductionDePollutionHandler(fusekiEndpoint));
            server.createContext("/stations-bus", new StationDeBusHandler(fusekiEndpoint));
            server.createContext("/stations-metro", new StationDeMetroHandler(fusekiEndpoint));
            server.createContext("/stations-tramway", new StationTramwayHandler(fusekiEndpoint));
            server.createContext("/trajets-longue-distance", new TrajetLongueDistanceHandler(fusekiEndpoint));
            server.createContext("/trajets-partage", new TrajetPartageHandler(fusekiEndpoint));
            server.createContext("/velos", new VeloHandler(fusekiEndpoint));
            server.createContext("/voitures-personnelles", new VoiturePersonnelleHandler(fusekiEndpoint));

            server.setExecutor(null); // Use the default executor
            server.start();
            System.out.println("Server is running on port 9000");
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }
}