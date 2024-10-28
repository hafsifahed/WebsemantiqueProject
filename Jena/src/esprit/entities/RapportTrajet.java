package esprit.entities;

public class RapportTrajet {
    private String id;                       // Unique identifier for the trip report
    private String dateEmission;             // Date of emission of the report
    private String description;               // Description of the trip
    private double montantEconomise;         // Amount saved during the trip
    private double quantiteEmise;            // Quantity emitted during the trip
    private String source;                    // Source of the report
    private String nomMetro;                  // Name of the metro used (if applicable)
    private String nomVelo;                   // Name of the bike used (if applicable)

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateEmission() {
        return dateEmission;
    }

    public void setDateEmission(String dateEmission) {
        this.dateEmission = dateEmission;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getMontantEconomise() {
        return montantEconomise;
    }

    public void setMontantEconomise(double montantEconomise) {
        this.montantEconomise = montantEconomise;
    }

    public double getQuantiteEmise() {
        return quantiteEmise;
    }

    public void setQuantiteEmise(double quantiteEmise) {
        this.quantiteEmise = quantiteEmise;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getNomMetro() {
        return nomMetro;
    }

    public void setNomMetro(String nomMetro) {
        this.nomMetro = nomMetro;
    }

    public String getNomVelo() {
        return nomVelo;
    }

    public void setNomVelo(String nomVelo) {
        this.nomVelo = nomVelo;
    }
}