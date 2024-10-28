package esprit.entities;

public class RapportImpactEcologique {
    private String id;                       // Unique identifier for the ecological impact report
    private String dateEmission;             // Date of emission of the report
    private String description;               // Description of the ecological impact
    private double montantEconomise;         // Amount saved in ecological costs
    private double quantiteCO2Reduite;       // Quantity of CO2 reduced (e.g., in kg)
    private String source;                    // Source of the ecological impact information

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

    public double getQuantiteCO2Reduite() {
        return quantiteCO2Reduite;
    }

    public void setQuantiteCO2Reduite(double quantiteCO2Reduite) {
        this.quantiteCO2Reduite = quantiteCO2Reduite;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}