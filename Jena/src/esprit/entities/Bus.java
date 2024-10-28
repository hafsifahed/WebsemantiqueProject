package esprit.entities;

public class Bus {
    private String id;                       // Unique identifier for the bus
    private String marque;                   // Brand of the bus
    private String modele;                   // Model of the bus
    private String immatriculation;          // License plate number
    private int capacite;                    // Seating capacity of the bus
    private String horairesOuverture;        // Opening hours for bus service
    private String horairesFermeture;        // Closing hours for bus service

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public String getHorairesOuverture() {
        return horairesOuverture;
    }

    public void setHorairesOuverture(String horairesOuverture) {
        this.horairesOuverture = horairesOuverture;
    }

    public String getHorairesFermeture() {
        return horairesFermeture;
    }

    public void setHorairesFermeture(String horairesFermeture) {
        this.horairesFermeture = horairesFermeture;
    }
}