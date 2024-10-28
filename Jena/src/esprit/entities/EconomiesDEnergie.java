package esprit.entities;

public class EconomiesDEnergie {
    private String id;                       // Unique identifier for the energy savings record
    private double montantEconomise;         // Amount saved in energy costs
    private double quantiteEconomise;        // Quantity of energy saved (e.g., in kWh)
    private String source;                    // Source of the energy savings information
    private String dateEmission;              // Date of emission of the record
    private String description;               // Description of the energy savings

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

    public double getQuantiteEconomise() {
        return quantiteEconomise;
    }

    public void setQuantiteEconomise(double quantiteEconomise) {
        this.quantiteEconomise = quantiteEconomise;
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