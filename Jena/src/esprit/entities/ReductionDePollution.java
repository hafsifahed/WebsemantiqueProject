package esprit.entities;

public class ReductionDePollution {
    private String id;                       // Unique identifier for the pollution reduction record
    private double montantEconomise;         // Amount saved in pollution costs
    private double quantitePollutionReduite; // Quantity of pollution reduced (e.g., in kg)
    private String source;                    // Source of the pollution reduction information
    private String dateEmission;              // Date of emission of the record
    private String description;               // Description of the pollution reduction

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getMontantEconomise() {
        return montantEconomise;
    }

    public void setMontantEconomise(double montantEconomise) {
        this.montantEconomise = montantEconomise;
    }

    public double getQuantitePollutionReduite() {
        return quantitePollutionReduite;
    }

    public void setQuantitePollutionReduite(double quantitePollutionReduite) {
        this.quantitePollutionReduite = quantitePollutionReduite;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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
}