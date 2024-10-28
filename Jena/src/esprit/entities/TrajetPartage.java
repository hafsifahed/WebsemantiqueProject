package esprit.entities;

public class TrajetPartage {
    private String id;                       // Unique identifier for the shared trip
    private String pointDeDepart;            // Departure point
    private String pointDArrivee;            // Arrival point
    private String heureDeDepart;            // Departure time
    private String heureDArrivee;            // Arrival time
    private double distanceEnKm;             // Distance in kilometers
    private String description;               // Description of the shared trip
    private String covoitureurId;            // ID of the carpooler

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPointDeDepart() {
        return pointDeDepart;
    }

    public void setPointDeDepart(String pointDeDepart) {
        this.pointDeDepart = pointDeDepart;
    }

    public String getPointDArrivee() {
        return pointDArrivee;
    }

    public void setPointDArrivee(String pointDArrivee) {
        this.pointDArrivee = pointDArrivee;
    }

    public String getHeureDeDepart() {
        return heureDeDepart;
    }

    public void setHeureDeDepart(String heureDeDepart) {
        this.heureDeDepart = heureDeDepart;
    }

    public String getHeureDArrivee() {
        return heureDArrivee;
    }

    public void setHeureDArrivee(String heureDArrivee) {
        this.heureDArrivee = heureDArrivee;
    }

    public double getDistanceEnKm() {
        return distanceEnKm;
    }

    public void setDistanceEnKm(double distanceEnKm) {
        this.distanceEnKm = distanceEnKm;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCovoitureurId() {
        return covoitureurId;
    }

    public void setCovoitureurId(String covoitureurId) {
        this.covoitureurId = covoitureurId;
    }
}